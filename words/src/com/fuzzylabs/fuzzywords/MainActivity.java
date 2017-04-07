package com.fuzzylabs.fuzzywords;

import java.sql.SQLException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fuzzylabs.fuzzywords.R;

public class MainActivity extends Activity {

	int i, count = 0;
	String data;
	private static final String TAG = "SourabhDB";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void onSwipe(View view) {
		// TODO Auto-generated method stub
		Words info = new Words(getApplicationContext());
		Typeface fonti = Typeface.createFromAsset(getAssets(), "Filmcrypob.ttf");
		TextView label = (TextView) findViewById(R.id.item_text);
		label.setTypeface(fonti);
		label.setTextSize(30);

		try {
			info.open();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		count = info.getCount();
		info.close();
		if (count > 0) {

			try {
				info.open();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			data = info.getData();
			info.close();
			label.setText(data);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_aboutus:
			startActivity(new Intent(this, AboutApp.class));
			return true;
		case R.id.action_rateus:
			String url = "https://play.google.com/store/apps/details?id=com.fuzzylabs.fuzzywords";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			return true;
		case R.id.action_share:
			String thisApp = "https://play.google.com/store/apps/details?id=com.fuzzylabs.fuzzywords";

			String body = "---Fuzzy Words---\n --Fuzzy Labs--"
					+ "\n\n Download this app: " + thisApp;

			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, body);
			sendIntent.setType("text/plain");
			startActivity(Intent.createChooser(sendIntent,
					"Fuzzy Labs | Fuzzy Words"));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void addData(View view) {
		final Dialog d = new Dialog(MainActivity.this);
		Window window = d.getWindow();
		window.setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		d.setContentView(R.layout.add_dialog);
		d.setTitle("Add an Interesting word");
		d.show();
		Button butt = (Button) d.findViewById(R.id.add_button);
		butt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean diditwork = true;
				EditText editText1 = (EditText) d.findViewById(R.id.addword);
				EditText editText2 = (EditText) d.findViewById(R.id.addmean);
				String message1 = editText1.getText().toString();
				String message2 = editText2.getText().toString();
				// String message3 = editText3.getText().toString();
				// intent.putExtra(EXTRA_MESSAGE, message);
				// startActivity(intent);
				Words entry = new Words(MainActivity.this);
				try {
					entry.open();
					entry.createEntry(message1, message2);
					entry.close();
				} catch (SQLException e) {
					diditwork = false;
					e.printStackTrace();
				} finally {
					if (diditwork)
						Toast.makeText(MainActivity.this, "Added",
								Toast.LENGTH_SHORT).show();
					else
						Toast.makeText(MainActivity.this, "Oops",
								Toast.LENGTH_SHORT).show();
					d.dismiss();
				}
			}
		});
	}

	public void viewData(View view) {
		Intent intent = new Intent(this, SelectWordGroup.class);
		startActivity(intent);
	}

	public void onShare(View view) {
		String thisApp = "https://play.google.com/store/apps/details?id=com.fuzzylabs.fuzzywords";
		String body = "***Fuzzy Words***\n **Fuzzy Labs**\n \n" + data
				+ "\n\n Download this app: " + thisApp;

		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, body);
		sendIntent.setType("text/plain");
		startActivity(Intent.createChooser(sendIntent, "Fuzzy Labs | Words"));

	}

	public void insertData(View view) {

		boolean diditwork = true;
		EditText editText1 = (EditText) findViewById(R.id.addword);
		EditText editText2 = (EditText) findViewById(R.id.addmean);
		String message1 = editText1.getText().toString();
		String message2 = editText2.getText().toString();
		Words entry = new Words(MainActivity.this);
		try {
			entry.open();
			entry.createEntry(message1, message2);
			entry.close();
		} catch (SQLException e) {
			diditwork = false;
			e.printStackTrace();
		} finally {
			if (diditwork)
				Toast.makeText(MainActivity.this, "Added", Toast.LENGTH_SHORT)
						.show();
			else
				Toast.makeText(MainActivity.this, "Oops", Toast.LENGTH_SHORT)
						.show();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// info.close();
		super.onDestroy();
	}
}
