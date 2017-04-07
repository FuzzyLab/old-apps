
package com.fuzzylabs.unitcoverter;

import java.util.HashMap;
import java.util.Map;

import com.fuzzylabs.unitcoverter.R;

public class WeightStrategy implements Strategy {
	
	private Map<String, Double> valuesMap;

	WeightStrategy() {
		valuesMap = new HashMap<String, Double>();
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.weightunitkg)), 1.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.weightunitgm)), 1000.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.weightunitlb)), 2.2046);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.weightunitounce)), 35.27396);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.weightunitmg)), 1000000.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.weightunitstone)), 0.157473);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.weightunitmetricton)), 0.001);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.weightunitquintal)), 0.01);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.weightunitdyne)), 980665.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.weightunitquarter)), 0.088185);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.weightunitcarrat)), 5000.0);
	}
	
	public double Convert(String from, String to, double input) {
		// TODO Auto-generated method stub

		double deno = valuesMap.get(from);
		double nume = valuesMap.get(to);
		double ret = (nume/deno)*input;
		return ret;
	}
}
