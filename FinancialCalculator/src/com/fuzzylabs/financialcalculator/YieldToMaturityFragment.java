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
import android.widget.*;

public class YieldToMaturityFragment extends Fragment {

	private EditText annualInterestView;
	private EditText faceValueView;
	private EditText marketPriceView;
	private EditText yearsView;
	private TextView ytmView;
	private Button calculate;
	private Button reset;
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static YieldToMaturityFragment yieldToMaturityFragment = null;
	
	public static Fragment newInstance(int sectionNumber) {
		if(yieldToMaturityFragment == null)
			yieldToMaturityFragment = new YieldToMaturityFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		yieldToMaturityFragment.setArguments(args);
		return yieldToMaturityFragment;
	}

	public YieldToMaturityFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_yield_to_maturity, container, false);
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.yieldtomaturitycalculator);
		
		annualInterestView = (EditText) getActivity().findViewById(R.id.annualInterest_ytm);
		faceValueView = (EditText) getActivity().findViewById(R.id.faceValue_ytm);
		marketPriceView = (EditText) getActivity().findViewById(R.id.marketPrice_ytm);
		yearsView = (EditText) getActivity().findViewById(R.id.year_ytm);
		ytmView = (TextView) getActivity().findViewById(R.id.ytm_ytm);
		calculate = (Button) getActivity().findViewById(R.id.calculate_ytm);
		reset = (Button) getActivity().findViewById(R.id.reset_ytm);
		
		calculate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String annualInterestText = annualInterestView.getText().toString();
				String faceValueText = faceValueView.getText().toString();
				String marketPriceText = marketPriceView.getText().toString();
				String yearsText = yearsView.getText().toString();
				
				double annualInterest;
				if (annualInterestText.equals(""))
					annualInterest = 0;
				else
					annualInterest = Double.parseDouble(annualInterestText);
				
				double faceValue;
				if (faceValueText.equals(""))
					faceValue = 0;
				else
					faceValue = Double.parseDouble(faceValueText);
				
				double marketPrice;
				if (marketPriceText.equals(""))
					marketPrice = 0;
				else
					marketPrice = Double.parseDouble(marketPriceText);
				
				double years;
				if (yearsText.equals(""))
					years = 0;
				else
					years = Double.parseDouble(yearsText);
				
				double ytm;
				ytm = ((annualInterest+(faceValue-marketPrice)/years)/((faceValue+marketPrice)/2))*100;
				
				DecimalFormat df = new DecimalFormat("#.###");
				ytmView.setText(String.valueOf(df.format(ytm))+" %");
			}
		});
		
		reset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				annualInterestView.setText("");
				faceValueView.setText("");
				marketPriceView.setText("");
				yearsView.setText("");
				ytmView.setText("");				
			}
		});
	}
}
