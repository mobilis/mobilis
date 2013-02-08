package de.tudresden.inf.rn.mobilis.gwtemulationserver.server;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.tudresden.inf.rn.mobilis.gwtemulationserver.client.EmuServerConnectService;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.CommandType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.InstanceGroupType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.InstanceType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.Script;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.utils.EmulationConnection;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.utils.EmulationSession;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.utils.SessionManager;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.shared.InstanceGroupExecutorInfo;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.shared.InstanceGroupInfo;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.shared.ScriptInfo;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.shared.SessionInfo;

public class EmuServerConnectServiceImpl extends RemoteServiceServlet implements EmuServerConnectService {

	private String TAG = "EmulationServerService";
	private SessionManager sessionManager = new SessionManager();
	private EmulationConnection connection = new EmulationConnection();
	
	@Override
	public void init() throws ServletException {
		//Connection.DEBUG_ENABLED = true;
		connection.connect();
		super.init();
		
	}

	@Override
	public void destroy() {
		connection.disconnect();
		super.destroy();
	}



	@Override
	public Boolean sendCommand(String cmd) {
		
		return false;
		
	}

	@Override
	public List<String> getDeviceList(String id) {
		
		List<String> devices = null;
		if(sessionManager.sessionExist(id)) devices=connection.getDeviceList();
		return devices;
		
	}

	@Override
	public SessionInfo openSession(String id) {
		
		//Connection.DEBUG_ENABLED = true;
		
		EmulationSession session = sessionManager.getSession(id, getServletContext());
		SessionInfo info = null;
		
		if(session != null) {
			String sessionID = session.getId();
			info = new SessionInfo(true,sessionID,getScriptList());
		} else{
			info = new SessionInfo(false,"","Session with ID " + id + " don't exist!");
		}
		
		return info;
		
	}

	@Override
	public Boolean closeSession(String id) {
		
		//EmulationSession session = sessionManager.getSession(id);
		//session.disconnect();
//		sessionManager.deleteSession(id);
		
		return true;
		
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
	public Boolean startScript(String id, String script, Map<String, String> instanceSelection, Map<String, InstanceGroupExecutorInfo> instanceGroupSelection) {
		
		Boolean executed = false;
		//ScriptInfo scriptVars = getNeededDevices(script);
		EmulationSession session = sessionManager.getSession(id, getServletContext());
		session.setStartTime(System.currentTimeMillis());
		/*Map<String,String> deviceAssignment = new HashMap<String, String>();
		for(int i=0;i<scriptVars.size();i++) {
			String var = scriptVars.get(i);
			deviceAssignment.put(var, deviceSelection.get(i));
		}*/
		
		if(session != null) {
			session.setScriptName(script);
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
		
		return executed;
	}

}
