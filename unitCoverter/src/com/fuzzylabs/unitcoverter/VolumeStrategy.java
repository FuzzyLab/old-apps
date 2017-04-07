package com.fuzzylabs.unitcoverter;

import java.util.HashMap;
import java.util.Map;

import com.fuzzylabs.unitcoverter.R;

public class VolumeStrategy implements Strategy {

	private Map<String, Double> valuesMap;

	VolumeStrategy() {
		valuesMap = new HashMap<String, Double>();
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.volumeunitlitres)), 1.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.volumeunitmillilitres)),
				1000.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.volumeunitcubicm)), 0.001);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.volumeunitcubiccm)), 1000.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.volumeunitcubicmm)),
				1000000.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.volumeunitcubicfeet)),
				0.0353147);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.volumeunitcubicyard)),
				0.0013080);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.volumeunitgallon)),
				0.264172);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.volumeunitoilbarrel)),
				0.0062898);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.volumeunittonofwater)),
				0.001);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.volumeunitpintsus)),
				2.11338);
	}

	public double Convert(String from, String to, double input) {
		// TODO Auto-generated method stub

		double deno = valuesMap.get(from);
		double nume = valuesMap.get(to);
		double ret = (nume / deno) * input;
		return ret;
	}
}
