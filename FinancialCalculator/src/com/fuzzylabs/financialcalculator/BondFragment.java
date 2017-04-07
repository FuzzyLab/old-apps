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

public class BondFragment extends Fragment {

	private EditText faceView;
	private EditText couponView;
	private EditText interestView;
	private EditText yearView;
	private TextView bondView;
	private Button calculate;
	private Button reset;
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static BondFragment bondFragment = null;
	
	public static Fragment newInstance(int sectionNumber) {
		if(bondFragment == null)
			bondFragment = new BondFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		bondFragment.setArguments(args);
		return bondFragment;
	}

	public BondFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_bond, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.bondpricecalculator);

		faceView = (EditText) getActivity().findViewById(R.id.faceValue_bond);
		couponView = (EditText) getActivity().findViewById(R.id.coupon_bond);
		interestView = (EditText) getActivity().findViewById(R.id.interest_bond);
		yearView = (EditText) getActivity().findViewById(R.id.year_bond);
		bondView = (TextView) getActivity().findViewById(R.id.bond_bond);
		calculate = (Button) getActivity().findViewById(R.id.calculate_bond);
		reset = (Button) getActivity().findViewById(R.id.reset_bond);
		
		calculate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String faceText = faceView.getText().toString();
				String couponText = couponView.getText().toString();
				String interestText = interestView.getText().toString();
				String yearText = yearView.getText().toString();

				double face;
				if (faceText.equals(""))
					face = 0;
				else
					face = Double.parseDouble(faceText);

				double coupon;
				if (couponText.equals(""))
					coupon = 0;
				else
					coupon = Double.parseDouble(couponText);

				double interest;
				if (interestText.equals(""))
					interest = 0;
				else
					interest = Double.parseDouble(interestText);

				double year;
				if (yearText.equals(""))
					year = 0;
				else
					year = Double.parseDouble(yearText);

				double bond = 0;
				double n = 0;
				double r = 0;
				double c = 0;

				if (((RadioButton) getActivity().findViewById(R.id.yearly_bond)).isChecked()) {
					n = year;
					r = interest / 100;
					c = coupon;
				} else if (((RadioButton) getActivity().findViewById(R.id.halfyearly_bond))
						.isChecked()) {
					n = 2 * year;
					r = interest / 200;
					c = coupon / 2;
				}

				double pow = Math.pow((1 + r), (-1) * n);
				bond = c*((1 - pow) / r) + (face / pow);

				DecimalFormat df = new DecimalFormat("#.###");
				bondView.setText(String.valueOf(df.format(bond)));				
			}
		});
		
		reset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				faceView.setText("");
				couponView.setText("");
				interestView.setText("");
				yearView.setText("");
				bondView.setText("");
			}
		});
	}
}
