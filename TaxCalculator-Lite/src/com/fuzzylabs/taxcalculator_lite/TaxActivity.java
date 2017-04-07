package com.fuzzylabs.taxcalculator_lite;

import java.text.DecimalFormat;

import com.fuzzylabs.taxcalculator_lite.R;
import com.fuzzylabs.taxcalculator_lite.AboutActivity;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.*;

public class TaxActivity extends Activity {

	private EditText grossIncomeView;
	private EditText hraView;
	private EditText investmentView;
	private TextView taxRateView;
	private TextView educessView;
	private TextView incometaxView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_tax);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.window_title);

		grossIncomeView = ((EditText) findViewById(R.id.grossSalary));
		hraView = ((EditText) findViewById(R.id.hra));
		investmentView = ((EditText) findViewById(R.id.investment));
		taxRateView = ((TextView) findViewById(R.id.taxRate));
		educessView = ((TextView) findViewById(R.id.eduCess));
		incometaxView = ((TextView) findViewById(R.id.incomeTax));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tax, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;
		case R.id.rate:
			String url = "https://play.google.com/store/apps/details?id=com.fuzzylabs.taxcalculator_lite";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			return true;
		case R.id.share:
			String thisApp = "https://play.google.com/store/apps/details?id=com.fuzzylabs.taxcalculator_lite";

			String body = "---Tax Calculator Lite---\n --Fuzzy Labs--"
					+ "\n\n Download this app: " + thisApp;

			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, body);
			sendIntent.setType("text/plain");
			startActivity(Intent.createChooser(sendIntent,
					"Fuzzy Labs | Tax Calculator Lite"));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void calculateTax(View view) {

		String grossSalaryText = grossIncomeView.getText().toString();
		String hraText = hraView.getText().toString();
		String investmentText = investmentView.getText().toString();

		double grossSalary;
		if (grossSalaryText.equals(""))
			grossSalary = 0;
		else
			grossSalary = Double.parseDouble(grossSalaryText);

		double hra;
		if (hraText.equals(""))
			hra = 0;
		else
			hra = Double.parseDouble(hraText);

		double investment;
		if (investmentText.equals(""))
			investment = 0;
		else
			investment = Double.parseDouble(investmentText);

		double totalDeduction = investment + hra;

		double salary = grossSalary - totalDeduction;
		double tax;
		int rate = 0;

		if (((RadioButton) findViewById(R.id.below60)).isChecked()) {
			if (salary <= 250000)
				tax = 0;
			else if (salary <= 500000) {
				tax = (salary - 250000) * 0.1;
				rate = 10;
				if(tax < 2000)
					tax = 0;
				else
					tax = tax - 2000;
			}
			else if (salary <= 1000000) {
				tax = 25000 + (salary - 500000) * 0.2;
				rate = 20;
			}
			else if (salary <= 10000000) {
				tax = 125000 + (salary - 1000000) * 0.3;
				rate = 30;
			}
			else {
				tax = 2825000 + (salary - 10000000) * 0.33;
				rate = 33;
			}
		}

		else if (((RadioButton) findViewById(R.id.seniorBelow80)).isChecked()) {
			if (salary <= 300000)
				tax = 0;
			else if (salary <= 500000) {
				tax = (salary - 300000) * 0.1;
				rate = 10;
				if(tax < 2000)
					tax = 0;
				else
					tax = tax - 2000;
			}
			else if (salary <= 1000000) {
				tax = 20000 + (salary - 500000) * 0.2;
				rate = 20;
			}
			else if (salary <= 10000000) {
				tax = 120000 + (salary - 1000000) * 0.3;
				rate = 30;
			}
			else {
				tax = 2820000 + (salary - 10000000) * 0.33;
				rate = 33;
			}
		}

		else {
			if (salary <= 500000)
				tax = 0;
			else if (salary <= 1000000) {
				tax = (salary - 500000) * 0.2;
				rate = 20;
			}
			else if (salary <= 10000000) {
				tax = 100000 + (salary - 1000000) * 0.3;
				rate = 30;
			}
			else {
				tax = 2700000 + (salary - 10000000) * 0.33;
				rate = 33;
			}
		}

		double eduCess = 0.03 * tax;
		double incomeTax = tax + eduCess;

		DecimalFormat df = new DecimalFormat("#.##");

		taxRateView.setText(String.valueOf(rate)+" %");
		educessView.setText(String.valueOf(df.format(eduCess)));
		incometaxView.setText(String.valueOf(df.format(incomeTax)));

	}

	public void reset(View view) {
		grossIncomeView.setText("");
		hraView.setText("");
		investmentView.setText("");
		taxRateView.setText("");
		educessView.setText("");
		incometaxView.setText("");
	}
}
