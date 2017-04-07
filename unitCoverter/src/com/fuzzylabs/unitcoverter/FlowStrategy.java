package com.fuzzylabs.unitcoverter;

import java.util.HashMap;
import java.util.Map;

import com.fuzzylabs.unitcoverter.R;

public class FlowStrategy implements Strategy {

	private Map<String, Double> valuesMap;

	FlowStrategy() {
		valuesMap = new HashMap<String, Double>();
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.flowmetercubepersec)), 1.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.flowmetercubepermin)), 60.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.flowmetercubeperhour)),
				3600.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.flowliterpersec)), 1000.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.flowliterpermin)), 60000.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.flowliterperhour)),
				3600000.0);

		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.flowcubicfeetpersec)), 35.3);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.flowcubicfeetpermin)),
				2119.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.flowcubicfeetperhour)),
				127133.0);

		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.flowcubicyardpersec)), 1.31);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.flowcubicyardpermin)), 78.5);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.flowcubicyardperhour)),
				4709.0);

		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.flowbarrelpersec)), 6.29);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.flowbarrelpermin)), 377.4);
		valuesMap
				.put((MainActivity.getInstance().getApplicationContext()
						.getResources().getString(R.string.flowbarrelperhour)),
						22643.0);

		valuesMap
				.put((MainActivity.getInstance().getApplicationContext()
						.getResources().getString(R.string.flowgallonukpersec)),
						219.97);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.flowgallonukpermin)),
				13198.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.flowgallonukperhour)),
				791889.0);

		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.flowgallonuspersec)), 264.2);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.flowgallonuspermin)),
				15850.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.flowgallonusperhour)),
				951019.0);

		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.flowtonofwaterpersec)), 1.0);
		valuesMap
				.put((MainActivity.getInstance().getApplicationContext()
						.getResources()
						.getString(R.string.flowtonofwaterpermin)), 60.0);
		valuesMap.put((MainActivity.getInstance().getApplicationContext()
				.getResources().getString(R.string.flowtonofwaterperhour)),
				3600.0);
	}

	public double Convert(String from, String to, double input) {
		// TODO Auto-generated method stub

		double deno = valuesMap.get(from);
		double nume = valuesMap.get(to);
		double ret = (nume / deno) * input;
		return ret;
	}
}
