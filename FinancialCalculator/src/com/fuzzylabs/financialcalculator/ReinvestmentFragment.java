package com.fuzzylabs.financialcalculator;

import java.text.DecimalFormat;

import com.fuzzylabs.financialcalculator.R;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ReinvestmentFragment extends Fragment {

	private EditText principalView;
	private EditText rateView;
	private EditText yearView;
	private EditText monthView;
	private TextView maturityView;
	private TextView interestView;
	private TextView caution;
	private Button calculate;
	private Button reset;
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static ReinvestmentFragment reinvestmentFragment = null;
	
	public static Fragment newInstance(int sectionNumber) {
		if(reinvestmentFragment == null)
			reinvestmentFragment = new ReinvestmentFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		reinvestmentFragment.setArguments(args);
		return reinvestmentFragment;
	}

	public ReinvestmentFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_reinvestment, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.reinvestmentdepositcalculator);

		caution = (TextView) getActivity().findViewById(R.id.caution_reinvestment);
		principalView = (EditText) getActivity().findViewById(R.id.principal_reinvestment);
		rateView = (EditText) getActivity().findViewById(R.id.rate_reinvestment);
		yearView = (EditText) getActivity().findViewById(R.id.year_reinvestment);
		monthView = (EditText) getActivity().findViewById(R.id.month_reinvestment);
		maturityView = (TextView) getActivity().findViewById(R.id.maturity_reinvestment);
		interestView = (TextView) getActivity().findViewById(R.id.interest_reinvestment);
		
		calculate = (Button) getActivity().findViewById(R.id.calculate_reinv);
		reset = (Button) getActivity().findViewById(R.id.reset_reinv);
		
		calculate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				caution.setText("");
				String principalText = principalView.getText().toString();
				String rateText = rateView.getText().toString();
				String yearText = yearView.getText().toString();
				String monthText = monthView.getText().toString();

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

				double r = rate / 400;
				int n = ((year * 12) + month) / 3;
				double maturity = 0.0;
				double interest = 0.0;

				if (month % 3 != 0) {
					caution.setText("!! Month must be a multiple of 3 !!");
					maturityView.setText("");
					interestView.setText("");
				} else {
					if (principal == 0) {
						r = 0;
						n = 0;
						maturity = 0;
					} else if (n == 0) {
						principal = 0;
						r = 0;
						maturity = 0;
					} else if (r == 0) {
						maturity = principal;
					} else {
						double x = 1.00;

						for (int i = 0; i < n; i++) {
							x = x * (1 + r);
						}
						maturity = principal * x;
						interest = maturity - principal;
					}

					DecimalFormat df = new DecimalFormat("#.##");
					maturityView.setText(df.format(maturity).toString());
					interestView.setText(df.format(interest).toString());
				}
			}
		});
		
		reset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				principalView.setText("");
				rateView.setText("");
				yearView.setText("");
				monthView.setText("");
				maturityView.setText("");
				interestView.setText("");
			}
		});
	}
}
