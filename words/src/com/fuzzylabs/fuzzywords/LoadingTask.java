package com.fuzzylabs.fuzzywords;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.ProgressBar;
import com.fuzzylabs.fuzzywords.R;

public class LoadingTask extends AsyncTask<String, Integer, Integer> {

	// This is the progress bar you want to update while the task is in progress
	private final ProgressBar progressBar;
	// This is the listener that will be told when this task is finished
	private final LoadingTaskFinishedListener finishedListener;

	public interface LoadingTaskFinishedListener {
		void onTaskFinished();
	}

	public LoadingTask(ProgressBar progressBar,
			LoadingTaskFinishedListener finishedListener) {
		// TODO Auto-generated constructor stub
		this.progressBar = progressBar;
		this.finishedListener = finishedListener;
	}

	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		progressBar.setProgress(values[0]); // This is ran on the UI thread so
											// it is ok to update our progress
											// bar ( a UI view ) here
	}

	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		finishedListener.onTaskFinished(); // Tell whoever was listening we have
											// finished
	}

	@Override
	protected Integer doInBackground(String... params) {

		Words word = new Words(ApplicationContextProvider.getContext());
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(ApplicationContextProvider
						.getContext());
		if (!prefs.getBoolean("firstTime", false)) {
			try {
				word.open();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				word.loadWords();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			word.close();
			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean("firstTime", true);
			editor.commit();
		}
		int count = 4;
		for (int i = 0; i < count; i++) {

			// Update the progress bar after every step
			int progress = (int) ((i / (float) count) * 100);
			publishProgress(progress);

			// Do some long loading things
			try {
				Thread.sleep(500);
			} catch (InterruptedException ignore) {
			}
		}

		return 1234;
	}

	private boolean isDbcreated() {
		// TODO Auto-generated method stub
		boolean check;
		Context mHelperContext = ApplicationContextProvider.getContext();
		final Resources resources = mHelperContext.getResources();
		InputStream inputStream = resources.openRawResource(R.raw.isdbcreated);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		String receiveString = "";
		StringBuilder stringBuilder = new StringBuilder();
		int value = -1;
		try {
			while ((receiveString = reader.readLine()) != null) {
				value = Integer.parseInt(receiveString);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (value <= 0)
			return false;
		else
			return true;
	}

}
