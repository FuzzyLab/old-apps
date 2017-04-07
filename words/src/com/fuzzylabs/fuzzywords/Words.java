package com.fuzzylabs.fuzzywords;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;


//import com.example.android.searchabledict.R;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;
//import android.widget.SimpleCursorAdapter;
import com.fuzzylabs.fuzzywords.R;

public class Words {

	public static final String KEY_ROWID = "_id";
	public static final String KEY_WORD = "word";
	public static final String KEY_MEANING = "word_meaning";
	public static final String KEY_SENTENCE = "sentence";
	private static final String TAG = "SourabhDB";
	private static final String DATABASE_NAME = "Worddb";
	static final String DATABASE_TABLE = "wordTable";
	private static final int DATABASE_VERSION = 1;
	
	private static DbHelper ourHelper;
	private static Context ourContext;
	static SQLiteDatabase ourDatabase;
	private final Context mHelperContext;
	//static Words info;
	public Words (Context c){
		mHelperContext = c;
		ourContext = c;
	}
	
	public  Words open() throws SQLException{
		ourHelper = new DbHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		ourHelper.close();
	}
	
	private  class DbHelper extends SQLiteOpenHelper {

		private SQLiteDatabase mDatabase;
		private Context mHelperContext;
		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			ourContext = context;
			mHelperContext = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			mDatabase = db;
			db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" + 
					KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					KEY_WORD + " TEXT NOT NULL, " + 
					KEY_MEANING + " TEXT NOT NULL);"
				);
		}
	        
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}		 
	}
	// end of DbHelper class
	 void loadWords() throws IOException {
	      Log.d(TAG, "Loading words...");
	       final Resources resources = mHelperContext.getResources();
	       InputStream inputStream = resources.openRawResource(R.raw.definitions);
	       BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	    Words uff = new Words(ApplicationContextProvider.getContext());
	     //  uff.close();
	      // String line
	           String line;
	          try {
	           while ((line = reader.readLine()) != null) {
	               String[] strings = TextUtils.split(line, "-");
	               if (strings.length < 2) continue;
	               Log.d(TAG, "going to insert");
	       		ContentValues cv = new ContentValues();
	    		cv.put(KEY_WORD, strings[0].trim());
	    		cv.put(KEY_MEANING, strings[1].trim());
	    		//cv.put(KEY_SENTENCE, message3);
	    		//   Log.d(TAG, "create entry function" + message1 + message2);
	    		long id =  ourDatabase.insert(DATABASE_TABLE, null, cv);
	              // long id = createEntry(strings[0].trim(), strings[1].trim());
	               Log.d(TAG, "After insert " + id);
	               if (id < 0) {
	                   Log.e(TAG, "unable to add word: " + strings[0].trim());
	               }
	           }
	         //  Log.d(TAG, "after whille loop");
	           } finally {
	     //  	uff.close();
	           reader.close();
	       }
	      // Log.d(TAG, "DONE loading words.");
	   
}
	public   long createEntry(String message1, String message2) {
		// TODO Auto-generated method stub
		//this.ourHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(KEY_WORD, message1);
		cv.put(KEY_MEANING, message2);
		//cv.put(KEY_SENTENCE, message3);
		   Log.d(TAG, "create entry function" + message1 + message2);
		return ourDatabase.insert(DATABASE_TABLE, null, cv);
		
	} 	







	
	public String getData() {
		String[] columns = new String[]{KEY_WORD, KEY_MEANING};
		 Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, "RANDOM()", "1");
			int iword = c.getColumnIndex(KEY_WORD);
			int imean = c.getColumnIndex(KEY_MEANING);
		String result = "";
		if(c.moveToFirst())
			result = result + c.getString(iword) + ": " + "\n\n" + c.getString(imean);
		c.close();
		return result;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		int count = 0;
		Cursor mCount= ourDatabase.rawQuery("select count(*) from wordTable", null);
		mCount.moveToFirst();
		count= mCount.getInt(0);
		mCount.close();
		return count;
	}
	
}

