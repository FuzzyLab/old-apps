package com.fuzzylabs.taxcalculator_pro;

import com.fuzzylabs.taxcalculator_pro.R;
import com.fuzzylabs.taxcalculator_pro.SplashActivity;
import com.fuzzylabs.taxcalculator_pro.TaxActivity;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		new Handler().postDelayed(new Runnable() {
			public void run() {
				startActivity(new Intent(SplashActivity.this, TaxActivity.class));
				finish();
			}
		}, 1000);
	}
}
