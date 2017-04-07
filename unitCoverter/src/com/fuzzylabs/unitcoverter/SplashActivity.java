package com.fuzzylabs.unitcoverter;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import com.fuzzylabs.unitcoverter.R;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		try {
			new Handler().postDelayed(new Runnable() {
				public void run() {
					startActivity(new Intent(SplashActivity.this,
							MainActivity.class));
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
