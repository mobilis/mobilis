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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import de.tudresden.inf.rn.mobilis.android.xhunt.Const;

/**
 * The Class SharedPrefHelper to simplify handling with shared preferences.
 *
 */
public class SharedPrefHelper {
	
	/** The shared preferences attribute used for this application 
	 * (@see android.content.SharedPreferences). */
	private SharedPreferences prefs = null;
	
	/** The editor attribute to modify shared preferences (@see android.content.SharedPreferences.Editor). */
	private Editor editor;

	/**
	 * Creates an new Class with the applications context.
	 *
	 * @param context the context of the application
	 */
	@SuppressLint("CommitPrefEdits")
	public SharedPrefHelper(Context context) {
		prefs = context.getSharedPreferences( Const.SHARED_PREF_KEY_FILE_NAME, Context.MODE_PRIVATE);
		editor = prefs.edit();
	}
	
	/**
	 * Get the value of the key as a string or null.
	 *
	 * @param key the key for a specific value stored in shared preferences
	 * @return the value of a specific key or null
	 */
	public String getValue(String key) {
		return prefs.getString( key, null );
	}
	
	/**
	 * Get the value of the key as a string.
	 *
	 * @param key the key for a specific value stored in shared preferences
	 * @param defaultValue the default value if a key does not exists
	 * @return the value of a specific key or default value if key does not exists
	 */
	public String getValue(String key, String defaultValue) {
		return prefs.getString( key, defaultValue );
	}

	/**
	 * Get the value of the key as an int or -1 if the value does not exists.
	 *
	 * @param key the key for a specific value stored in shared preferences
	 * @return the value of a specific key or -1
	 */
	public int getValueAsInt(String key) {
		return prefs.getInt( key, -1 );
	}
	
	/**
	 * Get the value of the key as boolean or false.
	 *
	 * @param key the key for a specific value stored in shared preferences
	 * @return the value of a specific key or false as default
	 */
	public boolean getValueAsBool(String key) {
		return prefs.getBoolean( key, false );
	}
	
	/**
	 * Get the value of the key as long or -1.
	 *
	 * @param key the key for a specific value stored in shared preferences
	 * @return the value of a specific key or -1
	 */
	public long getValueAsLong(String key) {
		return prefs.getLong( key, -1 );
	}
	
	/**
	 * Store a value as string related to a unique key in shared preferences.
	 *
	 * @param key the key to locate a specific value
	 * @param value a string value related to the key
	 */
	public void setValue(String key, String value) {
		editor.putString( key, value );
	}

	/**
	 * Store a value as int related to a unique key in shared preferences.
	 *
	 * @param key the key to locate a specific value
	 * @param value an int value related to the key
	 */
	public void setValueInt(String key, int value) {
		editor.putInt( key, value );
	}

	/**
	 * Store a value as bool related to a unique key in shared preferences.
	 *
	 * @param key the key to locate a specific value
	 * @param value a bool value related to the key
	 */
	public void setValueBool(String key, boolean value) {
		editor.putBoolean( key, value );
	}
	
	/**
	 * Store a value as long related to a unique key in shared preferences.
	 *
	 * @param key the key to locate a specific value
	 * @param value a long value related to the key
	 */
	public void setValueLong(String key, long value) {
		editor.putLong( key, value );
	}

	/**
	 * Clear the previously changed settings to last savepoint.
	 */
	public void clear() {
		editor.clear();
	}
	
	/**
	 * Save the previously changed settings.
	 *
	 * @return true, if successful
	 */
	public boolean save() {
		return editor.commit();
	}
}