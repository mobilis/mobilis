package de.tudresden.inf.rn.mobilis.android.xhunt.emulation;

import junit.framework.TestSuite;
import android.os.Bundle;
import android.test.InstrumentationTestRunner;
import android.test.InstrumentationTestSuite;
import android.util.Log;

public class XHuntTestStarter extends InstrumentationTestRunner {

	private static final String TAG = "Mobilis XHunt Emulation - TestStarter";

	private String startID = "";
	private String host = "";

	@Override
	public void onCreate(Bundle settings) {

		Log.d(TAG, "onCreate im TestStarter");

		if(settings.containsKey("startID")) {
			startID = settings.getString("startID");
			Log.d(TAG, startID);
		}
		if(settings.containsKey("host")) {
			host = settings.getString("host");
			Log.d(TAG, host);
		}

		super.onCreate(settings);

	}

	public String getStartID() {
		return startID;
	}
	
	public String getHost() {
		return host;
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