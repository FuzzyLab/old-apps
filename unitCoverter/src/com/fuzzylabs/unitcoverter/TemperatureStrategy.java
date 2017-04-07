
package com.fuzzylabs.unitcoverter;

import com.fuzzylabs.unitcoverter.R;


public class TemperatureStrategy implements Strategy {

	public double Convert(String from, String to, double input) {
		
		if((from.equals(MainActivity.getInstance().getApplicationContext().getResources().getString(R.string.temperatureunitc)) && to.equals((MainActivity.getInstance().getApplicationContext().getResources().getString(R.string.temperatureunitf))))){
			double ret = (double)((input*9/5)+32);
			return ret;
		}
	
		if((from.equals(MainActivity.getInstance().getApplicationContext().getResources().getString(R.string.temperatureunitf)) && to.equals((MainActivity.getInstance().getApplicationContext().getResources().getString(R.string.temperatureunitc))))){
			double ret = (double)((input-32)*5/9);
			return ret;
		}
		if((from.equals(MainActivity.getInstance().getApplicationContext().getResources().getString(R.string.temperatureunitk)) && to.equals((MainActivity.getInstance().getApplicationContext().getResources().getString(R.string.temperatureunitc))))){
			double ret = (double)(input - 273.15);
			return ret;
		}
		if((from.equals(MainActivity.getInstance().getApplicationContext().getResources().getString(R.string.temperatureunitc)) && to.equals((MainActivity.getInstance().getApplicationContext().getResources().getString(R.string.temperatureunitk))))){
			double ret = (double)(input + 273.15);
			return ret;
		}
		if((from.equals(MainActivity.getInstance().getApplicationContext().getResources().getString(R.string.temperatureunitk)) && to.equals((MainActivity.getInstance().getApplicationContext().getResources().getString(R.string.temperatureunitf))))){
			double ret = (double)(((input-273.15)*9/5)+32);
			return ret;
		}
		if((from.equals(MainActivity.getInstance().getApplicationContext().getResources().getString(R.string.temperatureunitf)) && to.equals((MainActivity.getInstance().getApplicationContext().getResources().getString(R.string.temperatureunitk))))){
			double ret = (double)(((input-32)*5/9)+273.15);
			return ret;
		}

	if(from.equals(to)){
		return input;	
	}
	return 0.0;
	}

}
