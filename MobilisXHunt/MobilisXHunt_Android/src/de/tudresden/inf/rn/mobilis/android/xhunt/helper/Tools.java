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
package de.tudresden.inf.rn.mobilis.android.xhunt.helper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;

import com.google.android.maps.GeoPoint;

/**
 * The Class Tools to provide some useful tools.
 */
public class Tools {
	
	/** The context of the application. */
	private Context mContext;
	
	/** The process which continuously writes the logcat output into a file. */
	private Process logProcess;

	/**
	 * Instantiates a new Tool class.
	 *
	 * @param context the context of the application
	 */
	public Tools(Context context) {
		this.mContext = context;
	}
	
	/**
	 * Convert a latitude and longitude from double to GeoPoint 
	 * (@see com.google.android.maps.GeoPoint).
	 *
	 * @param lat the latitude as double
	 * @param lon the longitude as double
	 * @return an Android specific GeoPoint
	 */
	public GeoPoint doubleToGeoPoint(double lat, double lon){
		return new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
	}
	
	/**
	 * Helper function to force the mobile device to vibrate. 
	 * In this application the function is used to announce when a new chat 
	 * message comes in.
	 */
	public void vibrateOnChatMessage(){
		Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

		// start and duration of vibrations
		// {start in 0ms, vibrate for 500ms, pause for 300ms, vibrate for 500ms}
    	long[] vibrate = {0, 500, 300, 500 };
    	
    	// -1 prevent vibration from repeating the pattern
    	vibrator.vibrate(vibrate, -1);
	}
	
	/**
	 * Reads '/system/bin/logcat' and writes it output to '\Memory Stick\xhunt\logcat\' until
	 * stopWritingLogToFile() is called.
	 */
	public void writeLogToFile() {
		try {
			File sdFolder = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "xhunt");
			if(!sdFolder.isDirectory())
				sdFolder.mkdir();
			
			File logFolder = new File(sdFolder.getAbsoluteFile(), "logcat");
			if(!logFolder.isDirectory())
				logFolder.mkdir();
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
			File logFile = new File(logFolder.getAbsoluteFile(), formatter.format(new Date()) + ".log");
			//File logFile = new File(logFolder.getAbsoluteFile(), System.currentTimeMillis() + ".log");
			
			String[] cmd = { "/system/bin/logcat", "-v", "time", "-f", logFile.getAbsolutePath() };
			ProcessBuilder procBuilder = new ProcessBuilder(cmd);
			if(logProcess != null)
				logProcess.destroy();
			logProcess = procBuilder.start();
			
		} catch (Exception e) {
			Log.e("Tools", "Error writing log to file", e);
		}	
	}
	
	/**
	 * Stops writeLogToFile().
	 */
	public void stopWritingLogToFile() {
		if(logProcess != null)
			logProcess.destroy();
	}
	
	/**
	 * Deletes all logfiles.
	 */
	public void deleteLogFiles() {
		 File sdFolder = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "xhunt");
		 
		 if(sdFolder.isDirectory()) {
			 File logcatFolder = new File(sdFolder.getAbsoluteFile(), "logcat");
			 File gpslogFolder = new File(sdFolder.getAbsoluteFile(), "gpx");
			 
			 if(logcatFolder.isDirectory()) {
				 String[] files = logcatFolder.list();
				 for(int i=0; i<files.length; i++) {
					 new File(logcatFolder, files[i]).delete();
				 }
			 }
			 
			 if(gpslogFolder.isDirectory()) {
				 String[] files = gpslogFolder.list();
				 for(int i=0; i<files.length; i++) {
					 new File(gpslogFolder, files[i]).delete();
				 }
			 }
		 }
	}
}
