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
import de.tudresden.inf.rn.mobilis.mxa.ConstMXA.MessageItems;

/**
 * @author Istvan Koren
 */
public class MessageProvider extends DynamicContentProvider {

	private static final String TAG = "MessageProvider";

	private static final String DATABASE_NAME = "messages.db";
	private static final int DATABASE_VERSION = 1;
	private static final String MESSAGES_TABLE_NAME = "messages";

	private static HashMap<String, String> sMessagesProjectionMap;

	/**
	 * all messages
	 */
	private static final int MESSAGES = 1;
	/**
	 * specific message
	 */
	private static final int MESSAGE_ID = 2;

	private static final UriMatcher sUriMatcher;

	/**
	 * This class helps open, create, and upgrade the database file.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + MESSAGES_TABLE_NAME + " ("
					+ MessageItems._ID + " INTEGER PRIMARY KEY,"
					+ MessageItems.SENDER + " TEXT, " + MessageItems.RECIPIENT
					+ " TEXT, " + MessageItems.SUBJECT + " TEXT, "
					+ MessageItems.BODY + " TEXT, " + MessageItems.DATE_SENT
					+ " LONG, " + MessageItems.READ + " INTEGER, "
					+ MessageItems.TYPE + " TEXT, " + MessageItems.STATUS
					+ " TEXT" + ");");
			Log.i(TAG, "onCreate(SQLiteDatabase db) --> DATE_SENT is LONG");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + MESSAGES_TABLE_NAME);
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
		case MESSAGES:
			count = db.delete(MESSAGES_TABLE_NAME, where, whereArgs);
			break;

		case MESSAGE_ID:
			String itemId = uri.getPathSegments().get(1);
			count = db.delete(MESSAGES_TABLE_NAME,
					MessageItems._ID
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
		case MESSAGES:
			return MessageItems.CONTENT_TYPE;

		case MESSAGE_ID:
			return MessageItems.CONTENT_ITEM_TYPE;

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
		if (sUriMatcher.match(uri) != MESSAGES) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// check values
		ContentValues values = checkValues(initialValues);

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = db.insert(MESSAGES_TABLE_NAME, MessageItems.BODY, values);
		if (rowId > 0) {
			Uri noteUri = ContentUris.withAppendedId(MessageItems.contentUri,
					rowId);
			getContext().getContentResolver().notifyChange(noteUri, null);
			return noteUri;
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
		case MESSAGES:
			qb.setTables(MESSAGES_TABLE_NAME);
			qb.setProjectionMap(sMessagesProjectionMap);
			break;

		case MESSAGE_ID:
			qb.setTables(MESSAGES_TABLE_NAME);
			qb.setProjectionMap(sMessagesProjectionMap);
			// get text after first slash
			qb.appendWhere(MessageItems._ID + "="
					+ uri.getPathSegments().get(1));
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// If no sort order is specified use the default
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = MessageItems.DEFAULT_SORT_ORDER;
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

		switch (sUriMatcher.match(uri)) {
		case MESSAGES:
			count = db.update(MESSAGES_TABLE_NAME, values, where, whereArgs);
			break;

		case MESSAGE_ID:
			String itemId = uri.getPathSegments().get(1);
			count = db.update(MESSAGES_TABLE_NAME, values,
					MessageItems._ID
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
		if (values.containsKey(MessageItems.SENDER) == false) {
			values.put(MessageItems.SENDER, "");
		}

		if (values.containsKey(MessageItems.RECIPIENT) == false) {
			values.put(MessageItems.RECIPIENT, "");
		}

		if (values.containsKey(MessageItems.SUBJECT) == false) {
			values.put(MessageItems.SUBJECT, "");
		}

		if (values.containsKey(MessageItems.BODY) == false) {
			values.put(MessageItems.BODY, "");
		}

		if (values.containsKey(MessageItems.DATE_SENT) == false) {
			values.put(MessageItems.DATE_SENT, now);
		}

		if (values.containsKey(MessageItems.READ) == false) {
			values.put(MessageItems.READ, 0);
		}

		if (values.containsKey(MessageItems.TYPE) == false) {
			values.put(MessageItems.TYPE, "");
		}

		if (values.containsKey(MessageItems.STATUS) == false) {
			values.put(MessageItems.STATUS, "");
		}

		return values;
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		sMessagesProjectionMap = new HashMap<String, String>();
		sMessagesProjectionMap.put(MessageItems._ID, MessageItems._ID);
		sMessagesProjectionMap.put(MessageItems.SENDER, MessageItems.SENDER);
		sMessagesProjectionMap.put(MessageItems.RECIPIENT,
				MessageItems.RECIPIENT);
		sMessagesProjectionMap.put(MessageItems.SUBJECT, MessageItems.SUBJECT);
		sMessagesProjectionMap.put(MessageItems.BODY, MessageItems.BODY);
		sMessagesProjectionMap.put(MessageItems.DATE_SENT,
				MessageItems.DATE_SENT);
		sMessagesProjectionMap.put(MessageItems.READ, MessageItems.READ);
		sMessagesProjectionMap.put(MessageItems.TYPE, MessageItems.TYPE);
		sMessagesProjectionMap.put(MessageItems.STATUS, MessageItems.STATUS);
	}
	
	@Override
	public void loadUriMatcherAuthority() {
		sUriMatcher.addURI(ConstMXA.messageAuthority, "messageitems", MESSAGES);
		sUriMatcher.addURI(ConstMXA.messageAuthority, "messageitems/#",
				MESSAGE_ID);
	}

}
