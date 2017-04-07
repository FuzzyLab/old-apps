package com.fuzzylabs.unitcoverter;

import java.util.HashMap;
import java.util.Map;

import com.fuzzylabs.unitcoverter.R;

public class EnergyStrategy implements Strategy {

	private Map<String, Double> valuesMap;

	EnergyStrategy() {
		valuesMap = new HashMap<String, Double>();
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.energyunitcalories)), 1.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.energyunitjoules)), 4.1868);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.energyunitkilocalories)), 0.001);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.energyuniterg)), 41868000.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.energyunitkwhour)), 0.000001163);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.energyunitwatthour)), 0.001163);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.energyunithphour)), 0.0000015596);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.energyunitbtu)), 0.00396832);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.energyunitkiloton)), 0.000000000001000669216);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.energyunitnewtonm)), 4.1868);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.energyunitgramforcem)), 426.9347841);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.energyunitkgforcem)), 0.4269347841);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.energyunitpoundforceft)), 3.088025207);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.energyunitounceforceinch)), 592.9);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.energyunittherm)), 0.0000000396832);
	}

	public double Convert(String from, String to, double input) {
		// TODO Auto-generated method stub

		double deno = valuesMap.get(from);
		double nume = valuesMap.get(to);
		double ret = (nume / deno) * input;
		return ret;
	}
}
