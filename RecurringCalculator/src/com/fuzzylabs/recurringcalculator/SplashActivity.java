package com.fuzzylabs.recurringcalculator;

import com.fuzzylabs.recurringcalculator.RecurringActivity;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		try {
			new Handler().postDelayed(new Runnable() {
				public void run() {
					startActivity(new Intent(SplashActivity.this,
							RecurringActivity.class));
					finish();
				}
			}, 1000);
		} catch (Exception e) {
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

}
