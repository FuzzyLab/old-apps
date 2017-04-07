
package com.fuzzylabs.medicinetracker;

import java.sql.SQLException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MedicineDB {

	public static final String KEY_ROWID = "_id";
	public static final String MEDI_NAME = "mediName";
	public static final String INTERVAL = "interval";
	public static final String DUE_DATE = "dueDate";
	public static final String START_DATE = "startDate";
	public static final String DUE_TIME = "dueTime";
	public static final String SNOOZE_TIME = "snoozeTime";
	public static final String FOR_WHO = "forWho";
	public static final String IS_NOTIFICATION = "isNotification";
	public static final String NOTES = "notes";
	public static final String DOSE_AMOUNT = "doseAmount";
	public static final String DOSE_UNIT = "doseUnit";
	
	public static final String MISSED_MEDI_ID = "missedMediId";
	public static final String MISSED_MEDI_DATE = "missedMediDate";
	public static final String MISSED_MEDI_TIME = "missedMediTime";

	private static final String DATABASE_NAME = "medicineDb";
	static final String MEDICINE_TABLE = "medicineTable";
	static final String MISSED_DOSE_TABLE = "missedDoseTable";
	private static final int DATABASE_VERSION = 3;

	private DbHelper ourHelper;
	private final Context ourContext;
	SQLiteDatabase ourDatabase;

	private static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + MEDICINE_TABLE + " ("
					+ KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ MEDI_NAME + " TEXT NOT NULL, "
					+ INTERVAL + " INTEGER NOT NULL, "
					+ DOSE_AMOUNT + " INTEGER NOT NULL, "
					+ DOSE_UNIT + " INTEGER NOT NULL, "
					+ NOTES + " TEXT NOT NULL, "
					+ DUE_DATE + " TEXT NOT NULL, "
					+ START_DATE+ " TEXT NOT NULL, "
					+ DUE_TIME + " TEXT NOT NULL, "
					+ SNOOZE_TIME + " TEXT NOT NULL, "
					+ FOR_WHO + " TEXT NOT NULL, "
					+ IS_NOTIFICATION + " INTEGER NOT NULL, "
					+ "UNIQUE (" + MEDI_NAME + ", " + FOR_WHO + ") ON CONFLICT IGNORE);");
			
			db.execSQL("CREATE TABLE " + MISSED_DOSE_TABLE + " ("
					+ KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ MISSED_MEDI_ID + " TEXT NOT NULL, "
					+ MISSED_MEDI_DATE + " TEXT NOT NULL, "
					+ MISSED_MEDI_TIME + " TEXT NOT NULL, "
					+ "UNIQUE (" + MISSED_MEDI_ID + "," + MISSED_MEDI_DATE + "," + MISSED_MEDI_TIME + ") ON CONFLICT IGNORE);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (newVersion < 2) {
				db.execSQL("DROP TABLE IF EXISTS " + MEDICINE_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + MISSED_DOSE_TABLE);
				onCreate(db);
			} else if(newVersion < 3) {
				db.execSQL("ALTER TABLE " + MEDICINE_TABLE + " ADD COLUMN "
						  + SNOOZE_TIME + " TEXT NOT NULL DEFAULT '';"
						);
				db.execSQL("ALTER TABLE " + MEDICINE_TABLE + " ADD COLUMN "
			              + DOSE_AMOUNT + " INTEGER NOT NULL DEFAULT 1;"
						);
				db.execSQL("ALTER TABLE " + MEDICINE_TABLE + " ADD COLUMN "
			              + DOSE_UNIT + " INTEGER NOT NULL DEFAULT 0;"
						);
				db.execSQL("ALTER TABLE " + MEDICINE_TABLE + " ADD COLUMN "
			              + NOTES + " TEXT NOT NULL DEFAULT '';"
						);
			    
			    db.execSQL("CREATE TABLE " + MISSED_DOSE_TABLE + " ("
						+ KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
						+ MISSED_MEDI_ID + " TEXT NOT NULL, "
						+ MISSED_MEDI_DATE + " TEXT NOT NULL, "
						+ MISSED_MEDI_TIME + " TEXT NOT NULL, "
						+ "UNIQUE (" + MISSED_MEDI_ID + "," + MISSED_MEDI_DATE + "," + MISSED_MEDI_TIME + ") ON CONFLICT IGNORE);");
			} else if(newVersion < 4) {
				ContentValues cv = new ContentValues();
				cv.put(SNOOZE_TIME, "");
				db.update(MEDICINE_TABLE, cv, null, null);
			}
		}
	}

	public MedicineDB(Context c) {

		ourContext = c;
	}

	public MedicineDB open() throws SQLException {
		ourHelper = new DbHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		ourHelper.close();
	}

	public long createEntry(String mediName, int interval, String dueDate,
			String startDate, String dueTime, String forWho, int isNotification, int doseAmount,
			int doseUnit, String notes) {
		ContentValues cv = new ContentValues();
		cv.put(MEDI_NAME, mediName);
		cv.put(INTERVAL, interval);
		cv.put(DUE_DATE, dueDate);
		cv.put(START_DATE, startDate);
		cv.put(DUE_TIME, dueTime);
		cv.put(FOR_WHO, forWho);
		cv.put(SNOOZE_TIME, "");
		cv.put(IS_NOTIFICATION, isNotification);
		cv.put(DOSE_AMOUNT, doseAmount);
		cv.put(DOSE_UNIT, doseUnit);
		cv.put(NOTES, notes);
		return ourDatabase.insert(MEDICINE_TABLE, null, cv);
	}
	
	public long createMissedEntry(String missedMediId, String missedMediDate, String missedMediTime) {
		ContentValues cv = new ContentValues();
		cv.put(MISSED_MEDI_ID, missedMediId);
		cv.put(MISSED_MEDI_DATE, missedMediDate);
		cv.put(MISSED_MEDI_TIME, missedMediTime);
		return ourDatabase.insert(MISSED_DOSE_TABLE, null, cv);
	}

	public int getCount() {
		int count = 0;
		Cursor mCount = ourDatabase.rawQuery(
				"select count(*) from medicineTable", null);
		mCount.moveToFirst();
		count = mCount.getInt(0);
		mCount.close();
		return count;
	}

	public Cursor getAllRows(String where, String[] args) {
		String[] columns = new String[] { KEY_ROWID, MEDI_NAME, INTERVAL,
				DUE_DATE, START_DATE, DUE_TIME, FOR_WHO, IS_NOTIFICATION, DOSE_AMOUNT, DOSE_UNIT, NOTES };
		Cursor c = ourDatabase.query(MEDICINE_TABLE, columns, where, args,
				null, null, null);
		return c;
	}

	public Cursor getAllDosesMissed(String where, String[] args) {
		String[] columns = new String[] { KEY_ROWID, MISSED_MEDI_ID, MISSED_MEDI_DATE, MISSED_MEDI_TIME };
		Cursor c = ourDatabase.query(MISSED_DOSE_TABLE, columns, where, args,
				null, null, null);
		return c;
	}
}