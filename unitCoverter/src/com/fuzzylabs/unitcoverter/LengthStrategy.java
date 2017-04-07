package com.fuzzylabs.unitcoverter;

import java.util.HashMap;
import java.util.Map;

import com.fuzzylabs.unitcoverter.R;

public class LengthStrategy implements Strategy {

	private Map<String, Double> valuesMap;

	LengthStrategy() {
		valuesMap = new HashMap<String, Double>();
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.lengthunitmile)), 1.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.lengthunitkm)), 1.60934);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.lengthunitm)), 1609.34);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.lengthunitcm)), 160934.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.lengthunitmm)), 1609340.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.lengthunitinch)), 63360.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.lengthunitfeet)), 5280.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.lengthunityard)), 1760.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.lengthunitfurlong)), 8.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.lengthunitchain)), 80.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.lengthunitrod)), 320.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.lengthunitperch)), 320.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.lengthunitpole)), 320.0);
	}

	public double Convert(String from, String to, double input) {
		// TODO Auto-generated method stub

		double deno = valuesMap.get(from);
		double nume = valuesMap.get(to);
		double ret = (nume / deno) * input;
		return ret;
	}
}
