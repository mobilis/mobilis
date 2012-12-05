package de.tudresden.inf.rn.mobilis.gwtemulationserver.server;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.Script;

public class XMLScriptParser implements ScriptParserInterface {

	public Script parse(File scriptFile) {
		Schema schema;
		try {
			schema = compileSchema("src/script-utils/XMLEmulationScript.xsd");
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
		Script script = null;
		try {
			JAXBContext context = JAXBContext.newInstance(Script.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			script = (Script) unmarshaller.unmarshal(scriptFile);
		} catch (JAXBException e1) {
			System.err.println("Error creating JAXBContext or unmarshalling script file!");
			e1.printStackTrace();
			return null;
		}
		
		return script;
	}
	
	private Schema compileSchema (String schemaURI) throws SAXException {
		return SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File(schemaURI));
	}
	
}
