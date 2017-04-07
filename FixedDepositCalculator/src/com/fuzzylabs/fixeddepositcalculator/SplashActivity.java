package com.fuzzylabs.fixeddepositcalculator;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import com.fuzzylabs.fixeddepositcalculator.R;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		try {
			new Handler().postDelayed(new Runnable() {
				public void run() {
					startActivity(new Intent(SplashActivity.this,
							FixedActivity.class));
					finish();
				}
			}, 1000);
		} catch (Exception e) {
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

}
