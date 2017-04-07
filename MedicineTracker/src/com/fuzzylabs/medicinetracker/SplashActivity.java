package com.fuzzylabs.medicinetracker;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		try {
			new Handler().postDelayed(new Runnable() {
				public void run() {
					startActivity(new Intent(SplashActivity.this,
							MedicineActivity.class));
					finish();
				}
			}, 1000);
		} catch (Exception e) {
		}
	}
}
