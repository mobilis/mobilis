/**
 * Copyright (C) 2009 Technische Universit�t Dresden
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Dresden, University of Technology, Faculty of Computer Science
 * Computer Networks Group: http://www.rn.inf.tu-dresden.de
 * mobilis project: http://mobilisplatform.sourceforge.net
 */

package de.tudresden.inf.rn.mobilis.mxa.provider;

import java.util.HashMap;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA;
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA.RosterItems;

/**
 * @author Istvan Koren
 */
public class RosterProvider extends DynamicContentProvider {

	private static final String TAG = "RosterProvider";

	private static final String DATABASE_NAME = "roster.db";
	private static final int DATABASE_VERSION = 1;
	private static final String ROSTER_TABLE_NAME = "roster";

	private static HashMap<String, String> sRosterProjectionMap;

	private static final int ROSTER = 1;
	private static final int ROSTER_ID = 2;

	private static final UriMatcher sUriMatcher;

	/**
	 * This class helps open, create, and upgrade the database file.
	 * Table:
	 * 	ID | XMPP_ID | NAME | PRESENCE_MODE | PRESENCE_STATUS | UPDATED_DATE | RESSOURCE
	 * 
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + ROSTER_TABLE_NAME + " ("
					+ RosterItems._ID + " INTEGER PRIMARY KEY,"
					+ RosterItems.XMPP_ID + " TEXT," + RosterItems.NAME
					+ " TEXT," + RosterItems.PRESENCE_MODE + " TEXT,"
					+ RosterItems.PRESENCE_STATUS + " TEXT,"
					+ RosterItems.UPDATED_DATE + " INTEGER,"
					+ RosterItems.RESSOURCE + " TEXT" +");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + ROSTER_TABLE_NAME);
			onCreate(db);
		}
	}

	private DatabaseHelper mOpenHelper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#delete(android.net.Uri,
	 * java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case ROSTER:
			count = db.delete(ROSTER_TABLE_NAME, where, whereArgs);
			break;

		case ROSTER_ID:
			String itemId = uri.getPathSegments().get(1);
			count = db.delete(ROSTER_TABLE_NAME,
					RosterItems._ID
							+ "="
							+ itemId
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case ROSTER:
			return RosterItems.CONTENT_TYPE;

		case ROSTER_ID:
			return RosterItems.CONTENT_ITEM_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#insert(android.net.Uri,
	 * android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		Log.i("mobilis", "saving with uri " + uri.toString());
		// Validate the requested uri
		if (sUriMatcher.match(uri) != ROSTER) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		

		// check if XMPP id exists, update if yes
		int id = checkXMPPIdExists(initialValues
				.getAsString(RosterItems.XMPP_ID), initialValues
				.getAsString(RosterItems.RESSOURCE));
		if (id > -1) {
			Uri updateUri = Uri.withAppendedPath(RosterItems.contentUri,
					String.valueOf(id));
			update(updateUri, initialValues, null, null);
			return updateUri;
		}

		// check values, only here otherwise doublechecked on update
		ContentValues values = checkValues(initialValues);

		//SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = db.insert(ROSTER_TABLE_NAME, RosterItems.XMPP_ID, values);
		if (rowId > 0) {
			Uri itemUri = ContentUris.withAppendedId(RosterItems.contentUri,
					rowId);
			getContext().getContentResolver().notifyChange(itemUri, null);
			return itemUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		super.onCreate();
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#query(android.net.Uri,
	 * java.lang.String[], java.lang.String, java.lang.String[],
	 * java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		switch (sUriMatcher.match(uri)) {
		case ROSTER:
			qb.setTables(ROSTER_TABLE_NAME);
			qb.setProjectionMap(sRosterProjectionMap);
			break;

		case ROSTER_ID:
			qb.setTables(ROSTER_TABLE_NAME);
			qb.setProjectionMap(sRosterProjectionMap);
			// get text after first slash
			qb
					.appendWhere(RosterItems._ID + "="
							+ uri.getPathSegments().get(1));
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// If no sort order is specified use the default
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = RosterItems.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}

		// Get the database and run the query
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, orderBy);

		// Tell the cursor what uri to watch, so it knows when its source data
		// changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#update(android.net.Uri,
	 * android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues initialValues, String where,
			String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;

		
		// check values
		ContentValues values = checkValues(initialValues);
		Log.d(TAG,"insert Values: xmppid: "+initialValues.getAsString(RosterItems.XMPP_ID)+" res: "+initialValues.getAsString(RosterItems.RESSOURCE));
		
		
		switch (sUriMatcher.match(uri)) {
		case ROSTER:
			
			count = db.update(ROSTER_TABLE_NAME, values, where, whereArgs);
			if (count==0)
			{
				Cursor entry=db.query(ROSTER_TABLE_NAME, new String[]{RosterItems.XMPP_ID,RosterItems.RESSOURCE},
						RosterItems.XMPP_ID+"='"+values.getAsString(RosterItems.XMPP_ID)
						+"' AND "+RosterItems.RESSOURCE+"=''", null,null,null,null);
				while (entry.moveToNext())
				{
					Log.v(TAG,"entry was there: "+entry.getString(0)+" "+entry.getString(1));
				}
				entry.close();
				int deletion=db.delete(ROSTER_TABLE_NAME,RosterItems.XMPP_ID+"='"+values.getAsString(RosterItems.XMPP_ID)
						+"' AND "+RosterItems.RESSOURCE+"=''",null);
				
				long rowId = db.insert(ROSTER_TABLE_NAME, RosterItems.XMPP_ID, values);
				if (rowId > 0) {
					count=1;
				}
				Log.v(TAG,"entry deleted: "+deletion+"of "+values.getAsString(RosterItems.XMPP_ID)+" and inserted: "+count+" res: "+values.getAsString(RosterItems.RESSOURCE));
			}
			if (values.getAsString(RosterItems.PRESENCE_MODE).equals(RosterItems.MODE_UNAVAILABLE))
			{
				//check if it is the last line
				Cursor c=db.rawQuery("SELECT COUNT(*) FROM "+ROSTER_TABLE_NAME+" WHERE "+RosterItems.XMPP_ID+"='"+values.getAsString(RosterItems.XMPP_ID)+"'",null);
				c.moveToFirst();
				if(c.getInt(0)==1)
				{
					values.put(RosterItems.RESSOURCE,"");
					db.update(ROSTER_TABLE_NAME, values, where, whereArgs);
					
				}
				c.close();
				
			}
			break;

		case ROSTER_ID:
			String rosterId = uri.getPathSegments().get(1);
			count = db.update(ROSTER_TABLE_NAME, values,
					RosterItems._ID
							+ "="
							+ rosterId
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	/**
	 * Make sure that the fields are all set
	 * 
	 * @param initialValues
	 *            The values to check.
	 * @return The correct values.
	 */
	private ContentValues checkValues(ContentValues initialValues) {
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}

