package com.fuzzylabs.taxcalculator_pro;

import java.text.DecimalFormat;

import com.fuzzylabs.taxcalculator_pro.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class InvestmentActivity extends Activity {
	Double investment = (double) 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_investment);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.window_title);
		((TextView)findViewById(R.id.headerText)).setText("Investment Calculator");
	}

	public void calculateInvestment(View view) {
		String pfText = ((TextView) findViewById(R.id.pf)).getText().toString();
		String fixedDepositeText = ((TextView) findViewById(R.id.fixedDeposite))
				.getText().toString();
		String mutualFundText = ((TextView) findViewById(R.id.mutualFund))
				.getText().toString();
		String homeLoanPremiumText = ((TextView) findViewById(R.id.homeLoanPremium))
				.getText().toString();
		String infraBondText = ((TextView) findViewById(R.id.infraBond))
				.getText().toString();
		String others1Text = ((TextView) findViewById(R.id.others1)).getText()
				.toString();
		String medicalInsuranceText = ((TextView) findViewById(R.id.medicalInsurance))
				.getText().toString();
		String eduLoanInterestText = ((TextView) findViewById(R.id.eduLoanInterest))
				.getText().toString();
		String homeLoanInterestText = ((TextView) findViewById(R.id.homeLoanInterest))
				.getText().toString();
		String others2Text = ((TextView) findViewById(R.id.others2)).getText()
				.toString();

		double pf;
		if (pfText.equals(""))
			pf = 0;
		else
			pf = Double.parseDouble(pfText);

		double fixedDeposite;
		if (fixedDepositeText.equals(""))
			fixedDeposite = 0;
		else
			fixedDeposite = Double.parseDouble(fixedDepositeText);

		double mutualFund;
		if (mutualFundText.equals(""))
			mutualFund = 0;
		else
			mutualFund = Double.parseDouble(mutualFundText);

		double homeLoanPremium;
		if (homeLoanPremiumText.equals(""))
			homeLoanPremium = 0;
		else
			homeLoanPremium = Double.parseDouble(homeLoanPremiumText);

		double others1;
		if (others1Text.equals(""))
			others1 = 0;
		else
			others1 = Double.parseDouble(others1Text);

		double limit1 = pf + fixedDeposite + mutualFund + homeLoanPremium
				+ others1;
		
		if (limit1 > 150000)
			limit1 = 150000;

		double infraBond;
		if (infraBondText.equals(""))
			infraBond = 0;
		else
			infraBond = Double.parseDouble(infraBondText);

		if (infraBond > 20000)
			infraBond = 20000;

		double limit = limit1
				+ infraBond;

		double medicalInsurance;
		if (medicalInsuranceText.equals(""))
			medicalInsurance = 0;
		else
			medicalInsurance = Double.parseDouble(medicalInsuranceText);

		if (medicalInsurance > 40000)
			medicalInsurance = 40000;

		double eduLoanInterest;
		if (eduLoanInterestText.equals(""))
			eduLoanInterest = 0;
		else
			eduLoanInterest = Double.parseDouble(eduLoanInterestText);

		double homeLoanInterest;
		if (homeLoanInterestText.equals(""))
			homeLoanInterest = 0;
		else
			homeLoanInterest = Double.parseDouble(homeLoanInterestText);
		
		if (homeLoanInterest > 150000)
			homeLoanInterest = 150000;

		double others2;
		if (others2Text.equals(""))
			others2 = 0;
		else
			others2 = Double.parseDouble(others2Text);

		investment = limit + medicalInsurance + eduLoanInterest
				+ homeLoanInterest + others2;

		DecimalFormat df = new DecimalFormat("#.##");
		((TextView) findViewById(R.id.totalInvestment)).setText(df.format(
				investment).toString());
	}

	public void copy(View view) {
		Intent i = getIntent();
		i.putExtra("investment", investment.toString());
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		setResult(RESULT_OK, i);
		finish();
	}

}
