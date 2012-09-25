package de.tudresden.inf.rn.mobilis.android.xhunt.test;

import junit.framework.TestSuite;
import android.os.Bundle;
import android.test.InstrumentationTestRunner;
import android.test.InstrumentationTestSuite;
import android.util.Log;

public class XHuntTestStarter extends InstrumentationTestRunner {
	
	private static final String TAG = "XHuntAutomatic - TestStarter";
	
	private Boolean isMisterX = true;
	private String userName = "";
	private Integer playerCount = 0;
	
	@Override
	public void onCreate(Bundle settings) {
		
		Log.d(TAG, "onCreate im TestStarter");
		
		if(settings.containsKey("userName") && settings.containsKey("isMisterX")) {
			isMisterX = settings.getBoolean("isMisterX");
			userName = settings.getString("userName");
			Log.d(TAG, "userName: " + userName);
			Log.d(TAG, "isMisterX: " + isMisterX);
		} else {
			Log.d(TAG, "noSetting");
		}
		
		if(settings.containsKey("playerCount")) {
			playerCount = settings.getInt("playerCount");
		}
		
		super.onCreate(settings);
		
	}
	
	public Boolean isMisterX() {
		return isMisterX;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public Integer getPlayerCount() {
		return playerCount;
	}
	
	@Override
    public TestSuite getAllTests() {
		Log.d(TAG, "getAllTests()");
		
        InstrumentationTestSuite suite = new InstrumentationTestSuite(this);
        suite.addTestSuite(MainTest.class);
        
        return suite;
    }
	
	@Override
    public ClassLoader getLoader() {
		Log.d(TAG, "getLoader()");
		
        return XHuntTestStarter.class.getClassLoader();
    }

}
