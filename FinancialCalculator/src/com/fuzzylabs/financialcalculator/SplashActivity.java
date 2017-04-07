package com.fuzzylabs.financialcalculator;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import com.fuzzylabs.financialcalculator.R;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_splash);
		try {
			new Handler().postDelayed(new Runnable() {
				public void run() {
					startActivity(new Intent(SplashActivity.this,
							HomeActivity.class));
					finish();
				}
			}, 1000);
		} catch (Exception e) {
		}
	}
}
