package com.fuzzylabs.fuzzywords;

import java.sql.SQLException;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.fuzzylabs.fuzzywords.R;

public class Update extends Activity{

	Words info = new Words(this);
	String word1;
	String mean1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_activity);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    word1 = extras.getString("word1");
		    mean1 = extras.getString("mean1");
		    
		    EditText ed1 = (EditText) findViewById(R.id.editupdate1);
		    EditText ed2 = (EditText) findViewById(R.id.editupdate2);
		    ed1.setText(word1, TextView.BufferType.EDITABLE);
		    ed2.setText(mean1, TextView.BufferType.EDITABLE);
	        
		}
	}
public void updateData(View v){
	
    EditText ed1 = (EditText) findViewById(R.id.editupdate1);
    EditText ed2 = (EditText) findViewById(R.id.editupdate2);
    
	String message1 = ed1.getText().toString();
	String message2 = ed2.getText().toString();
	boolean success = true;
    try {
		info.open();
		ContentValues cv = new ContentValues();
		cv.put(info.KEY_WORD, message1);
		cv.put(info.KEY_MEANING, message2);
		String where = info.KEY_WORD + " LIKE ?";
		String[] arg = {word1};
		info.ourDatabase.update(info.DATABASE_TABLE, cv, where, arg);
		info.close();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		success = false;
		e.printStackTrace();
	}finally{
		if(success)
		Toast.makeText(Update.this, "Updated!", Toast.LENGTH_SHORT).show();
		Intent i = getIntent();
		setResult(RESULT_OK, i);
		finish();
		//ViewWordsActivity view =  new ViewWordsActivity();
		//view.populateFromDB();
	}
}
}
