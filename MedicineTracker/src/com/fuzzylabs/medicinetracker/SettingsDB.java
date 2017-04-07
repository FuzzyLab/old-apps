package com.fuzzylabs.medicinetracker;

import java.sql.SQLException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.RingtoneManager;

public class SettingsDB {

	public static final String KEY_ROWID = "_id";
	public static final String SNOOZE_TIME = "snooze_time";
	public static final String TONE = "tone";
	public static final String VIBRATE = "vibrate";
	public static final String LIGHT = "light";

	private static final String DATABASE_NAME = "settingsDb";
	static final String SETTINGS_TABLE = "settingsTable";
	private static final int DATABASE_VERSION = 1;

	private DbHelper ourHelper;
	private final Context ourContext;
	SQLiteDatabase ourDatabase;

	private static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + SETTINGS_TABLE + " ("
					+ KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ SNOOZE_TIME + " INTEGER NOT NULL, "
					+ TONE + " TEXT NOT NULL, "
					+ LIGHT + " INTEGER NOT NULL, "
					+ VIBRATE +" INTEGER NOT NULL);");
			
			ContentValues cv = new ContentValues();
			cv.put(SNOOZE_TIME, 10);
			cv.put(TONE, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());
			cv.put(VIBRATE, 1);
			cv.put(LIGHT, 1);
			db.insert(SETTINGS_TABLE, null, cv);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + SETTINGS_TABLE);
			onCreate(db);
		}
	}
	
	public int getSnoozeTime() {
		String[] columns = new String[] { KEY_ROWID, SNOOZE_TIME };
		Cursor c = ourDatabase.query(SETTINGS_TABLE, columns, null, null, null,
				null, null);
		c.moveToFirst();
		return Integer.parseInt(c.getString(1));
	}
	
	public String getTone() {
		String[] columns = new String[] { KEY_ROWID, TONE };
		Cursor c = ourDatabase.query(SETTINGS_TABLE, columns, null, null, null,
				null, null);
		c.moveToFirst();
		return c.getString(1);
	}
	
	public int getVibrate() {
		String[] columns = new String[] { KEY_ROWID, VIBRATE };
		Cursor c = ourDatabase.query(SETTINGS_TABLE, columns, null, null, null,
				null, null);
		c.moveToFirst();
		return Integer.parseInt(c.getString(1));
	}
	
	public int getLight() {
		String[] columns = new String[] { KEY_ROWID, LIGHT };
		Cursor c = ourDatabase.query(SETTINGS_TABLE, columns, null, null, null,
				null, null);
		c.moveToFirst();
		return Integer.parseInt(c.getString(1));
	}

	public SettingsDB(Context c) {

		ourContext = c;
	}

	public SettingsDB open() throws SQLException {
		ourHelper = new DbHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		ourHelper.close();
	}
}