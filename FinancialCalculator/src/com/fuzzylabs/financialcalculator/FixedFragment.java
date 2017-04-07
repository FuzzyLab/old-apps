package com.fuzzylabs.financialcalculator;

import java.text.DecimalFormat;

import com.fuzzylabs.financialcalculator.R;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class FixedFragment extends Fragment {

	private EditText fixedView;
	private EditText rateView;
	private EditText yearView;
	private EditText monthView;
	private TextView maturityView;
	private TextView interestView;
	private Button calculate;
	private Button reset;
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static FixedFragment fixedFragment = null;
	
	public static Fragment newInstance(int sectionNumber) {
		if(fixedFragment == null)
			fixedFragment = new FixedFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fixedFragment.setArguments(args);
		return fixedFragment;
	}

	public FixedFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_fixed, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.fixeddepositcalculator);

		fixedView = (EditText) getActivity().findViewById(R.id.fixed_fixed);
		rateView = (EditText) getActivity().findViewById(R.id.rate_fixed);
		yearView = (EditText) getActivity().findViewById(R.id.year_fixed);
		monthView = (EditText) getActivity().findViewById(R.id.month_fixed);
		maturityView = (TextView) getActivity().findViewById(R.id.maturity_fixed);
		interestView = (TextView) getActivity().findViewById(R.id.interest_fixed);

		calculate = (Button) getActivity().findViewById(R.id.calculate_fixed);
		reset = (Button) getActivity().findViewById(R.id.reset_fixed);

		calculate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String fixedText = fixedView.getText().toString();
				String rateText = rateView.getText().toString();
				String yearText = yearView.getText().toString();
				String monthText = monthView.getText().toString();

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

				if (((RadioButton) getActivity().findViewById(R.id.monthly_fixed)).isChecked()) {
					div = 12;
					mul = 1;
				} else if (((RadioButton) getActivity().findViewById(R.id.quarterly_fixed))
						.isChecked()) {
					div = 4;
					mul = 3;
				} else if (((RadioButton) getActivity().findViewById(R.id.halfYearly_fixed))
						.isChecked()) {
					div = 2;
					mul = 6;
				} else if (((RadioButton) getActivity().findViewById(R.id.yearly_fixed)).isChecked()) {
					div = 1;
					mul = 12;
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
				maturityView.setText(df.format(maturity).toString());
				interestView.setText(df.format(interest).toString());				
			}
		});
		
		reset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				fixedView.setText("");
				rateView.setText("");
				yearView.setText("");
				monthView.setText("");
				maturityView.setText("");
				interestView.setText("");				
			}
		});
	}
}
