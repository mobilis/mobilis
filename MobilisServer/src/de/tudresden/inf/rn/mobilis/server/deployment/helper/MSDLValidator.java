package de.tudresden.inf.rn.mobilis.server.deployment.helper;

import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

/**
 * The Class MSDLValidator to validate a msdl file. This class isn't ready yet
 * because it cannot handle two schema files. The msdl file consist of two
 * schema files aka namespaces.
 */
public class MSDLValidator {

	/** The last validation error message. */
	private String _lastValidationErrorMessage = "";

	/**
	 * Gets the last validation error message.
	 * 
	 * @return the last validation error message
	 */
	public String getLastValidationErrorMessage() {
		return _lastValidationErrorMessage;
	}

	/**
	 * Validate a msdl against the schema.
	 * 
	 * @param inMsdlStream
	 *            the msdl as input stream
	 * @param inMsdlSchemaStream
	 *            the schema as input stream
	 * @return true, if msdl is valid
	 */
	public boolean validateSchema( InputStream inMsdlStream, InputStream inMsdlSchemaStream ) {
		boolean isValid = false;

		try {
			SchemaFactory schemaFactory = SchemaFactory
					.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );

			Schema schema = schemaFactory.newSchema( new StreamSource( inMsdlSchemaStream ) );
			Validator validator = schema.newValidator();

			validator.validate( new StreamSource( inMsdlStream ) );

			isValid = true;
		} catch ( SAXException ex ) {
			_lastValidationErrorMessage = ex.getMessage();
		} catch ( Exception ex ) {
			ex.printStackTrace();
		}

		return isValid;
	}

}
