package de.tudresden.inf.rn.mobilis.gwtemulationserver.server.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Map;

import org.junit.Test;

import com.google.gwt.dev.util.collect.HashMap;

import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.XMLScriptExecutor;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.XMLScriptParser;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.AppCommandType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.InstanceGroupType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.InstanceType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.Script;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.StartType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.StopType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.notgen.InstanceGroupImpl;

public class XMLScriptParserTest extends XMLScriptParser {

	private final String scriptFilePath = "src/script-utils/testScript.xml";

	@Test
	public void testParse() {
		assertNotNull(parseScript());
	}
	
	@Test
	public void testInstanceGroupFirstInstanceId() {
		InstanceGroupType instanceGroup = (InstanceGroupType) parseScript().getCommand().get(2).getValue();
		
		assertEquals(21, instanceGroup.getFirstInstanceId());
		assertEquals(3, instanceGroup.getInstanceCount());
	}
	
	@Test
	public void testInstanceGroupImplConstructor() {
		// first instance ID = 1
		InstanceGroupImpl instanceGroupImpl = new InstanceGroupImpl((InstanceGroupType) parseScript().getCommand().get(1).getValue());
		
		assertEquals("evil.race", instanceGroupImpl.getAppNS());
		assertEquals(1, instanceGroupImpl.getFirstInstanceId());
		assertEquals(20, instanceGroupImpl.getInstanceCount());
		assertEquals("daleks", instanceGroupImpl.getVarName());
		assertEquals(20, instanceGroupImpl.getInstances().size());
		
		for (int i = 0; i < instanceGroupImpl.getInstances().size(); i++) {
			InstanceType instance = instanceGroupImpl.getInstances().get(i);
			assertEquals(i + instanceGroupImpl.getFirstInstanceId(), instance.getInstanceId());
		}
		
		// first instance ID arbitrary
		InstanceGroupImpl instanceGroupImpl2 = new InstanceGroupImpl((InstanceGroupType) parseScript().getCommand().get(2).getValue());
		
		assertEquals("evil.race", instanceGroupImpl2.getAppNS());
		assertEquals(21, instanceGroupImpl2.getFirstInstanceId());
		assertEquals(3, instanceGroupImpl2.getInstanceCount());
		assertEquals("daleks2", instanceGroupImpl2.getVarName());
		assertEquals(3, instanceGroupImpl2.getInstances().size());
		
		for (int i = 0; i < instanceGroupImpl2.getInstances().size(); i++) {
			InstanceType instance = instanceGroupImpl2.getInstances().get(i);
			assertEquals(i + instanceGroupImpl2.getFirstInstanceId(), instance.getInstanceId());
		}
	}
	
	@Test
	public void instanceGroupFirstInstanceIdShouldBeRespectedByScriptExecutor() {
		final Map<Integer, Boolean> inspectedGroupInstancesToStart = new HashMap<Integer, Boolean>();
		inspectedGroupInstancesToStart.put(21, false);
		inspectedGroupInstancesToStart.put(22, false);
		inspectedGroupInstancesToStart.put(23, false);
		
		XMLScriptExecutor executor = new XMLScriptExecutor() {
			
			@Override
			public void executeStopCommand(InstanceType instance, StopType stopCommand) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void executeStartCommand(InstanceType instance,
					StartType startCommand) {
				if (instance.getVarName().equals("daleks2")) {
					inspectedGroupInstancesToStart.put(instance.getInstanceId(), true);
				}
			}
			
			@Override
			public void executeAppCommand(InstanceType instance,
					AppCommandType appCommand) {
				if (instance.getVarName().equals("daleks2")) {
					assertEquals(String.valueOf(instance.getInstanceId()), appCommand.getParameter().getIntOrStringOrBoolean().get(1));
				}
			}
		};
		
		assertTrue(executor.execute(new File(scriptFilePath)));
		
		for (int instanceId : inspectedGroupInstancesToStart.keySet()) {
			assertTrue(inspectedGroupInstancesToStart.get(instanceId));
		}
	}
	
	private Script parseScript() {
		XMLScriptParser parser = new XMLScriptParser();
		return parser.parse(new File(scriptFilePath));
	}
	
}
