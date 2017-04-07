package com.fuzzylabs.currencyconverter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.fuzzylabs.currencyconverter.R;
import com.fuzzylabs.currencyconverter.Currencies;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ViewAllActivity extends Activity {

	Currencies info = new Currencies(this);
	Cursor cursor;
	TextView loading;
	Dialog dialogbox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_all);
		loading = (TextView) findViewById(R.id.loading);

		if (!isOnline()) {
			loading.setText("No Network Connection");
			populateList();
		} else {
			loading.setText("Loading..");
			SetViewAllTask sv = new SetViewAllTask();
			sv.execute("");
		}
		registerOnClickListCallBack();

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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.view_all, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.deleteAll:

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);
			
			alertDialogBuilder.setTitle("Delete All");
			alertDialogBuilder
					.setMessage("Clear your Favorites?")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {

									try {
										info.open();
										info.ourDatabase
												.delete(info.DATABASE_TABLE,
														null, null);
									} catch (SQLException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									dialog.cancel();
									populateList();
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {

									dialog.cancel();
								}
							});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
			return true;

		case R.id.refresh:
			if (!isOnline()) {
				loading.setText("No Network Connection");
				populateList();
			} else {
				loading.setText("Loading..");
				SetViewAllTask sv = new SetViewAllTask();
				sv.execute("");
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void populateList() {
		try {
			info.open();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String[] columns = { info.KEY_ROWID, info.FROM_CURR, info.TO_CURR,
				info.VALUE };
		cursor = info.ourDatabase.query(info.DATABASE_TABLE, columns, null,
				null, null, null, null);

		String[] fromColumns = { info.FROM_CURR, info.TO_CURR, info.VALUE };
		int[] toViews = { R.id.item_text_1, R.id.item_text_2, R.id.item_text_3 };
		SimpleCursorAdapter mAdapter;
		mAdapter = new SimpleCursorAdapter(ViewAllActivity.this,
				R.layout.item_layout, cursor, fromColumns, toViews);
		ListView myList = (ListView) findViewById(R.id.listViewFromDb);
		myList.setAdapter(mAdapter);
	}

	private class SetViewAllTask extends AsyncTask<String, Void, Cursor> {

		@Override
		protected Cursor doInBackground(String... arg0) {
			try {
				info.open();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String[] columns = { info.KEY_ROWID, info.FROM_CURR, info.TO_CURR,
					info.VALUE };
			cursor = info.ourDatabase.query(info.DATABASE_TABLE, columns, null,
					null, null, null, null);

			while (cursor.moveToNext()) {
				String from = cursor.getString(1);
				String to = cursor.getString(2);
				String urlStr = new String(
						"http://finance.yahoo.com/d/quotes.csv?s=" + from + to
								+ "=X&f=l1&e=.csv");
				String response = "";
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(urlStr);
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

				ContentValues cv = new ContentValues();
				cv.put(info.VALUE, response);
				String where = info.KEY_ROWID + "=" + cursor.getInt(0);
				info.ourDatabase.update(info.DATABASE_TABLE, cv, where, null);
			}

			cursor.requery();
			return cursor;
		}

		@Override
		protected void onPostExecute(Cursor result) {
			Log.v("Saka", "Saka");
			String[] fromColumns = { info.FROM_CURR, info.TO_CURR, info.VALUE };
			int[] toViews = { R.id.item_text_1, R.id.item_text_2,
					R.id.item_text_3 };
			SimpleCursorAdapter mAdapter;
			mAdapter = new SimpleCursorAdapter(ViewAllActivity.this,
					R.layout.item_layout, result, fromColumns, toViews);
			ListView myList = (ListView) findViewById(R.id.listViewFromDb);
			myList.setAdapter(mAdapter);
			loading.setText("");
		}
	}

	private void registerOnClickListCallBack() {

		ListView myList = (ListView) findViewById(R.id.listViewFromDb);
		myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long idInDb) {

				String[] columns = { info.FROM_CURR, info.TO_CURR };
				try {
					info.open();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				final int id = (int) idInDb;
				Cursor cursor = info.ourDatabase.query(info.DATABASE_TABLE,
						columns, info.KEY_ROWID + "=" + idInDb, null, null,
						null, null);
				String deleteFrom = "";
				String deleteTo = "";
				if (cursor.moveToFirst()) {
					Log.v(getPackageName(), "dialogCall after moveToFirst");
					deleteFrom = cursor.getString(cursor
							.getColumnIndex(info.FROM_CURR));
					deleteTo = cursor.getString(cursor
							.getColumnIndex(info.TO_CURR));
				}
				cursor.close();
				info.close();

				dialogbox = new Dialog(ViewAllActivity.this);
				dialogbox.setContentView(R.layout.delete_dialog);
				dialogbox.setTitle(deleteFrom + " - " + deleteTo);
				Button button = (Button) dialogbox
						.findViewById(R.id.deleteButton);
				button.setText("Delete");
				button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							info.open();
							info.ourDatabase.delete(info.DATABASE_TABLE,
									info.KEY_ROWID + "=" + id, null);
						} catch (SQLException e) {
							Toast.makeText(ViewAllActivity.this, "Error",
									Toast.LENGTH_SHORT).show();
						} finally {
							dialogbox.dismiss();
							Toast.makeText(ViewAllActivity.this, "Deleted",
									Toast.LENGTH_SHORT).show();
							if (!isOnline()) {
								loading.setText("No Network Connection");
								populateList();
							} else {
								loading.setText("Loading..");
								SetViewAllTask sv = new SetViewAllTask();
								sv.execute("");
							}
						}
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
			}

		});
	}
}
