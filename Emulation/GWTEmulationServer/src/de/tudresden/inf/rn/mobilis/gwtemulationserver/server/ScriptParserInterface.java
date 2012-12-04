package de.tudresden.inf.rn.mobilis.gwtemulationserver.server;

import java.io.File;

import de.tudresden.inf.rn.mobilis.gwtemulationserver.server.script.Script;

public interface ScriptParserInterface {
	
	public abstract Script parse(File script);

}
