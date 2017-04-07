package com.fuzzylabs.unitcoverter;

import java.util.HashMap;
import java.util.Map;

import com.fuzzylabs.unitcoverter.R;

public class VelocityStrategy implements Strategy {

	private Map<String, Double> valuesMap;

	VelocityStrategy() {
		valuesMap = new HashMap<String, Double>();
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.velocityunitkmph)), 1.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.velocityunitmilesperh)),
				0.6214);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.velocityunitmeterpers)),
				0.2778);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.velocityunitfeetpers)),
				0.9113);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.velocityunityardsperh)),
				1093.61);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.velocityunitinchpers)),
				10.93614);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.velocityunitknots)),
				0.539957);
	}

	public double Convert(String from, String to, double input) {
		// TODO Auto-generated method stub

		double deno = valuesMap.get(from);
		double nume = valuesMap.get(to);
		double ret = (nume / deno) * input;
		return ret;
	}
}
