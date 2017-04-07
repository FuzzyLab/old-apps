package com.fuzzylabs.financialcalculator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import com.fuzzylabs.financialcalculator.R;

public class CurrencyFragment extends Fragment {
	private TextView valueView;
	private TextView loading;
	private EditText factorText;
	private double factor;
	private double value;
	private DecimalFormat df = new DecimalFormat("#.#####");
	private Spinner fromList;
	private Spinner toList;
	private Button toggle;
	private Button refresh;
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static CurrencyFragment currencyFragment = null;
	
	public static Fragment newInstance(int sectionNumber) {
		if(currencyFragment == null)
			currencyFragment = new CurrencyFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		currencyFragment.setArguments(args);
		return currencyFragment;
	}

	public CurrencyFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_currency, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.currencyconverter);

		valueView = (TextView) getActivity().findViewById(R.id.valueText_currency);
		loading = (TextView) getActivity().findViewById(R.id.loadingView_currency);
		factorText = (EditText) getActivity().findViewById(R.id.inputText_currency);
		fromList = (Spinner) getActivity().findViewById(R.id.spinnerFrom_currency);
		toList = (Spinner) getActivity().findViewById(R.id.spinnerTo_currency);
		toggle = (Button)getActivity().findViewById(R.id.toggle_currency);
		refresh = (Button)getActivity().findViewById(R.id.refresh_currency);
		factor = 0;
		value = 0;
		fromList.setSelection(149);
		toList.setSelection(65);
		
		toggle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int from = fromList.getSelectedItemPosition();
				int to = toList.getSelectedItemPosition();
				fromList.setSelection(to);
				toList.setSelection(from);
				if (!isOnline()) {
					loading.setText("No Network Connection");
				} else {
					loading.setText("Loading..");
					fetch();
				}
			}
		});
		
		refresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!isOnline()) {
					loading.setText("No Network Connection");
				} else {
					loading.setText("Loading..");
					fetch();
				}
			}
		});
		
		if (isOnline()) {
			factorText.setText("1");
			loading.setText("Loading..");
			Thread th = new Thread(new Runnable() {
				@Override
				public void run() {
					fetch();
				}
			});
			th.start();
		}

		fromList.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (!isOnline()) {
					loading.setText("No Network Connection");
				} else {
					loading.setText("Loading..");
					fetch();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		toList.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (!isOnline()) {
					loading.setText("No Network Connection");
				} else {
					loading.setText("Loading..");
					fetch();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		factorText.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable statusText) {
				String changedText = factorText.getText().toString();
				double changed;
				if (changedText.toString().equals(""))
					changed = 0;
				else
					changed = Double.parseDouble(changedText);

				Double d = value * changed;

				valueView.setText(df.format(d).toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int count,
					int after) {
			}
		});

	}

	public boolean isOnline() {

		ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public void fetch() {
		String currencyFrom = String.valueOf((fromList).getSelectedItem());
		String currencyTo = String.valueOf((toList).getSelectedItem());
		valueView.setText("");

		String fact = (factorText).getText().toString();
		if (fact.equals(""))
			factor = 0;
		else
			factor = Double.parseDouble(fact);

		String[] x = currencyFrom.split(" - ");
		String from = x[1];
		x = null;
		x = currencyTo.split(" - ");
		String to = x[1];

		String urlStr = new String("http://finance.yahoo.com/d/quotes.csv?s="
				+ from + to + "=X&f=l1&e=.csv");
		GetCurrencyQuoteTask task = new GetCurrencyQuoteTask();
		task.execute(urlStr);

	}

	private class GetCurrencyQuoteTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... url) {
			String response = "";
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url[0]);
			try {
				HttpResponse execute = client.execute(httpGet);
				InputStream content = execute.getEntity().getContent();

				BufferedReader buffer = new BufferedReader(
						new InputStreamReader(content));
				String s = buffer.readLine();
				String[] sx = s.split(",");
				response = sx[0];

			} catch (Exception e) {
				e.printStackTrace();
			}
			value = Double.parseDouble(response);
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			Double val = Double.parseDouble(result);
			val = val * factor;
			valueView.setText(df.format(val).toString());
			loading.setText("");
		}
	}
}
