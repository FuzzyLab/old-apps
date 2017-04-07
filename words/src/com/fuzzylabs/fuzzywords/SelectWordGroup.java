package com.fuzzylabs.fuzzywords;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.fuzzylabs.fuzzywords.R;

public class SelectWordGroup extends Activity implements OnClickListener{

	//String sendWhere;
	public static int g_buttonPressed=0;
	//String[] argsWhere;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_word_group);
		Button b1 = (Button)findViewById(R.id.button1);
		Button b2 = (Button)findViewById(R.id.button2);
		Button b3 = (Button)findViewById(R.id.button3);
		Button b4 = (Button)findViewById(R.id.button4);
		Button b5 = (Button)findViewById(R.id.button5);
		Button b6 = (Button)findViewById(R.id.button6);
		
		b1.setOnClickListener(this);
		b2.setOnClickListener(this);
		b3.setOnClickListener(this);
		b4.setOnClickListener(this);
		b5.setOnClickListener(this);
		b6.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Words word = new Words(this);
		switch(arg0.getId())
		{	

		case R.id.button1:
			g_buttonPressed = 1;
			break;
		case R.id.button2:
			g_buttonPressed = 2;

			break;
		case R.id.button3:
			g_buttonPressed = 3;
			break;
		case R.id.button4:
			g_buttonPressed = 4;
			break;
		case R.id.button5:
			g_buttonPressed = 5;
			break;
		case R.id.button6:
			g_buttonPressed = 6;
			break;
			default:
				break;
		}
    	Intent intent = new Intent(this, ViewWordsActivity.class);
    	startActivity(intent);
	}
}
