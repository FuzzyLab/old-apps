package com.fuzzylabs.election2014quiz;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ElectionDb {

	public static final String GEN_QUES_ID = "_id";
	public static final String GEN_QUES = "genQues";
	public static final String GEN_OPT1 = "opt1";
	public static final String GEN_OPT2 = "opt2";
	public static final String GEN_OPT3 = "opt3";
	public static final String GEN_OPT4 = "opt4";
	public static final String GEN_TYPE_ID = "typeId";
	public static final String GEN_LEVEL = "genLevel";
	public static final String GEN_IS_DONE = "genIsDone";
	public static final String GEN_IS_CORRECT = "genIsCorrect";

	public static final String INFO_ID = "_id";
	public static final String INFO_KEY = "infoKey";
	public static final String INFO_VALUE = "infoValue";

	private static final String DATABASE_NAME = "electionDb";
	public static final String GEN_QUIZ_TABLE = "genQuizTable";
	public static final String LEADER_TABLE = "leaderTable";
	public static final String INFO_TABLE = "infoTable";
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

			db.execSQL("CREATE TABLE " + GEN_QUIZ_TABLE + " (" + GEN_QUES_ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + GEN_QUES
					+ " TEXT NOT NULL, " + GEN_OPT1 + " TEXT NOT NULL, "
					+ GEN_OPT2 + " TEXT NOT NULL, " + GEN_OPT3
					+ " TEXT NOT NULL, " + GEN_OPT4 + " TEXT NOT NULL, "
					+ GEN_TYPE_ID + " TEXT NOT NULL, " + GEN_LEVEL
					+ " TEXT NOT NULL, " + GEN_IS_DONE + " INTEGER NOT NULL, "
					+ GEN_IS_CORRECT + " INTEGER NOT NULL);");

			db.execSQL("CREATE TABLE " + INFO_TABLE + " (" + INFO_ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + INFO_KEY
					+ " TEXT NOT NULL, " + INFO_VALUE
					+ " TEXT NOT NULL, UNIQUE (" + INFO_KEY
					+ ") ON CONFLICT REPLACE);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (newVersion < 2) {
				db.execSQL("DROP TABLE IF EXISTS " + GEN_QUIZ_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + LEADER_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + INFO_TABLE);
				onCreate(db);
			}
		}
	}

	public ElectionDb(Context c) {

		ourContext = c;
	}

	public ElectionDb open() throws SQLException {
		ourHelper = new DbHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		ourHelper.close();
	}

	public long createGenQuizEntry(String genQues, String opt1, String opt2,
			String opt3, String opt4, String typeId, String level) {
		ContentValues cv = new ContentValues();
		cv.put(GEN_QUES, genQues);
		cv.put(GEN_OPT1, opt1);
		cv.put(GEN_OPT2, opt2);
		cv.put(GEN_OPT3, opt3);
		cv.put(GEN_OPT4, opt4);
		cv.put(GEN_TYPE_ID, typeId);
		cv.put(GEN_LEVEL, level);
		cv.put(GEN_IS_DONE, 0);
		cv.put(GEN_IS_CORRECT, 0);
		return ourDatabase.insert(GEN_QUIZ_TABLE, null, cv);
	}

	public long createInfoEntry(String key, String value) {
		ContentValues cv = new ContentValues();
		cv.put(INFO_KEY, key);
		cv.put(INFO_VALUE, value);
		return ourDatabase.insert(INFO_TABLE, null, cv);
	}

	public long resetGenQuizTable(String typeId) {
		ContentValues cv = new ContentValues();
		cv.put(GEN_IS_DONE, 0);
		cv.put(GEN_IS_CORRECT, 0);
		return ourDatabase.update(GEN_QUIZ_TABLE, cv, GEN_TYPE_ID + " like ?",
				new String[] { typeId });
	}

	public int getCount(String dbTable) {
		int count = 0;
		Cursor mCount = ourDatabase.rawQuery("select count(*) from " + dbTable,
				null);
		mCount.moveToFirst();
		count = mCount.getInt(0);
		mCount.close();
		return count;
	}

	public int getQuizSize(String typeId) {
		int count = 0;
		String[] columns = new String[] { GEN_QUES_ID };
		Cursor mCount = ourDatabase.query(GEN_QUIZ_TABLE, columns, GEN_TYPE_ID
				+ " like ?", new String[] { typeId }, null, null, null);
		count = mCount.getCount();
		mCount.close();
		return count;
	}

	public int getQuizCorrectSize(String typeId) {
		Cursor cursor = ourDatabase.rawQuery("select count(*) from "
				+ GEN_QUIZ_TABLE + " where " + GEN_TYPE_ID + " = " + typeId
				+ " and " + GEN_IS_CORRECT + " = 1", null);
		cursor.moveToFirst();
		return cursor.getInt(0);
	}

	public int getLevelSize(String typeId, String level) {
		int count = 0;
		String[] columns = new String[] { GEN_QUES_ID };
		Cursor mCount = ourDatabase.query(GEN_QUIZ_TABLE, columns, GEN_TYPE_ID
				+ " like ?" + " and " + GEN_LEVEL + " like ?", new String[] {
				typeId, level }, null, null, null);
		count = mCount.getCount();
		mCount.close();
		return count;
	}

	public int getLevelCorrectSize(String typeId, String level) {
		Cursor cursor = ourDatabase.rawQuery("select count(*) from "
				+ GEN_QUIZ_TABLE + " where " + GEN_TYPE_ID + " = " + typeId
				+ " and " + GEN_LEVEL + " = " + level + " and "
				+ GEN_IS_CORRECT + " = 1", null);
		cursor.moveToFirst();
		return cursor.getInt(0);
	}

	public String getInfo(String key) {
		String[] columns = new String[] { INFO_VALUE };
		Cursor c = ourDatabase.query(INFO_TABLE, columns, INFO_KEY + " like ?",
				new String[] { String.valueOf(key) }, null, null, null);
		c.moveToFirst();
		return c.getString(0);
	}

	public List<Integer> getGenQuizLevelUnansweredList(String typeId,
			String level) {
		String[] columns = new String[] { GEN_QUES_ID };
		Cursor c = ourDatabase.query(GEN_QUIZ_TABLE, columns, GEN_IS_DONE
				+ " like ? and " + GEN_TYPE_ID + " like ? and " + GEN_LEVEL
				+ " like ?", new String[] { "0", typeId, level }, null, null,
				null);
		List<Integer> list = new ArrayList<Integer>();
		while (c.moveToNext()) {
			list.add(c.getInt(0));
		}
		return list;
	}

	public int getGenQuizAnsweredCount(String typeId) {
		String[] columns = new String[] { GEN_QUES_ID };
		Cursor c = ourDatabase.query(GEN_QUIZ_TABLE, columns, GEN_IS_DONE
				+ " like ? and " + GEN_TYPE_ID + " like ?", new String[] { "1",
				typeId }, null, null, null);
		
		return c.getCount();
	}

	public int getGenQuizAllUnansweredCount(String typeId) {
		String[] columns = new String[] { GEN_QUES_ID };
		Cursor c = ourDatabase.query(GEN_QUIZ_TABLE, columns, GEN_IS_DONE
				+ " like ? and " + GEN_TYPE_ID + " like ?", new String[] { "0",
				typeId }, null, null, null);
		return c.getCount();
	}

	public GenQuiz getGenQuizQuestion(int row) {
		String[] columns = new String[] { GEN_QUES, GEN_OPT1, GEN_OPT2,
				GEN_OPT3, GEN_OPT4 };

		Cursor c = ourDatabase.query(GEN_QUIZ_TABLE, columns, GEN_QUES_ID
				+ " like ? AND " + GEN_IS_DONE + " like ?", new String[] {
				String.valueOf(row), "0" }, null, null, null);

		if (c.getCount() == 0) {
			c.close();
			return new GenQuiz();
		} else {
			c.moveToFirst();
			GenQuiz q = new GenQuiz();
			q.questionId = row;
			q.question = c.getString(0);
			q.options[0] = c.getString(1);
			q.options[1] = c.getString(2);
			q.options[2] = c.getString(3);
			q.options[3] = c.getString(4);
			c.close();
			return q;
		}
	}
}