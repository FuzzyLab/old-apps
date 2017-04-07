package com.fuzzylabs.unitcoverter;

import java.util.HashMap;
import java.util.Map;

import com.fuzzylabs.unitcoverter.R;

public class PowerStrategy implements Strategy {

	private Map<String, Double> valuesMap;

	PowerStrategy() {
		valuesMap = new HashMap<String, Double>();
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.powerunitwatts)), 1.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.powerunithorseposer)), 0.00134);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.powerunitkilowatts)), 0.001);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.powerunitmegawatt)), 0.000001);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.powerunitbtuperhour)), 3.412141633);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.powerunitbtupermin)), 0.05686902722);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.powerunitbtupersec)), 0.0009478);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.powerunitton)), 0.001);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.powerunitkilocalorieperhour)), 0.859845);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.powerunitkilocaloriepermin)), 0.01433);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.powerunitkilocaloriepersec)), 0.000238846);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.powerunitcaloriepermin)), 14.34);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.powerunitcaloriepersec)), 0.239);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.powerunitergpersec)), 10000000.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.powerunitkilojoulespersec)), 0.001);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.powerunitjoulespersec)), 1.0);
	}
	
	public double Convert(String from, String to, double input) {
		// TODO Auto-generated method stub

		double deno = valuesMap.get(from);
		double nume = valuesMap.get(to);
		double ret = (nume/deno)*input;
		return ret;
	}
}
