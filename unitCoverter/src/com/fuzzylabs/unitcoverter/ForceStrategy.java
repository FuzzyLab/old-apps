package com.fuzzylabs.unitcoverter;

import java.util.HashMap;
import java.util.Map;

public class ForceStrategy implements Strategy {
	private Map<String, Double> valuesMap;

	ForceStrategy() {
		valuesMap = new HashMap<String, Double>();
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.forcenewton)), 1.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.forcekilonewton)), 0.001);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.forcedyne)), 100000.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.forcejouleperm)), 1.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.forcekgforce)), 0.1019716213);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.forcegramforce)), 101.9716213);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.forcepoundforce)), 0.2248089431);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.forceounceforce)), 3.59694309);
	}

	public double Convert(String from, String to, double input) {
		// TODO Auto-generated method stub

		double deno = valuesMap.get(from);
		double nume = valuesMap.get(to);
		double ret = (nume / deno) * input;
		return ret;
	}
}
