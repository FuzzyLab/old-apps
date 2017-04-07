package com.fuzzylabs.fuzzywords;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.fuzzylabs.fuzzywords.LoadingTask.LoadingTaskFinishedListener;
import com.fuzzylabs.fuzzywords.R;

public class SplashActivity  extends Activity implements LoadingTaskFinishedListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Show the splash screen
        setContentView(R.layout.splash);
        // Find the progress bar
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.activity_splash_progress_bar);
        // Start your loading
        new LoadingTask(progressBar, this).execute("String"); // Pass in whatever you need a url is just an example we don't use it in this tutorial
    }
	@Override
	public void onTaskFinished() {
		// TODO Auto-generated method stub
		completeSplash();
	}

	private void completeSplash() {
		// TODO Auto-generated method stub
		startApp();
		finish();
	}

	private void startApp() {
		// TODO Auto-generated method stub
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);		
	}

}
