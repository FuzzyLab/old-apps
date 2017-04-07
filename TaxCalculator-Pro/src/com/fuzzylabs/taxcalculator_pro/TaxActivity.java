package com.fuzzylabs.taxcalculator_pro;

import java.text.DecimalFormat;

import com.fuzzylabs.taxcalculator_pro.R;
import com.fuzzylabs.taxcalculator_pro.AboutActivity;
import com.fuzzylabs.taxcalculator_pro.HraActivity;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class TaxActivity extends Activity {

	Double hra = (double) 0;
	Double investment = (double) 0;
	DecimalFormat df = new DecimalFormat("#.##");
	private static final int REQUEST_HRA = 123;
	private static final int REQUEST_INVESTMENT = 456;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_tax);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.window_title);
		((TextView)findViewById(R.id.headerText)).setText("Tax Calculator");
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
			String url = "https://play.google.com/store/apps/details?id=com.fuzzylabs.taxcalculator_pro";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			return true;
		case R.id.share:
			String thisApp = "https://play.google.com/store/apps/details?id=com.fuzzylabs.taxcalculator_pro";

			String body = "---Tax Calculator Pro---\n --Fuzzy Labs--"
					+ "\n\n Download this app: " + thisApp;

			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, body);
			sendIntent.setType("text/plain");
			startActivity(Intent.createChooser(sendIntent,
					"Fuzzy Labs | Tax Calculator Pro"));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_HRA && resultCode == RESULT_OK) {
			hra = Double.parseDouble(data.getExtras().getString("hra"));
			((TextView) findViewById(R.id.hra)).setText(String.valueOf(df
					.format(hra)));
		}
		if (requestCode == REQUEST_INVESTMENT && resultCode == RESULT_OK) {
			investment = Double.parseDouble(data.getExtras().getString(
					"investment"));
			((TextView) findViewById(R.id.deduction)).setText(String.valueOf(df
					.format(investment)));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void calculateHra(View view) {
		String basicSalaryText = ((TextView) findViewById(R.id.basicIncome))
				.getText().toString();

		Intent hraIntent = new Intent(this, HraActivity.class);
		hraIntent.putExtra("basic", basicSalaryText);
		hraIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(hraIntent, REQUEST_HRA);
	}

	public void calculateInvestment(View view) {
		Intent invetmentIntent = new Intent(this, InvestmentActivity.class);
		invetmentIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(invetmentIntent, REQUEST_INVESTMENT);
	}

	public void calculateTax(View view) {

		String grossSalaryText = ((TextView) findViewById(R.id.grossIncome))
				.getText().toString();

		double grossSalary;
		if (grossSalaryText.equals(""))
			grossSalary = 0;
		else
			grossSalary = Double.parseDouble(grossSalaryText);

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

		((TextView) findViewById(R.id.taxRate)).setText(String.valueOf(rate)+"%");
		((TextView) findViewById(R.id.eduCess)).setText(String.valueOf(df
				.format(eduCess)));
		((TextView) findViewById(R.id.incomeTax)).setText(String.valueOf(df
				.format(incomeTax)));

	}

	public void sendResult(View view) {
		String grossSalaryText = ((EditText) findViewById(R.id.grossIncome))
				.getText().toString();
		if (grossSalaryText.equals(""))
			grossSalaryText = "0";
		String basicSalaryText = ((EditText) findViewById(R.id.basicIncome))
				.getText().toString();
		if (basicSalaryText.equals(""))
			basicSalaryText = "0";
		String deductionText = ((TextView) findViewById(R.id.deduction))
				.getText().toString();
		if (deductionText.equals(""))
			deductionText = "0";
		String hraText = ((TextView) findViewById(R.id.hra)).getText()
				.toString();
		if (hraText.equals(""))
			hraText = "0";
		String taxText = ((TextView) findViewById(R.id.incomeTax)).getText()
				.toString();
		if (taxText.equals(""))
			taxText = "0";
		
		String thisApp = "https://play.google.com/store/apps/details?id=com.fuzzylabs.taxcalculator_pro";

		String body = "---Tax Calculator 2013-14---\n --Fuzzy Labs--\nGross Income:\t"
				+ grossSalaryText
				+ "\nBasic Salary:\t"
				+ basicSalaryText
				+ "\nHRA:\t"
				+ hraText
				+ "\nInvestment:\t"
				+ deductionText
				+ "\n\nIncomeTax:\t"
				+ taxText
				+ "\n\n Download this app: "
				+ thisApp;

		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, body);
		sendIntent.setType("text/plain");
		startActivity(Intent.createChooser(sendIntent, "Tax Calculation"));
	}

}