		Long now = Long.valueOf(System.currentTimeMillis());

		// Make sure that the fields are all set
		if (values.containsKey(RosterItems.UPDATED_DATE) == false) {
			values.put(RosterItems.UPDATED_DATE, now);
		}

		if (values.containsKey(RosterItems.XMPP_ID) == false) {
			values.put(RosterItems.XMPP_ID, "");
		}

		if (values.containsKey(RosterItems.PRESENCE_MODE) == false) {
			values.put(RosterItems.PRESENCE_MODE, "");
		}

		if (values.containsKey(RosterItems.PRESENCE_STATUS) == false) {
			values.put(RosterItems.PRESENCE_STATUS, "");
		}
		
		if (values.containsKey(RosterItems.RESSOURCE) == false) {
			values.put(RosterItems.RESSOURCE, "");
		}

		return values;
	}

	/**
	 * Checks whether there is a dataset available for the provided XMPP ID or
	 * not.
	 * 
	 * @param xmppId
	 *            The bare XMPP ID.
	 *          
	 * @return The id of the dataset if the XMPP ID is already in database, -1
	 *         if the user has not been found.
	 */
	private int checkXMPPIdExists(String xmppId, String ressource) {
		int id = -1;
		Cursor c;
		if (ressource==null)
			c= query(RosterItems.contentUri, null, RosterItems.XMPP_ID
				+ "='" + xmppId + "'", null, null);
		else
			c= query(RosterItems.contentUri, null, RosterItems.XMPP_ID
					+ "='" + xmppId + "'"+"AND "+RosterItems.RESSOURCE+"='"+ressource+"'", null, null);
		if (c.moveToFirst())
			id = c.getInt(c.getColumnIndex(RosterItems._ID));

		// no, we don't forget to release the resources
		c.close();
		return id;
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		sRosterProjectionMap = new HashMap<String, String>();
		sRosterProjectionMap.put(RosterItems._ID, RosterItems._ID);
		sRosterProjectionMap.put(RosterItems.XMPP_ID, RosterItems.XMPP_ID);
		sRosterProjectionMap.put(RosterItems.NAME, RosterItems.NAME);
		sRosterProjectionMap.put(RosterItems.PRESENCE_MODE,
				RosterItems.PRESENCE_MODE);
		sRosterProjectionMap.put(RosterItems.PRESENCE_STATUS,
				RosterItems.PRESENCE_STATUS);
		sRosterProjectionMap.put(RosterItems.UPDATED_DATE,
				RosterItems.UPDATED_DATE);
		sRosterProjectionMap.put(RosterItems.RESSOURCE,
				RosterItems.RESSOURCE);
	}
	
	@Override
	public void loadUriMatcherAuthority() {
		sUriMatcher.addURI(ConstMXA.rosterAuthority, "rosteritems", ROSTER);
		sUriMatcher.addURI(ConstMXA.rosterAuthority, "rosteritems/#",
				ROSTER_ID);
	}

}
