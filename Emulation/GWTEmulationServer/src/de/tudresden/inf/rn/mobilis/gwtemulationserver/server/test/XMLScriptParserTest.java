package de.tudresden.inf.rn.mobilis.gwtemulationserver.server.test;

import java.io.File;

import org.junit.Test;

import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.XMLScriptParser;

public class XMLScriptParserTest extends XMLScriptParser {

	@Test
	public void testParse() {
		XMLScriptParser parser = new XMLScriptParser();
		parser.parse(new File("src/script-utils/testScript.xml"));
	}

}
