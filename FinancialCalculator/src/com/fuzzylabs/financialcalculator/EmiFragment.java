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
import android.widget.TextView;
import com.fuzzylabs.financialcalculator.R;

public class EmiFragment extends Fragment {

	private EditText principalView;
	private EditText rateView;
	private EditText yearView;
	private EditText month_view;
	private TextView emiView;
	private TextView interestView;
	private TextView totalPaymentView;
	private Button calculate;
	private Button reset;
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static EmiFragment emiFragment = null;
	
	public static Fragment newInstance(int sectionNumber) {
		if(emiFragment == null)
			emiFragment = new EmiFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		emiFragment.setArguments(args);
		return emiFragment;
	}

	public EmiFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_emi, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.emicalculator);

		principalView = ((EditText) getActivity().findViewById(R.id.principal_emi));
		rateView = ((EditText) getActivity().findViewById(R.id.rate_emi));
		yearView = ((EditText) getActivity().findViewById(R.id.year_emi));
		month_view = ((EditText) getActivity().findViewById(R.id.month_emi));
		emiView = ((TextView) getActivity().findViewById(R.id.emi_emi));
		interestView = ((TextView) getActivity().findViewById(R.id.interest_emi));
		totalPaymentView = ((TextView) getActivity().findViewById(R.id.totalPayment_emi));
		
		calculate = (Button) getActivity().findViewById(R.id.calculate_emi);
		reset = (Button) getActivity().findViewById(R.id.reset_emi);
		
		calculate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String principalText = principalView.getText().toString();
				String rateText = rateView.getText().toString();
				String yearText = yearView.getText().toString();
				String monthText = month_view.getText().toString();

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
				emiView.setText(df.format(emi).toString());
				interestView.setText(df.format(interest).toString());
				totalPaymentView.setText(df.format(totalPay).toString());				
			}
		});
		
		reset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				principalView.setText("");
				rateView.setText("");
				yearView.setText("");
				month_view.setText("");
				emiView.setText("");
				totalPaymentView.setText("");
				interestView.setText("");				
			}
		});
	}
}
