package com.fuzzylabs.taxcalculator_pro;

import java.text.DecimalFormat;

import com.fuzzylabs.taxcalculator_pro.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.TextView;

public class HraActivity extends Activity {
	Double basicSalary;
	Double hra = (double) 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_hra);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);
		((TextView)findViewById(R.id.headerText)).setText("HRA Calculator");
		
		Intent i = getIntent();
		String basic = i.getStringExtra("basic");
		
		if (basic.equals(""))
			basicSalary = (double) 0;
		else
			basicSalary = Double.parseDouble(basic);
		
		((TextView)findViewById(R.id.basic)).setText(basicSalary.toString());
	}
	
	public void calculateHra(View view) {
		String houseRentPaidText = ((TextView) findViewById(R.id.houseRentPaid))
				.getText().toString();
		String hraReceivedText = ((TextView) findViewById(R.id.hraReceived))
				.getText().toString();
		
		float factor = 0.4f;
		if(((RadioButton)(findViewById(R.id.metroRadio))).isChecked()) {
			System.out.println("True");
			factor = (float) 0.5;
		}
		else
			System.out.println("False");
			
		
		double houseRentPaid;
		if (houseRentPaidText.equals(""))
			houseRentPaid = 0;
		else
			houseRentPaid = Double.parseDouble(houseRentPaidText);

		double hraReceived;
		if (hraReceivedText.equals(""))
			hraReceived = 0;
		else
			hraReceived = Double.parseDouble(hraReceivedText);
		
		double hra1 = factor * basicSalary;
		double hra2 = houseRentPaid - (0.1 * basicSalary);
		double hra3 = hraReceived;
		
		if (hra1 < hra2) {
			if (hra1 < hra3)
				hra = hra1;
			else
				hra = hra3;
		} else {
			if (hra2 < hra3)
				hra = hra2;
			else
				hra = hra3;
		}
		
		if(hra < 0)
			hra = (double) 0;
		
		DecimalFormat df = new DecimalFormat("#.##");
		((TextView)findViewById(R.id.hra2)).setText(df.format(hra).toString());
	}
	
	public void copy(View view) {
		Intent i = getIntent();
		i.putExtra("hra", hra.toString());
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		setResult(RESULT_OK, i);
		finish();
	}

}
