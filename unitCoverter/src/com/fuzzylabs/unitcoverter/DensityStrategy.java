package com.fuzzylabs.unitcoverter;

import java.util.HashMap;
import java.util.Map;

public class DensityStrategy implements Strategy {

	private Map<String, Double> valuesMap;

	DensityStrategy() {
		valuesMap = new HashMap<String, Double>();
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.densitykgpercubicm)), 1.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.densitygrampercubiccm)),
				1000.0);
		valuesMap
				.put((MainActivity.getInstance().getApplicationContext()
						.getResources().getString(R.string.densitymgpercubicmm)),
						0.001);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.densitykgperl)), 0.001);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.densitygramperl)),
				1.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.densitypoundpercubicft)),
				0.06242793658);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.densitypoundpercubicinch)), 0.000036127292);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.densitypoundpergallon)), 0.008345404452);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.densityouncepercubicft)), 0.9988473692);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.densityouncepercubicinch)),
				0.000578);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.densityouncepergallon)),
				0.1335264712);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.densitytonpercubicyard)), 0.0008427774678);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.densitypsiperthft)),
				0.433527504);
	}

	public double Convert(String from, String to, double input) {
		// TODO Auto-generated method stub

		double deno = valuesMap.get(from);
		double nume = valuesMap.get(to);
		double ret = (nume / deno) * input;
		return ret;
	}

}
