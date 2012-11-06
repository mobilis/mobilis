/*******************************************************************************
 * Copyright (C) 2010 Technische Universität Dresden
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Dresden, University of Technology, Faculty of Computer Science
 * Computer Networks Group: http://www.rn.inf.tu-dresden.de
 * mobilis project: http://mobilisplatform.sourceforge.net
 ******************************************************************************/
package de.tudresden.inf.rn.mobilis.services.xhunt.helper;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * The LogClass is responsible for creating a log file in '[E:/|C:/|/var/]XHuntServiceLogs' and for writing into this log file.
 * It also takes care of the deletion of older log files, to save disk space. All Loggers instantiated in other classes
 * inherit Handlers and configuration from the Logger of this class, as long as their namespace is correctly set.
 * All log output is not only written into a file, but also to the console.
 * 
 * @author Matthias Köngeter
 */
public class LogClass {
	
	/** The Logger object used for Logging. */
	private Logger mLogger;
	/** A FileHandler used by the Logger to write into the log file. */
	private FileHandler mFHandler;
	/** A ConsoleHandler used by the Logger to write to the console. */
	private ConsoleHandler mCHandler;
	/** The folder to store the log file in. */
	private File logFolder;
	
	
	/**
	 * Creates a new LogClass instance with its own FileHandler.
	 */
	public LogClass() {
		//name = empty String: RootLogger, also logs messages from MobilisServer
		//should only be on top of the XHuntService Logger hierarchy
		mLogger = Logger.getLogger("de.tudresden.inf.rn.mobilis.services.xhunt");
		mLogger.setLevel(Level.INFO);
		mLogger.setUseParentHandlers(false);
		
		String os = System.getProperty("os.name").toLowerCase();
	
		if(os.contains("win")) {
			if(new File("E:/").exists())
				logFolder = new File("E:/XHuntServiceLogs");
			else
				logFolder = new File("C:/XHuntServiceLogs");
		}
		
		else {
			logFolder = new File("/var/XHuntServiceLogs");
		}

		if(!logFolder.isDirectory()) {
			try {
				logFolder.mkdir();
			} catch (SecurityException e) { e.printStackTrace(); }
		}
		
		deleteOlderLogs(14);
		initializeHandler(200);
	}
	
	
	/**
	 * Initializes the FileHandler. For the name of the log file, a timestamp is used.
	 * When the file reaches the size given as the parameter, writing restarts at the beginning of the file.
	 * @param maxFilesizeMB the maximum size allowed for the log file (in MB).
	 */
	private void initializeHandler(int maxFilesizeMB) {
		
		if(logFolder.isDirectory()) {
			
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS");	
			File logFile = new File(logFolder, formatter.format(new Date()) + ".txt");
			
			try {
				mFHandler = new FileHandler(logFile.getAbsolutePath(), 1024*1024*maxFilesizeMB, 1, true);
				mFHandler.setFormatter(new SimpleFormatter() {
					@Override
					public String format(LogRecord record) {
						return super.format(record) + "\r\n";
					}
				});
				mFHandler.setLevel(Level.ALL);
				mLogger.addHandler(mFHandler);
				
			} catch (Exception e) { e.printStackTrace(); }
		}

		mCHandler = new ConsoleHandler();
		mCHandler.setFormatter(new SimpleFormatter());
		mCHandler.setLevel(Level.ALL);
		mLogger.addHandler(mCHandler);
	}
	
	/**
	 * Checks how many files are existent in the log folder and deletes the oldest files if the maximum number is exceeded.
	 * @param maxCountAllowed the maximum number of log files allowed in the log folder
	 */
	private void deleteOlderLogs(int maxCountAllowed) {
		String[] existingLogs = logFolder.list();
		while(existingLogs.length > maxCountAllowed) {
			File oldestLog = null;
			for(int i=0; i<existingLogs.length; i++) {
				File deleteCandidate = new File(logFolder, existingLogs[i]);
				if((oldestLog == null) || (deleteCandidate.lastModified() < oldestLog.lastModified()))
						oldestLog = deleteCandidate;
			}
			oldestLog.delete();
			existingLogs = logFolder.list();
			oldestLog = null;
		}
	}
	
	/**
	 * Closes the FileHandler, so that no .lck files remain in the log folder, and flushes the ConsoleHandler.
	 */
	public void closeLogFile() {
		mCHandler.flush();
		mFHandler.close();
	}
}
