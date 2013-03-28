package de.tudresden.inf.rn.mobilis.android.emulation;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivtiy extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}

}
