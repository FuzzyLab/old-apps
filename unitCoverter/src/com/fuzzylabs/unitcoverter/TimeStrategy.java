package com.fuzzylabs.unitcoverter;

import java.util.HashMap;
import java.util.Map;

import com.fuzzylabs.unitcoverter.R;

public class TimeStrategy implements Strategy {
	private Map<String, Double> valuesMap;

	TimeStrategy() {
		valuesMap = new HashMap<String, Double>();
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.timeyear)), 1.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.timemonth)), 12.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.timeweek)), 52.1775);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.timeday)), 365.242);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.timehour)), 8765.81);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.timeminute)), 525949.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
						.getResources().getString(R.string.timesec)), 31556940.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.timemillisec)), 31556940000.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.timemicrosec)), 31556940000000.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.timenanosec)), 31556940000000000.0);
	}

	public double Convert(String from, String to, double input) {
		// TODO Auto-generated method stub

		double deno = valuesMap.get(from);
		double nume = valuesMap.get(to);
		double ret = (nume / deno) * input;
		return ret;
	}
}
