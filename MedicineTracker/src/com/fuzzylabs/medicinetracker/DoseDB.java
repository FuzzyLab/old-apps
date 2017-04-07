package com.fuzzylabs.medicinetracker;

import java.sql.SQLException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DoseDB {

	public static final String KEY_ROWID = "_id";
	public static final String MEDI_ID = "medi_id";
	public static final String DOSE = "dose";
	public static final String IS_SET = "is_set";
	public static final String IS_DONE = "is_done";
	public static final String SNOOZE_TIME = "snooze_time";
	public static final String SNOOZE_DATE = "snooze_date";
	public static final String SNOOZE_DUE_DATE = "snooze_due_date";

	private static final String DATABASE_NAME = "doseDb";
	static final String DOSE_TABLE = "doseTable";
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
			db.execSQL("CREATE TABLE " + DOSE_TABLE + " ("
					+ KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ MEDI_ID + " TEXT NOT NULL, "
					+ DOSE + " TEXT NOT NULL, "
					+ IS_SET + " INTEGER NOT NULL, "
					+ SNOOZE_DATE + " TEXT NOT NULL, "
					+ SNOOZE_TIME + " TEXT NOT NULL, "
					+ SNOOZE_DUE_DATE + " TEXT NOT NULL, "
					+ IS_DONE + " INTEGER NOT NULL, UNIQUE (" + MEDI_ID + "," + DOSE
					+ ") ON CONFLICT IGNORE);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if(newVersion < 2) {
				db.execSQL("DROP TABLE IF EXISTS " + DOSE_TABLE);
				onCreate(db);
			} else if(newVersion < 3) {
				db.execSQL("ALTER TABLE " + DOSE_TABLE + " ADD COLUMN "
						  + SNOOZE_DATE + " TEXT NOT NULL DEFAULT '';"
						);
				db.execSQL("ALTER TABLE " + DOSE_TABLE + " ADD COLUMN "
			              + SNOOZE_TIME + " TEXT NOT NULL DEFAULT '';"
						);
				db.execSQL("ALTER TABLE " + DOSE_TABLE + " ADD COLUMN "
			              + SNOOZE_DUE_DATE + " TEXT NOT NULL DEFAULT '';"
						);
			}
		}

	}

	public DoseDB(Context c) {

		ourContext = c;
	}

	public DoseDB open() throws SQLException {
		ourHelper = new DbHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		ourHelper.close();
	}

	public long createEntry(String mediId, String dose, int is_set,
			int is_done) {
		ContentValues cv = new ContentValues();
		cv.put(MEDI_ID, mediId);
		cv.put(DOSE, dose);
		cv.put(IS_SET, is_set);
		cv.put(IS_DONE, is_done);
		cv.put(SNOOZE_DATE, "");
		cv.put(SNOOZE_TIME, "");
		cv.put(SNOOZE_DUE_DATE, "");
		return ourDatabase.insert(DOSE_TABLE, null, cv);
	}

	public int getCount() {
		int count = 0;
		Cursor mCount = ourDatabase.rawQuery("select count(*) from doseTable",
				null);
		mCount.moveToFirst();
		count = mCount.getInt(0);
		mCount.close();
		return count;
	}

	public Cursor getAllRows(String where, String[] args) {
		String[] columns = new String[] { KEY_ROWID, MEDI_ID, DOSE, IS_SET,
				IS_DONE, SNOOZE_DATE, SNOOZE_TIME, SNOOZE_DUE_DATE };
		Cursor c = ourDatabase.query(DOSE_TABLE, columns, where, args, null,
				null, null);
		return c;
	}

}