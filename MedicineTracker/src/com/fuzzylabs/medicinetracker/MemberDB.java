package com.fuzzylabs.medicinetracker;

import java.sql.SQLException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MemberDB {

	public static final String KEY_ROWID = "_id";
	public static final String MEMBER_NAME = "member_name";

	private static final String DATABASE_NAME = "membersDb";
	public static final String MEMBER_TABLE = "membersTable";
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
			db.execSQL("CREATE TABLE " + MEMBER_TABLE + " (" + KEY_ROWID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + MEMBER_NAME
					+ " TEXT NOT NULL, UNIQUE (" + MEMBER_NAME
					+ ") ON CONFLICT IGNORE);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + MEMBER_TABLE);
			onCreate(db);
		}

	}

	public MemberDB(Context c) {

		ourContext = c;
	}

	public MemberDB open() throws SQLException {
		ourHelper = new DbHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		ourHelper.close();
	}

	public long createEntry(String memberName) {
		ContentValues cv = new ContentValues();
		cv.put(MEMBER_NAME, memberName);
		return ourDatabase.insert(MEMBER_TABLE, null, cv);

	}

	public int getCount() {
		int count = 0;
		Cursor mCount = ourDatabase.rawQuery(
				"select count(*) from doseTable", null);
		mCount.moveToFirst();
		count = mCount.getInt(0);
		mCount.close();
		return count;
	}

	public Cursor getAllRows() {
		String[] columns = new String[] { KEY_ROWID, MEMBER_NAME };
		Cursor c = ourDatabase.query(MEMBER_TABLE, columns, null, null, null,
				null, null);
		return c;
	}

}