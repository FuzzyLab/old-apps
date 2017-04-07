package com.fuzzylabs.currencyconverter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.DecimalFormat;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

public class CurrencyConverterActivity extends Activity {

	TextView valueView;
	ImageView imgView;
	TextView loading;
	EditText factorText;
	double factor;
	double value;
	DecimalFormat df = new DecimalFormat("#.#####");
	Spinner fromList;
	Spinner toList;
	Dialog dialogbox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_currency_converter);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.window_title);

		valueView = (TextView) findViewById(R.id.valueText);
		loading = (TextView) findViewById(R.id.loadingView);
		imgView = (ImageView) findViewById(R.id.graphImage);
		factorText = (EditText) findViewById(R.id.inputText);
		fromList = (Spinner) findViewById(R.id.spinnerFrom);
		toList = (Spinner) findViewById(R.id.spinnerTo);
		factor = 0;
		value = 0;
		fromList.setSelection(149);
		toList.setSelection(65);

		if (!isOnline()) {
			dialogbox = new Dialog(CurrencyConverterActivity.this);
			dialogbox.setContentView(R.layout.no_connection_dialog);
			dialogbox.setTitle("No Network Connection");
			Button button = (Button) dialogbox.findViewById(R.id.closingButton);
			button.setText("OK");
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialogbox.dismiss();
					CurrencyConverterActivity.this.finish();
				}
			});
			button.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN: {
						v.setBackgroundColor(0xFF000000);
						v.invalidate();
						break;
					}
					case MotionEvent.ACTION_UP: {
						v.setBackgroundColor(0x00000000);
						v.invalidate();
						break;
					}
					}
					return false;
				}
			});
			dialogbox.show();
		} else {

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
				// TODO Auto-generated method stub

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
				// TODO Auto-generated method stub

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

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;
		case R.id.rate:
			String url = "https://play.google.com/store/apps/details?id=com.fuzzylabs.currencyconverter";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			return true;
		case R.id.share:
			String thisApp = "https://play.google.com/store/apps/details?id=com.fuzzylabs.currencyconverter";

			String body = "---Currency Converter---\n --Fuzzy Labs--"
					+ "\n\n Download this app: " + thisApp;

			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, body);
			sendIntent.setType("text/plain");
			startActivity(Intent.createChooser(sendIntent,
					"Fuzzy Labs | Emi Calculator"));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.currency_converter, menu);
		return true;
	}

	public void fetch() {
		valueView.setText("");
		String currencyFrom = String.valueOf((fromList).getSelectedItem());
		String currencyTo = String.valueOf((toList).getSelectedItem());

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

		String imgUrl = "http://chart.finance.yahoo.com/z?s=" + from + to
				+ "=X&t=6m&q=l&l=on&z=s&p=m50,m200";
		GetCurrencyImageTask imgTask = new GetCurrencyImageTask();
		imgTask.execute(imgUrl);

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
		}
	}

	private class GetCurrencyImageTask extends AsyncTask<String, Void, Bitmap> {
		@Override
		protected Bitmap doInBackground(String... url) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url[0]);
			Bitmap myBitmap = null;
			try {
				HttpResponse execute = client.execute(httpGet);
				InputStream content = execute.getEntity().getContent();
				myBitmap = BitmapFactory.decodeStream(content);

			} catch (Exception e) {
				System.out.println(e);
			}

			return myBitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			imgView.setImageBitmap(result);
			loading.setText("");
		}
	}

	public void add(View view) {
		try {
			String currencyFrom = String.valueOf((fromList).getSelectedItem());
			String currencyTo = String.valueOf((toList).getSelectedItem());

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

			Currencies entry = new Currencies(CurrencyConverterActivity.this);
			entry.open();
			int countPre = entry.getCount();
			entry.createEntry(from, to, df.format(value).toString());
			int countPost = entry.getCount();

			if (countPre == countPost) {
				Toast.makeText(CurrencyConverterActivity.this, "Dublicate",
						Toast.LENGTH_SHORT).show();
			} else
				Toast.makeText(CurrencyConverterActivity.this, "Added",
						Toast.LENGTH_SHORT).show();
			entry.close();
		} catch (Exception e) {
			Toast.makeText(CurrencyConverterActivity.this, "Error",
					Toast.LENGTH_SHORT).show();
		} finally {

		}

	}

	public void viewAll(View view) {
		Intent intent = new Intent(this, ViewAllActivity.class);
		startActivity(intent);
	}

	public void changeRange(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				CurrencyConverterActivity.this);
		builder.setTitle("Select Range");
		String[] options = { "1 Day", "1 month", "6 Months", "1 Year",
				"5 Years" };
		builder.setItems(options, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				loading.setText("Loading..");
				String currencyFrom = String.valueOf((fromList)
						.getSelectedItem());
				String currencyTo = String.valueOf((toList).getSelectedItem());

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
				if (which == 0) {
					String imgUrl = "http://chart.finance.yahoo.com/z?s="
							+ from + to + "=X&t=1d&q=l&l=on&z=s&p=m50,m200";
					GetCurrencyImageTask imgTask = new GetCurrencyImageTask();
					imgTask.execute(imgUrl);
				}
				if (which == 1) {
					String imgUrl = "http://chart.finance.yahoo.com/z?s="
							+ from + to + "=X&t=1m&q=l&l=on&z=s&p=m50,m200";
					GetCurrencyImageTask imgTask = new GetCurrencyImageTask();
					imgTask.execute(imgUrl);
				}
				if (which == 2) {
					String imgUrl = "http://chart.finance.yahoo.com/z?s="
							+ from + to + "=X&t=6m&q=l&l=on&z=s&p=m50,m200";
					GetCurrencyImageTask imgTask = new GetCurrencyImageTask();
					imgTask.execute(imgUrl);
				}
				if (which == 3) {
					String imgUrl = "http://chart.finance.yahoo.com/z?s="
							+ from + to + "=X&t=1y&q=l&l=on&z=s&p=m50,m200";
					GetCurrencyImageTask imgTask = new GetCurrencyImageTask();
					imgTask.execute(imgUrl);
				}
				if (which == 4) {
					String imgUrl = "http://chart.finance.yahoo.com/z?s="
							+ from + to + "=X&t=5y&q=l&l=on&z=s&p=m50,m200";
					GetCurrencyImageTask imgTask = new GetCurrencyImageTask();
					imgTask.execute(imgUrl);
				}
			};
		});
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	public void refresh(View view) {
		if (!isOnline()) {
			loading.setText("No Network Connection");
		} else {
			loading.setText("Loading..");
			fetch();
		}
	}
}
