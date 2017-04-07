package com.fuzzylabs.election2014quiz;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

public class SplashActivity extends Activity {

	private ElectionDb info;
	private Resources resources;
	private InputStream inputStream;
	private String line;
	private String[] strings;
	private BufferedReader reader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		info = new ElectionDb(getApplicationContext());

		OnStartTask ots = new OnStartTask();
		ots.execute();
	}

	private class OnStartTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... url) {
			try {
				info.open();
				int isInserted = info.getCount(info.INFO_TABLE);
				if (isInserted == 0) {
					resources = getApplicationContext().getResources();
					inputStream = resources.openRawResource(R.raw.general);
					reader = new BufferedReader(new InputStreamReader(
							inputStream));
					while ((line = reader.readLine()) != null) {
						strings = TextUtils.split(line, ";;");
						if (strings.length < 7)
							continue;
						info.createGenQuizEntry(strings[0].trim(), strings[1].trim(),
								strings[2].trim(), strings[3].trim(), strings[4].trim(), strings[5].trim(),
								strings[6].trim());
					}
					reader.close();

					info.createInfoEntry(GenQuiz.DB_SET, "1");
					info.createInfoEntry(GenQuiz.VOTED, "0");
					info.createInfoEntry(GenQuiz.QUIZ_TYPE_1, "1");
					info.createInfoEntry(GenQuiz.QUIZ_TYPE_2, "1");
					info.createInfoEntry(GenQuiz.QUIZ_TYPE_3, "1");
				} else {
					publishProgress(5);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				info.close();
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean bool) {
			new Handler().postDelayed(new Runnable() {
				public void run() {
					startActivity(new Intent(SplashActivity.this,
							SelectQuizActivity.class));
					finish();
				}
			}, 1000);
		}
	}
}
