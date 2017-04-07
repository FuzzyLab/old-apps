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

public class DiscountLostFragment extends Fragment {

	private EditText discountView;
	private EditText lastView;
	private EditText finalView;
	private TextView discountLostView;
	private Button calculate;
	private Button reset;
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static DiscountLostFragment discountLostFragment = null;
	
	public static Fragment newInstance(int sectionNumber) {
		if(discountLostFragment == null)
			discountLostFragment = new DiscountLostFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		discountLostFragment.setArguments(args);
		return discountLostFragment;
	}

	public DiscountLostFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_discount_lost, container, false);
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.discountlostcalculator);
		
		discountView = (EditText) getActivity().findViewById(R.id.discount_discountlost);
		lastView = (EditText) getActivity().findViewById(R.id.last_discountlost);
		finalView = (EditText) getActivity().findViewById(R.id.final_discountlost);
		discountLostView = (TextView) getActivity().findViewById(R.id.discountlost_discountlost);
		calculate = (Button) getActivity().findViewById(R.id.calculate_discountlost);
		reset = (Button) getActivity().findViewById(R.id.reset_discountlost);
		
		calculate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String discountText = discountView.getText().toString();
				String lastText = lastView.getText().toString();
				String finalText = finalView.getText().toString();
				
				double discount;
				if (discountText.equals(""))
					discount = 0;
				else
					discount = Double.parseDouble(discountText);
				
				double lastDay;
				if (lastText.equals(""))
					lastDay = 0;
				else
					lastDay = Double.parseDouble(lastText);
				
				double finalDay;
				if (finalText.equals(""))
					finalDay = 0;
				else
					finalDay = Double.parseDouble(finalText);
				
				double discountLost;
				discountLost = (discount/(100-discount))*(365/(finalDay-lastDay))*100;
				
				DecimalFormat df = new DecimalFormat("#.###");
				
				discountLostView.setText(String.valueOf(df.format(discountLost))+" %");
			}
		});
		
		reset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				discountView.setText("");
				lastView.setText("");
				finalView.setText("");
				discountLostView.setText("");
			}
		});
	}
}
