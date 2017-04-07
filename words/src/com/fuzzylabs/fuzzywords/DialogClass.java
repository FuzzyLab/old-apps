package com.fuzzylabs.fuzzywords;

import java.sql.SQLException;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.TextView;
import com.fuzzylabs.fuzzywords.R;

public class DialogClass extends Activity {
	Words info = new Words(getApplicationContext());
	
	public void deleteData() {
		boolean diditwork;
		ViewWordsActivity view = new ViewWordsActivity();
		try {
			info.open();

			info.ourDatabase.delete(info.DATABASE_TABLE, info.KEY_WORD + "=?", new String[] { view.word });
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			diditwork = false;
    		Dialog d = new Dialog(this);
    		d.setTitle("Oops!");
    		TextView tv = new TextView(this);
    		String error = e.toString();
    		tv.setText(error);
    		d.setContentView(tv);
    		d.show();
			e.printStackTrace();
		}
		finally{
    		Dialog d = new Dialog(this);
    		d.setTitle("Deleted!");
    		TextView tv = new TextView(this);
    		tv.setText("SUCCESS");
    		d.setContentView(tv);
    		d.show();
    		view.populateFromDB();
		}
		
}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.dialoglayout);
		super.onCreate(savedInstanceState);
	}
}
