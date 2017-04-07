package com.fuzzylabs.unitcoverter;

import java.util.HashMap;
import java.util.Map;

import com.fuzzylabs.unitcoverter.R;

public class AreaStrategy implements Strategy {

	private Map<String, Double> valuesMap;

	AreaStrategy() {
		valuesMap = new HashMap<String, Double>();
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.areaunitsqkm)), 1.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.areaunitsqmiles)), 0.3861);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.areaunitsqm)), 1000000.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.areaunitsqcm)),
				10000000000.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.areaunitsqmm)),
				1000000000000.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.areaunitsqyard)),
				1195990.0499);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.areaunithectare)), 100.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.areaunitacre)), 247.1053815);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.areaunitrood)), 988.4215259);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.areaunitsqporch)), 39536.86103);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.areaunitsqpole)), 39536.86103);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.areaunitplaza)), 156.25);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.areaunitsection)), 0.3861021585);
	}

	public double Convert(String from, String to, double input) {
		// TODO Auto-generated method stub

		double deno = valuesMap.get(from);
		double nume = valuesMap.get(to);
		double ret = (nume / deno) * input;
		return ret;
	}
}
