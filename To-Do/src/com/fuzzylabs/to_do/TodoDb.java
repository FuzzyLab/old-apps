package com.fuzzylabs.to_do;

import java.sql.SQLException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TodoDb {

	public static final String KEY_ROWID = "_id";
	public static final String JOB_NAME = "jobName";
	public static final String DUE_DATE = "dueDate";
	public static final String DUE_TIME = "dueTime";
	public static final String IS_DONE = "isDone";
	public static final String IS_NOTIFICATION = "isNotification";
	public static final String IS_ALARM = "isAlarm";
	public static final String URGENCY = "urgency";
	public static final String NOTES = "notes";

	public static final String CHECK_ID = "_id";
	public static final String JOB_ID = "jobId";
	public static final String ITEM_NAME = "itemName";
	public static final String IS_CHECKED = "isChecked";

	private static final String DATABASE_NAME = "todoDb";
	static final String CHECKLIST_TABLE = "checklistTable";
	static final String DATABASE_TABLE = "todoTable";
	private static final int DATABASE_VERSION = 2;

	private DbHelper ourHelper;
	private final Context ourContext;
	SQLiteDatabase ourDatabase;

	private static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" + KEY_ROWID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + JOB_NAME
					+ " TEXT NOT NULL, " + DUE_DATE + " TEXT NOT NULL, "
					+ URGENCY + " INTEGER NOT NULL, " + NOTES
					+ " TEXT NOT NULL, " + DUE_TIME + " TEXT NOT NULL, "
					+ IS_NOTIFICATION + " INTEGER NOT NULL, " + IS_ALARM
					+ " INTEGER NOT NULL, " + IS_DONE
					+ " INTEGER NOT NULL, UNIQUE (" + JOB_NAME + ", "
					+ DUE_DATE + ") ON CONFLICT IGNORE);");
			
			db.execSQL("CREATE TABLE " + CHECKLIST_TABLE + " (" + CHECK_ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + JOB_ID
					+ " TEXT NOT NULL, " + ITEM_NAME + " TEXT NOT NULL, "
					+ IS_CHECKED + " INTEGER NOT NULL, UNIQUE (" + JOB_ID
					+ ", " + ITEM_NAME + ") ON CONFLICT IGNORE);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (newVersion < 2) {
				db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
				onCreate(db);
			} else if(newVersion < 3) {
				db.execSQL("CREATE TABLE " + CHECKLIST_TABLE + " (" + CHECK_ID
						+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + JOB_ID
						+ " TEXT NOT NULL, " + ITEM_NAME + " TEXT NOT NULL, "
						+ IS_CHECKED + " INTEGER NOT NULL, UNIQUE (" + JOB_ID
						+ ", " + ITEM_NAME + ") ON CONFLICT IGNORE);");
			}
		}
	}

	public TodoDb(Context c) {

		ourContext = c;
	}

	public TodoDb open() throws SQLException {
		ourHelper = new DbHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		ourHelper.close();
	}

	public long createEntry(String jobName, String dueDate, String dueTime,
			int isNotification, int isAlarm, int isDone, int urgency,
			String notes) {
		ContentValues cv = new ContentValues();
		cv.put(JOB_NAME, jobName);
		cv.put(DUE_DATE, dueDate);
		cv.put(DUE_TIME, dueTime);
		cv.put(IS_NOTIFICATION, isNotification);
		cv.put(IS_ALARM, isAlarm);
		cv.put(IS_DONE, isDone);
		cv.put(URGENCY, urgency);
		cv.put(NOTES, notes);
		return ourDatabase.insert(DATABASE_TABLE, null, cv);
	}

	public int getCount(String dbTable) {
		int count = 0;
		Cursor mCount = ourDatabase.rawQuery("select count(*) from "+dbTable,
				null);
		mCount.moveToFirst();
		count = mCount.getInt(0);
		mCount.close();
		return count;
	}

	public Cursor getAllRows() {
		// TODO Auto-generated method stub
		String[] columns = new String[] { KEY_ROWID, JOB_NAME, DUE_DATE,
				DUE_TIME, IS_NOTIFICATION, IS_ALARM, IS_DONE, URGENCY, NOTES };
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null,
				null, null);
		return c;
	}
	
	public long createEntry(String jobId, String itemName, int isChecked) {
		ContentValues cv = new ContentValues();
		cv.put(JOB_ID, jobId);
		cv.put(ITEM_NAME, itemName);
		cv.put(IS_CHECKED, isChecked);
		return ourDatabase.insert(CHECKLIST_TABLE, null, cv);
	}

	public Cursor getAllRows(String jobId) {
		// TODO Auto-generated method stub
		String[] columns = new String[] { CHECK_ID, JOB_ID, ITEM_NAME,
				IS_CHECKED };
		Cursor c = ourDatabase.query(CHECKLIST_TABLE, columns, JOB_ID
				+ " like ?", new String[] { jobId }, null, null, null);
		return c;
	}

}