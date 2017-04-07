package com.fuzzylabs.currencyconverter;

import java.sql.SQLException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Currencies {

	public static final String KEY_ROWID = "_id";
	public static final String FROM_CURR = "fromCurr";
	public static final String TO_CURR = "toCurr";
	public static final String VALUE = "value";

	private static final String DATABASE_NAME = "currencyDb";
	static final String DATABASE_TABLE = "currTable";
	private static final int DATABASE_VERSION = 1;

	private DbHelper ourHelper;
	private final Context ourContext;
	SQLiteDatabase ourDatabase;

	private static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" + KEY_ROWID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + FROM_CURR
					+ " TEXT NOT NULL, " + TO_CURR + " TEXT NOT NULL, " + VALUE
					+ " TEXT NOT NULL, UNIQUE (" + FROM_CURR + ", "
					+ TO_CURR + ") ON CONFLICT REPLACE);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}

	}

	public Currencies(Context c) {

		ourContext = c;
	}

	public Currencies open() throws SQLException {
		ourHelper = new DbHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		ourHelper.close();
	}

	public long createEntry(String message1, String message2, String message3) {
		// TODO Auto-generated method stub
		ContentValues cv = new ContentValues();
		cv.put(FROM_CURR, message1);
		cv.put(TO_CURR, message2);
		cv.put(VALUE, message3);
		return ourDatabase.insert(DATABASE_TABLE, null, cv);

	}

	public int getCount() {
		// TODO Auto-generated method stub
		int count = 0;
		Cursor mCount = ourDatabase.rawQuery("select count(*) from currTable",
				null);
		mCount.moveToFirst();
		count = mCount.getInt(0);
		mCount.close();
		return count;
	}

	public Cursor getAllRows() {
		// TODO Auto-generated method stub
		String[] columns = new String[] { KEY_ROWID, FROM_CURR, TO_CURR, VALUE };
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null,
				null, null);
		return c;
	}

}