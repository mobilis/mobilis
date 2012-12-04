package de.tudresden.inf.rn.mobilis.gwtemulationserver.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBElement;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.CommandType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.InstanceType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.ObjectFactory;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.Script;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.SetupCommandType;
import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.TestCommandType;

public class XMLScriptParser implements ScriptParserInterface {

	private Script script = null;
	private JAXBElement<? extends CommandType> currentCommand;
	private StringBuffer accumulator = new StringBuffer();
	
	private List<String> currentXMLTreePath = new ArrayList<String>();
	private ObjectFactory objectFactory = new ObjectFactory();
	
	@Override
	public Script parse(File scriptFile) {
		Schema schema;
		try {
			schema = compileSchema("script-utils/XMLEmulationScript.xsd");
		} catch (SAXException e2) {
			System.err.println("Couldn't compile XMLEmulationScript.xsd!");
			e2.printStackTrace();
			return null;
		}
		
		Validator validator = schema.newValidator();
		try {
			validator.validate(new StreamSource(scriptFile));
		} catch (SAXException e1) {
			System.err.println("Script couldn't be validated against XMLEmulationScript.xsd!");
			e1.printStackTrace();
			System.err.println("...caused by...");
			e1.getCause().fillInStackTrace();
			return null;
		} catch (IOException e1) {
			System.err.println("Couldn't access script file.");
			e1.printStackTrace();
			return null;
		}
		
		// once here we can assume that the script file is valid
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(true);
		SAXParser saxParser;
		
		try {
			saxParser = factory.newSAXParser();
			DefaultHandler parserHandler = new ScriptParserHandler();
			saxParser.parse(scriptFile, parserHandler);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return script;
	}
	
	private Schema compileSchema (String schemaURI) throws SAXException {
		return SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File(schemaURI));
	}
	
	private class ScriptParserHandler extends DefaultHandler {
		
		@Override
		public void startDocument() throws SAXException {
			System.out.println("Emulation script parsing started.");
			super.startDocument();
		}
		
		@Override
		public void endDocument() throws SAXException {
			System.out.println("Finished parsing emulation script.");
			super.endDocument();
		}
		
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			currentXMLTreePath.add(qName);
			if (qName.equals("script")) {
				System.out.println("Entering <script/>...");
				script = new Script();
			} else if (qName.equals("setupCommand")) {
				currentCommand = objectFactory.createSetupCommand(objectFactory.createSetupCommandType());
			} else if (qName.equals("instanceVarDeclaration")) {
				InstanceType instance = objectFactory.createInstanceType();
				instance.setAppNS(attributes.getValue("appNS"));
				instance.setInstanceId(Integer.parseInt(attributes.getValue("instanceId")));
				instance.setVarName(attributes.getValue("varName"));
				
				((SetupCommandType) currentCommand.getValue()).setInstanceVarDeclaration(instance);
			} else if (qName.equals("instance")) {
				// is handled in endElement()
			} else if (qName.equals("start")) {
				((SetupCommandType) currentCommand.getValue()).setSetupMethods(objectFactory.createStart(objectFactory.createStartType()));
			} else if (qName.equals("stop")) {
				((SetupCommandType) currentCommand.getValue()).setSetupMethods(objectFactory.createStop(objectFactory.createStopType()));
			} else if (qName.equals("testCommand")) {
				currentCommand = objectFactory.createTestCommand(objectFactory.createTestCommandType());
			} else if (qName.equals("methodName")) {
				// is handled in endElement()
			}
		}
		
		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			currentXMLTreePath.remove(currentXMLTreePath.size() - 1);
			if (qName.equals("setupCommand") || qName.equals("testCommand")) {
				script.getCommand().add(currentCommand);
			} else if (qName.equals("instance")) {
				// check if we are in the correct element as instance is a pretty common element name
				if (currentXMLTreePath.get(currentXMLTreePath.size() - 2).equals("setupCommandType")) {
					((SetupCommandType) currentCommand.getValue()).setInstance(accumulator.toString().trim());
					accumulator.setLength(0);
				} else if (currentXMLTreePath.get(currentXMLTreePath.size() - 2).equals("setupCommandType")) {
					((TestCommandType) currentCommand.getValue()).setInstance(accumulator.toString().trim());
					accumulator.setLength(0);
				}
			} else if (qName.equals("methodName")) {
				((TestCommandType) currentCommand.getValue()).setMethodName(accumulator.toString().trim());
				accumulator.setLength(0);
			}
		}
		
		@Override
		public void characters(char buffer[], int start, int length)
				throws SAXException {
			accumulator.append(buffer, start, length);
		}
	}

}
