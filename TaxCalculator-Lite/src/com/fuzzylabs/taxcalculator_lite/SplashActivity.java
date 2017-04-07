package com.fuzzylabs.taxcalculator_lite;

import com.fuzzylabs.taxcalculator_lite.R;
import com.fuzzylabs.taxcalculator_lite.SplashActivity;
import com.fuzzylabs.taxcalculator_lite.TaxActivity;

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
							TaxActivity.class));
					finish();
				}
			}, 1000);
		} catch (Exception e) {
		}
		
	}
}
