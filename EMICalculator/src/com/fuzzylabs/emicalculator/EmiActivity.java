package com.fuzzylabs.emicalculator;

import java.text.DecimalFormat;

import com.fuzzylabs.emicalculator.R;
import com.fuzzylabs.emicalculator.AboutActivity;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

public class EmiActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_emi);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.window_title);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.emi, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;
		case R.id.rate:
			String url = "https://play.google.com/store/apps/details?id=com.fuzzylabs.emicalculator";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
			return true;
		case R.id.share:
			String thisApp = "https://play.google.com/store/apps/details?id=com.fuzzylabs.emicalculator";

			String body = "---EMI Calculator---\n --Fuzzy Labs--"
					+ "\n\n Download this app: "
					+ thisApp;

			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, body);
			sendIntent.setType("text/plain");
			startActivity(Intent.createChooser(sendIntent, "Fuzzy Labs | Emi Calculator"));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void calculateEmi(View view) {
		String principalText = ((EditText) findViewById(R.id.principal))
				.getText().toString();
		String rateText = ((EditText) findViewById(R.id.rate)).getText()
				.toString();
		String yearText = ((EditText) findViewById(R.id.year)).getText()
				.toString();
		String monthText = ((EditText) findViewById(R.id.month)).getText()
				.toString();

		double principal;
		if (principalText.equals(""))
			principal = 0;
		else
			principal = Double.parseDouble(principalText);

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

		double r = (rate / 12) / 100;
		int n = (year * 12) + month;

		double emi;
		double totalPay;
		double interest;

		if (principal == 0) {
			r = 0;
			n = 0;
			emi = 0;
		} else if (n == 0) {
			principal = 0;
			r = 0;
			emi = 0;
		} else if (r == 0) {
			emi = principal / n;
		} else {
			double x = 1.00;

			for (int i = 0; i < n; i++) {
				x = x * (1 + r);
			}

			double deno = x - 1;

			emi = principal * r * x / deno;
		}

		totalPay = emi * n;
		interest = totalPay - principal;

		DecimalFormat df = new DecimalFormat("#.##");
		((TextView) findViewById(R.id.emi)).setText(df.format(emi).toString());
		((TextView) findViewById(R.id.interest)).setText(df.format(interest)
				.toString());
		((TextView) findViewById(R.id.totalPayment)).setText(df
				.format(totalPay).toString());
	}

	public void sendResult(View view) {
		String principalText = ((EditText) findViewById(R.id.principal))
				.getText().toString();
		if (principalText.equals(""))
			principalText = "0";
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
		String emiText = ((TextView) findViewById(R.id.emi)).getText()
				.toString();
		if (emiText.equals(""))
			emiText = "0";
		String totalText = ((TextView) findViewById(R.id.totalPayment))
				.getText().toString();
		if (totalText.equals(""))
			totalText = "0";

		String thisApp = "https://play.google.com/store/apps/details?id=com.fuzzylabs.emicalculator";

		String body = "---EMI Calculator---\n --Fuzzy Labs--\nPrincipal Amount:\t"
				+ principalText
				+ "\nInterest Rate:\t"
				+ rateText
				+ "\nTerm:\t"
				+ yearText
				+ "Yr "
				+ monthText
				+ "Mth\n\nEMI:\t"
				+ emiText
				+ "\nTotal Payment:\t"
				+ totalText
				+ "\n\n Download this app: "
				+ thisApp;

		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, body);
		sendIntent.setType("text/plain");
		startActivity(Intent.createChooser(sendIntent, "Emi Calculation"));
	}

}
