package com.fuzzylabs.financialcalculator;

import java.text.DecimalFormat;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import com.fuzzylabs.financialcalculator.R;

public class TaxFragment extends Fragment {

	private EditText grossIncomeView;
	private EditText hraView;
	private EditText investmentView;
	private TextView taxRateView;
	private TextView educessView;
	private TextView taxView;
	private Button calculate;
	private Button reset;
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static TaxFragment taxFragment = null;
	
	public static Fragment newInstance(int sectionNumber) {
		if(taxFragment == null)
			taxFragment = new TaxFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		taxFragment.setArguments(args);
		return taxFragment;
	}

	public TaxFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_tax, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.taxcalculator);

		grossIncomeView = ((EditText) getActivity().findViewById(R.id.grossSalary_tax));
		hraView = ((EditText) getActivity().findViewById(R.id.hra_tax));
		investmentView = ((EditText) getActivity().findViewById(R.id.investment_tax));
		taxRateView = ((TextView) getActivity().findViewById(R.id.taxRate_tax));
		educessView = ((TextView) getActivity().findViewById(R.id.eduCess_tax));
		taxView = ((TextView) getActivity().findViewById(R.id.incomeTax_tax));
		calculate = (Button) getActivity().findViewById(R.id.calculate_tax);
		reset = (Button) getActivity().findViewById(R.id.reset_tax);
		
		calculate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
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

				if (((RadioButton) getActivity().findViewById(R.id.below60)).isChecked()) {
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

				else if (((RadioButton) getActivity().findViewById(R.id.seniorBelow80)).isChecked()) {
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
				taxView.setText(String.valueOf(df.format(incomeTax)));				
			}
		});
		
		reset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				grossIncomeView.setText("");
				hraView.setText("");
				investmentView.setText("");
				educessView.setText("");
				taxView.setText("");
			}
		});
	}
}
