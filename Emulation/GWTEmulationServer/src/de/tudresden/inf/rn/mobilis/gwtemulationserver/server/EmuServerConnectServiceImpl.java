package de.tudresden.inf.rn.mobilis.gwtemulationserver.server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.tudresden.inf.rn.mobilis.gwtemulationserver.client.EmuServerConnectService;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.CommandType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.InstanceGroupType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.InstanceType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.Script;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.utils.EmulationConnection;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.utils.EmulationSession;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.utils.EmulationStatus;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.utils.SessionManager;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.shared.InstanceGroupExecutorInfo;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.shared.InstanceGroupInfo;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.shared.ScriptInfo;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.shared.SessionList;

public class EmuServerConnectServiceImpl extends RemoteServiceServlet implements EmuServerConnectService {

	private String TAG = "EmulationServerService";
	private SessionManager sessionManager = new SessionManager();
	private EmulationConnection connection = new EmulationConnection();
	
	private EntityManagerFactory emf = Persistence.createEntityManagerFactory("emulationserver");
	private EntityManager em = emf.createEntityManager();
	
	@Override
	public void init() throws ServletException {
		//Connection.DEBUG_ENABLED = true;
		connection.connect();
		super.init();
	}

	@Override
	public void destroy() {
		connection.disconnect();
		em.close();
		super.destroy();
	}

	@Override
	public Boolean sendCommand(String cmd) {
		
		return false;
		
	}

	@Override
	public List<String> getDeviceList() {
		
		List<String> devices = connection.getDeviceList();
		return devices;
		
	}
	
	@Override
	public List<String> getScriptList() {
		
		String scriptPath = getServletContext().getRealPath("skripte");
		File f = new File(scriptPath);
		String[] fArray = f.list();
		List<String> scripts = new ArrayList<String>();
		
		if(fArray == null) return scripts;
				
		for(String s:fArray) {
			scripts.add(s);
		}
		
		return scripts;
		
	}

	@Override
	public ScriptInfo getNeededDevices(String script) {
		
		ScriptInfo neededDevices = null;
		String scriptPath = getServletContext().getRealPath("skripte") + File.separator + script;
		
		File f = new File(scriptPath);
		if(f.exists()) {
			if(f.isFile()) {
				neededDevices = new ScriptInfo();
				XMLScriptParser parser = new XMLScriptParser();
				Script parsedScript = parser.parse(f);
				for (int i = 0; i < parsedScript.getCommand().size(); i++) {
					CommandType command = parsedScript.getCommand().get(i).getValue();
					if (command instanceof InstanceType) {
						neededDevices.addInstance(((InstanceType)command).getVarName());
					} else if (command instanceof InstanceGroupType) {
						InstanceGroupType instanceGroup = (InstanceGroupType) command;
						neededDevices.addInstanceGroup(instanceGroup.getVarName(), new InstanceGroupInfo(instanceGroup.getVarName(), instanceGroup.getInstanceCount(), instanceGroup.getFirstInstanceId()));
					}
				}
			} else {
				System.err.println(scriptPath + "is not a file");
			}
		} else {
			System.err.println(scriptPath + " don't exist");
		}
		
		return neededDevices;
	}

	@Override
	public Boolean startScript(String script, Map<String, String> instanceSelection, Map<String, InstanceGroupExecutorInfo> instanceGroupSelection) {
		
		Boolean executed = false;
		
		ArrayList<String> devices = new ArrayList<String>();
		for(Map.Entry<String, String> entry:instanceSelection.entrySet()) {
			devices.add(entry.getValue());
		}
		for(Entry<String, InstanceGroupExecutorInfo> entry:instanceGroupSelection.entrySet()) {
			InstanceGroupExecutorInfo deviceList = entry.getValue();
			for(String device:deviceList.getExecutors()) {
				devices.add(device);
			}
		}
		
		//ScriptInfo scriptVars = getNeededDevices(script);
		//EmulationSession session = sessionManager.getSession(id, getServletContext());
		Integer count = 1;
		try {
			ArrayList<EmulationSession> sessions = new ArrayList<EmulationSession>(em.createQuery("from EmulationSession").getResultList());
			if(sessions != null) count = sessions.size();
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
		
		em.getTransaction().begin();
		
		EmulationSession session = new EmulationSession(script, getServletContext().getRealPath("sessions/" + count.toString()), devices);
		session.setStartTime(System.currentTimeMillis());
		session.setStatus(new EmulationStatus());
		/*Map<String,String> deviceAssignment = new HashMap<String, String>();
		for(int i=0;i<scriptVars.size();i++) {
			String var = scriptVars.get(i);
			deviceAssignment.put(var, deviceSelection.get(i));
		}*/
		
		if(session != null) {
			session.setScript(script);
			//session.addDeviceList(deviceSelection);
			
			ScriptRunner runner = new ScriptRunner(connection, session, instanceSelection, instanceGroupSelection);
			File scriptFile = new File(getServletContext().getRealPath("skripte") + File.separator + script);
			executed = runner.execute(scriptFile);
			
			/*for(int i=0;i<deviceSelection.size();i++) {
				for(int j=0;j<connection.getDeviceList().size();j++) {
					if(deviceSelection.get(i).equals(connection.getDeviceList().get(j))) {
						connection.removeDevice(j);
						break;
					}
				}
			}*/
		}
		
		session.setEndTime(System.currentTimeMillis());
		
		if(executed) {
			em.persist(session);
			em.getTransaction().commit();
		} else {
			em.getTransaction().rollback();
		}
		
		return executed;
	}
	
	@Override
	public SessionList getSessionList() {
		
		SessionList sessionList = new SessionList();
		
		try{
			ArrayList<EmulationSession> sessions = new ArrayList<EmulationSession>(em.createQuery("from EmulationSession").getResultList());
			for(EmulationSession session:sessions) {
				sessionList.addId(session.getId());
				sessionList.addStartTime(session.getStartTime());
				sessionList.addEndTime(session.getEndTime());
				sessionList.addScript(session.getScript());
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
		
		return sessionList;
	}
	
}
