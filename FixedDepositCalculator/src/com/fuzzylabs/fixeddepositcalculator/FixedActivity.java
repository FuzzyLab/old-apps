package com.fuzzylabs.fixeddepositcalculator;

import java.text.DecimalFormat;

import com.fuzzylabs.fixeddepositcalculator.AboutActivity;
import com.fuzzylabs.fixeddepositcalculator.R;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class FixedActivity extends Activity {

	String compounded;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fixed);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fixed, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;
		case R.id.rate:
			String url = "https://play.google.com/store/apps/details?id=com.fuzzylabs.fixeddepositcalculator";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			return true;
		case R.id.share:
			String thisApp = "https://play.google.com/store/apps/details?id=com.fuzzylabs.fixeddepositcalculator";

			String body = "---Fixed Deposit Calculator---\n --Fuzzy Labs--"
					+ "\n\n Download this app: " + thisApp;

			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, body);
			sendIntent.setType("text/plain");
			startActivity(Intent.createChooser(sendIntent,
					"Fuzzy Labs | Fixed Deposit Calculator"));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void calculateEmi(View view) {

		String fixedText = ((EditText) findViewById(R.id.fixed)).getText()
				.toString();
		String rateText = ((EditText) findViewById(R.id.rate)).getText()
				.toString();
		String yearText = ((EditText) findViewById(R.id.year)).getText()
				.toString();
		String monthText = ((EditText) findViewById(R.id.month)).getText()
				.toString();

		double fixed;
		if (fixedText.equals(""))
			fixed = 0;
		else
			fixed = Double.parseDouble(fixedText);

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

		int div = 1;
		int mul = 1;

		if (((RadioButton) findViewById(R.id.monthly)).isChecked()) {
			div = 12;
			mul = 1;
			compounded = "Monthly";
		} else if (((RadioButton) findViewById(R.id.quarterly)).isChecked()) {
			div = 4;
			mul = 3;
			compounded = "Quartly";
		} else if (((RadioButton) findViewById(R.id.halfYearly)).isChecked()) {
			div = 2;
			mul = 6;
			compounded = "Half Yearly";
		} else if (((RadioButton) findViewById(R.id.yearly)).isChecked()) {
			div = 1;
			mul = 12;
			compounded = "Yearly";
		}

		double r = rate / 100 / div;
		int m = (year * 12) + month;
		int n = m / mul;

		double maturity;
		double interest;

		if (fixed == 0) {
			r = 0;
			n = 0;
			maturity = 0;
		} else if (n == 0) {
			fixed = 0;
			r = 0;
			maturity = 0;
		} else if (r == 0) {
			maturity = fixed;
		} else {
			double numo = 1.00;
			for (int i = 0; i < n; i++) {
				numo = numo * (1 + r);
			}

			maturity = fixed * numo;
			double extra = m % mul;
			maturity = maturity * (1 + extra * rate / 1200);
		}
		interest = maturity - fixed;

		DecimalFormat df = new DecimalFormat("#.##");
		((TextView) findViewById(R.id.maturity)).setText(df.format(maturity)
				.toString());
		((TextView) findViewById(R.id.interest)).setText(df.format(interest)
				.toString());
	}

	public void sendResult(View view) {
		String fixedText = ((EditText) findViewById(R.id.fixed)).getText()
				.toString();
		if (fixedText.equals(""))
			fixedText = "0";
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

		String thisApp = "https://play.google.com/store/apps/details?id=com.fuzzylabs.fixeddepositcalculator";

		String body = "---Fixed Deposite Calculator---\n --Fuzzy Labs--\nFixed Amount:\t"
				+ fixedText
				+ "\nInterest Rate:\t"
				+ rateText
				+ "\nCompounded:\t"
				+ compounded
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
		startActivity(Intent.createChooser(sendIntent,
				"Fixed Deposit Calculation"));
	}

}
