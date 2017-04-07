package com.fuzzylabs.unitcoverter;

import java.util.HashMap;
import java.util.Map;

public class PressureStrategy implements Strategy {
	private Map<String, Double> valuesMap;

	PressureStrategy() {
		valuesMap = new HashMap<String, Double>();
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.pressurepascal)), 1.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.pressureatmosphere)), 0.00000987);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.pressurebar)), 0.00001);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.pressuremercurycm)), 0.0007501);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.pressurebarad)), 10.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.pressurebarye)), 10.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.pressuredynepersquarecm)), 10.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.pressurekgpersquarem)), 0.101972);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.pressuremlofwater)), 0.10197);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.pressureouncepersquareinch)), 32.1507);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.pressurepoundpersquareinch)), 0.000145);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.pressuretorr)), 0.0075);
	}

	public double Convert(String from, String to, double input) {
		// TODO Auto-generated method stub

		double deno = valuesMap.get(from);
		double nume = valuesMap.get(to);
		double ret = (nume / deno) * input;
		return ret;
	}
}
