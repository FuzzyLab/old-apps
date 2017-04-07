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

public class CcipFragment extends Fragment {
	private EditText maturityView;
	private EditText rateView;
	private EditText yearView;
	private EditText monthView;
	private TextView issueView;
	private TextView interestView;
	private TextView caution;
	private Button calculate;
	private Button reset;
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static CcipFragment ccipFragment = null;
	
	public static Fragment newInstance(int sectionNumber) {
		if(ccipFragment == null)
			ccipFragment = new CcipFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		ccipFragment.setArguments(args);
		return ccipFragment;
	}

	public CcipFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_ccip, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.ccipcalculator);

		caution = (TextView) getActivity().findViewById(R.id.caution_ccip);
		maturityView = (EditText) getActivity().findViewById(R.id.maturity_ccip);
		rateView = (EditText) getActivity().findViewById(R.id.rate_ccip);
		yearView = (EditText) getActivity().findViewById(R.id.year_ccip);
		monthView = (EditText) getActivity().findViewById(R.id.month_ccip);
		issueView = (TextView) getActivity().findViewById(R.id.issue_ccip);
		interestView = (TextView) getActivity().findViewById(R.id.interest_ccip);
		calculate = (Button) getActivity().findViewById(R.id.calculate_ccip);
		reset = (Button) getActivity().findViewById(R.id.reset_ccip);
		
		calculate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				caution.setText("");
				String maturityText = maturityView.getText().toString();
				String rateText = rateView.getText().toString();
				String yearText = yearView.getText().toString();
				String monthText = monthView.getText().toString();

				double maturity;
				if (maturityText.equals(""))
					maturity = 0;
				else
					maturity = Double.parseDouble(maturityText);

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
				double issue = 0.0;
				double interest = 0.0;

				if (month % 3 != 0) {
					caution.setText("!! Month must be a multiple of 3 !!");
					issueView.setText("");
					interestView.setText("");
				} else {
					if (maturity == 0) {
						r = 0;
						n = 0;
						issue = 0;
					} else if (n == 0) {
						maturity = 0;
						r = 0;
						issue = 0;
					} else if (r == 0) {
						issue = maturity;
					} else {
						double x = 1.00;

						for (int i = 0; i < n; i++) {
							x = x * (1 + r);
						}
						issue = maturity / x;
						interest = maturity - issue;
					}

					DecimalFormat df = new DecimalFormat("#.##");
					issueView.setText(df.format(issue).toString());
					interestView.setText(df.format(interest).toString());
				}
			}
		});
		
		reset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				maturityView.setText("");
				rateView.setText("");
				yearView.setText("");
				monthView.setText("");
				issueView.setText("");
				interestView.setText("");
			}
		});
	}
}
