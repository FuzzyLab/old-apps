package com.fuzzylabs.recurringcalculator;

import java.text.DecimalFormat;

import com.fuzzylabs.recurringcalculator.AboutActivity;
import com.fuzzylabs.recurringcalculator.R;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RecurringActivity extends Activity {

	Button calculate;
	Button sendResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.recurring);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.window_title);

		calculate = (Button) findViewById(R.id.calculate);
		sendResult = (Button) findViewById(R.id.sendResult);

		calculate.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: {
					v.setBackgroundColor(0xFF444444);
					v.invalidate();
					break;
				}
				case MotionEvent.ACTION_UP: {
					v.setBackgroundColor(0xFF222222);
					v.invalidate();
					break;
				}
				}
				return false;
			}
		});

		sendResult.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: {
					v.setBackgroundColor(0xFF444444);
					v.invalidate();
					break;
				}
				case MotionEvent.ACTION_UP: {
					v.setBackgroundColor(0xFF222222);
					v.invalidate();
					break;
				}
				}
				return false;
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recurring, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;
		case R.id.rate:
			String url = "https://play.google.com/store/apps/details?id=com.fuzzylabs.recurringcalculator";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			return true;
		case R.id.share:
			String thisApp = "https://play.google.com/store/apps/details?id=com.fuzzylabs.recurringcalculator";

			String body = "---Recurring Calculator---\n --Fuzzy Labs--"
					+ "\n\n Download this app: " + thisApp;

			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, body);
			sendIntent.setType("text/plain");
			startActivity(Intent.createChooser(sendIntent,
					"Fuzzy Labs | Recurring Calculator"));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void calculateEmi(View view) {

		String instalmentText = ((EditText) findViewById(R.id.instalment))
				.getText().toString();
		String rateText = ((EditText) findViewById(R.id.rate)).getText()
				.toString();
		String yearText = ((EditText) findViewById(R.id.year)).getText()
				.toString();
		String monthText = ((EditText) findViewById(R.id.month)).getText()
				.toString();

		double instalment;
		if (instalmentText.equals(""))
			instalment = 0;
		else
			instalment = Double.parseDouble(instalmentText);

		double rate;
		if (rateText.equals(""))
			rate = 0;
		else
			rate = Double.parseDouble(rateText);

		int year;
		if (yearText.equals(""))
			year = 0;
		else
			year = Integer.parseInt(yearText);

		int month;
		if (monthText.equals(""))
			month = 0;
		else
			month = Integer.parseInt(monthText);

		double r = rate / 400;
		int m = (year * 12) + month;
		int n = m / 3;

		double maturity = 0.0;
		double totalDeposit = 0.0;
		double interest = 0.0;

		if (instalment == 0) {
			r = 0;
			n = 0;
			maturity = 0;
		} else if (n == 0) {
			instalment = 0;
			r = 0;
			maturity = 0;
		} else if (r == 0) {
			maturity = instalment * m;
		} else {
			double x = 1.00;

			for (int i = 0; i < n; i++) {
				x = x * (1 + r);
			}

			double numo = x - 1.0;
			double pow = Math.pow((1 + r), (1.0 / 3.0));
			double neg = 1.0 / pow;
			double prematurity = instalment * numo / (1.0 - neg);
			double extra = m % 3;
			maturity = prematurity * (1 + extra * rate / 1200);
			for (int i = 1; i <= extra; i++) {
				maturity += instalment * (1 + i * rate / 1200);
			}
		}
		totalDeposit = instalment * m;
		interest = maturity - totalDeposit;

		DecimalFormat df = new DecimalFormat("#.##");
		((TextView) findViewById(R.id.maturity)).setText(df.format(maturity)
				.toString());
		((TextView) findViewById(R.id.interest)).setText(df.format(interest)
				.toString());
		((TextView) findViewById(R.id.totalDeposit)).setText(df.format(
				totalDeposit).toString());

	}

	public void sendResult(View view) {
		String instalmentText = ((EditText) findViewById(R.id.instalment))
				.getText().toString();
		if (instalmentText.equals(""))
			instalmentText = "0";
		String rateText = ((EditText) findViewById(R.id.rate)).getText()
				.toString();
		if (rateText.equals(""))
			rateText = "0";
		String yearText = ((EditText) findViewById(R.id.year)).getText()
				.toString();
		if (yearText.equals(""))
			yearText = "0";
		String monthText = ((EditText) findViewById(R.id.month)).getText()
				.toString();
		if (monthText.equals(""))
			monthText = "0";
		String maturityText = ((TextView) findViewById(R.id.maturity))
				.getText().toString();
		if (maturityText.equals(""))
			maturityText = "0";
		String interestText = ((TextView) findViewById(R.id.interest))
				.getText().toString();
		if (interestText.equals(""))
			interestText = "0";

		String thisApp = "https://play.google.com/store/apps/details?id=com.fuzzylabs.recurringcalculator";

		String body = "---Recurring Calculator---\n --Fuzzy Labs--\nMonthly Instalment:\t"
				+ instalmentText
				+ "\nInterest Rate:\t"
				+ rateText
				+ "\nTerm:\t"
				+ yearText
				+ "Yr "
				+ monthText
				+ "Mth\n\nMaturity Amount:\t"
				+ maturityText
				+ "\nTotal Interest:\t"
				+ interestText
				+ "\n\n Download this app: " + thisApp;

		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, body);
		sendIntent.setType("text/plain");
		startActivity(Intent.createChooser(sendIntent, "Recurring Calculation"));
	}

}
