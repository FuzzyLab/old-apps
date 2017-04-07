package com.fuzzylabs.election2014quiz;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

public class VoteForActivity extends Activity implements OnClickListener {

	private static ElectionDb info;
	Button vote;
	RadioButton radioModi;
	RadioButton radioKejri;
	RadioButton radioNandan;
	RadioButton radioRahul;
	RadioButton radioMulayam;
	TextView statusText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_votefor);

		setTitle("Vote your Leader");

		info = new ElectionDb(getApplicationContext());

		vote = (Button) findViewById(R.id.btnVote);
		vote.setOnClickListener(this);
		radioModi = (RadioButton) findViewById(R.id.radioModi);
		radioKejri = (RadioButton) findViewById(R.id.radioKejri);
		radioNandan = (RadioButton) findViewById(R.id.radioNandan);
		radioRahul = (RadioButton) findViewById(R.id.radioRahul);
		radioMulayam = (RadioButton) findViewById(R.id.radioMulayam);
		statusText = (TextView) findViewById(R.id.statusText);

		vote.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: {
					v.setBackgroundColor(0xFF222222);
					v.invalidate();
					break;
				}
				case MotionEvent.ACTION_UP: {
					v.setBackgroundColor(0xFF444444);
					v.invalidate();
					break;
				}
				}
				return false;
			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.share:
			String thisApp = "https://play.google.com/store/apps/details?id=com.fuzzylabs.election2014quiz";

			String body = "--Fuzzy Labs--\n" + "---Election 2014 Quiz---\n"
					+ "$ Who Am I? Quiz $\n" + "$ Scams Quiz $\n"
					+ "$ General Election Quiz $\n"
					+ "$ Vote Now & Current Standings $\n"
					+ "\n\n Download this app: " + thisApp;

			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, body);
			sendIntent.setType("text/plain");
			startActivity(Intent.createChooser(sendIntent,
					"Fuzzy Labs | Election 2014 Quiz"));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View view) {

		if (!isOnline()) {
			statusText.setText("No Network Connection");
		} else {
			statusText.setText("Loading..");
			String value = "0";
			if (radioModi.isChecked())
				value = "0";
			else if (radioKejri.isChecked())
				value = "1";
			else if (radioNandan.isChecked())
				value = "2";
			else if (radioRahul.isChecked())
				value = "3";
			else if (radioMulayam.isChecked())
				value = "4";

			String url = "http://www.fuzzylabselectionquiz.appspot.com/votefor";
			VoteForTask task = new VoteForTask();
			task.execute(url, value);
		}
	}

	private boolean isOnline() {

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	private class VoteForTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... url) {
			String response = "";
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url[0]);

			List<NameValuePair> params = new ArrayList<NameValuePair>(2);
			params.add(new BasicNameValuePair("type", "add"));
			params.add(new BasicNameValuePair("value", url[1]));
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
				HttpResponse execute = client.execute(httpPost);
				InputStream content = execute.getEntity().getContent();

				info.open();
				ContentValues cv = new ContentValues();
				cv.put(info.INFO_VALUE, "1");
				info.ourDatabase.update(info.INFO_TABLE, cv, info.INFO_KEY
						+ " like ?", new String[] { GenQuiz.VOTED });
				info.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			startActivity(new Intent(VoteForActivity.this,
					VoteShowActivity.class));
			finish();
		}
	}
}
