package com.fuzzylabs.expensemanager;

import java.sql.SQLException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ExpensesDB {

	public static final String KEY_EXPENSEID = "_eid";
	public static final String BILL_AMOUNT = "billAmount";
	public static final String BILL_DATE = "billDate";
	public static final String CATEGORY_ID = "categoryId";
	public static final String NOTE = "note";
	
	public static final String KEY_CATEGORYID = "_cid";
	public static final String CATEGORY_NAME = "categoryName";

	private static final String DATABASE_NAME = "expensesDb";
	static final String EXPENSE_TABLE = "expenseTable";
	static final String CATEGORY_TABLE = "categoryTable";
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
			db.execSQL("CREATE TABLE " + EXPENSE_TABLE + " (" + KEY_EXPENSEID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + BILL_AMOUNT
					+ " TEXT NOT NULL, " + BILL_DATE + " TEXT NOT NULL, "
					+ CATEGORY_ID + " TEXT NOT NULL, " + NOTE
					+ " TEXT NOT NULL, " + "ON CONFLICT IGNORE);");
			
			db.execSQL("CREATE TABLE " + CATEGORY_TABLE + " (" + KEY_CATEGORYID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + CATEGORY_NAME
					+ " TEXT NOT NULL, " + "ON CONFLICT IGNORE);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + EXPENSE_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE);
			onCreate(db);
		}

	}

	public ExpensesDB(Context c) {

		ourContext = c;
	}

	public ExpensesDB open() throws SQLException {
		ourHelper = new DbHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		ourHelper.close();
	}

	public long addExpense(String billAmount, String billDate, String categoryId,
			String note) {
		ContentValues cv = new ContentValues();
		cv.put(BILL_AMOUNT, billAmount);
		cv.put(BILL_DATE, billDate);
		cv.put(CATEGORY_ID, categoryId);
		cv.put(NOTE, note);
		return ourDatabase.insert(EXPENSE_TABLE, null, cv);

	}
	
	public long addCategory(String categoryName) {
		ContentValues cv = new ContentValues();
		cv.put(CATEGORY_NAME, categoryName);
		return ourDatabase.insert(CATEGORY_TABLE, null, cv);
	}

	public int getExpensesCount() {
		int count = 0;
		Cursor mCount = ourDatabase.rawQuery(
				"select count(*) from " + EXPENSE_TABLE, null);
		mCount.moveToFirst();
		count = mCount.getInt(0);
		mCount.close();
		return count;
	}

	public Cursor getAllExpenses(String where, String[] args) {
		String[] columns = new String[] { KEY_EXPENSEID, BILL_AMOUNT, BILL_DATE,
				NOTE };
		Cursor c = ourDatabase.query(EXPENSE_TABLE, columns, where, args,
				null, null, null);
		return c;
	}
	
	public Cursor getAllCategories(String where, String[] args) {
		String[] columns = new String[] { KEY_CATEGORYID, CATEGORY_NAME };
		Cursor c = ourDatabase.query(CATEGORY_TABLE, columns, where, args,
				null, null, null);
		return c;
	}
}