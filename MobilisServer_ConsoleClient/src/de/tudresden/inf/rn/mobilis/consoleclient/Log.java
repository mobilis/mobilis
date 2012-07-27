package de.tudresden.inf.rn.mobilis.consoleclient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * The Class Log.
 */
public class Log {
	
	/** The date formatter. */
	private DateFormat _dateFormatter;	
	
	/** The log file. */
	private File _logFile;
	
	/** The relative log file path. */
	private String _relativeLogFilePath = "";
	
	
	/**
	 * Instantiates a new log.
	 */
	public Log(){
		_dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		_relativeLogFilePath = "log";
		
		File logFolder = new File(_relativeLogFilePath);
		
		if(!logFolder.isDirectory())
			logFolder.mkdir();
	}
	
	/**
	 * Log to console.
	 *
	 * @param str the logging string
	 */
	public void writeToConsole(String str){		 
		System.out.println("[" + _dateFormatter.format(System.currentTimeMillis()) + "] " + str);
	}
	
	/**
	 * Log error to console.
	 *
	 * @param str the error logging string
	 */
	public void writeErrorToConsole(String str){		 
		System.err.println("[" + _dateFormatter.format(System.currentTimeMillis()) + "] " + str);
	}
	
	/**
	 * Log to file.
	 *
	 * @param str the logging string
	 */
	public void writeToFile(String str){
		FileWriter fw;
		
		if(null == _logFile)
			_logFile = new File(System.currentTimeMillis() + ".log");
		
		try {
			fw = new FileWriter(_relativeLogFilePath + File.separator + _logFile, true);
			
			BufferedWriter bw = new BufferedWriter(fw); 
			
			bw.write(str); 
			bw.newLine();
			
			bw.close(); 
		} catch (IOException e) {
			System.err.println("ERROR while writing to logfile: " 
					+ _relativeLogFilePath + File.separator + _logFile.getAbsolutePath());
			
			e.printStackTrace();
		} 
	}

}
