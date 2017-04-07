package com.fuzzylabs.fuzzywords;

import java.sql.SQLException;

//import com.example.android.searchabledict.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
//import android.support.v4.widget.SimpleCursorAdapter;
import com.fuzzylabs.fuzzywords.R;

public class ViewWordsActivity extends Activity {

	public boolean isSearchResult = false;
	public static int REQUEST_UPDATE = 123;

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_UPDATE && resultCode == RESULT_OK)
			populateFromDB();
	}

	Words info = new Words(this);
	Cursor cursor;
	String word = "NULL";
	String mean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_words);
		Intent intent = getIntent();
		populateFromDB();
		registerOnClickListCallBack();
		setupActionBar();
	}

	private void doMySearch(String query) {

		String[] columns = new String[] { info.KEY_ROWID, info.KEY_WORD,
				info.KEY_MEANING };
		isSearchResult = true;
		try {
			info.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Log.v(getPackageName(), "dialogCall Bhenchod_1");
		String where = info.KEY_WORD + " LIKE ?";
		query = query + "%";
		String[] arg = { query };
		cursor = info.ourDatabase.query(info.DATABASE_TABLE, columns, where,
				arg, null, null, null);

		String[] fromColumns = { info.KEY_WORD, info.KEY_MEANING };
		int[] toViews = { R.id.item_text_1, R.id.item_text_2 };
		SimpleCursorAdapter mAdapter;
		mAdapter = new SimpleCursorAdapter(this, R.layout.item_layout, cursor,
				fromColumns, toViews);
		ListView myList = (ListView) findViewById(R.id.listViewFromDb);
		myList.setAdapter(mAdapter);
		info.close();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.view_words, menu);

		SearchManager searchManager = (SearchManager) getSystemService(this.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(
				R.id.options_menu_main_search).getActionView();

		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		searchView.setIconifiedByDefault(true);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.options_menu_main_search:
			onSearchRequested();
			return true;
		case android.R.id.home:
			onBackPressed();
			return true;
		default:
			return false;
		}
	}

	public void populateFromDB() {

		SelectWordGroup swg = new SelectWordGroup();
		String where = "";
		String[] args = new String[4];// {"a%","b%","c%","d%"};
		String[] columns = new String[] { info.KEY_ROWID, info.KEY_WORD,
				info.KEY_MEANING };
		String[] args6 = new String[6];
		if (swg.g_buttonPressed >= 1 && swg.g_buttonPressed <= 5) {
			where = info.KEY_WORD + " LIKE ? OR " + info.KEY_WORD
					+ " LIKE ? OR " + info.KEY_WORD + " LIKE ? OR "
					+ info.KEY_WORD + " LIKE ?";
			if (swg.g_buttonPressed == 1) {
				args[0] = "a%";
				args[1] = "b%";
				args[2] = "c%";
				args[3] = "d%";
			} else if (swg.g_buttonPressed == 2) {
				args[0] = "e%";
				args[1] = "f%";
				args[2] = "g%";
				args[3] = "h%";
			} else if (swg.g_buttonPressed == 3) {
				args[0] = "i%";
				args[1] = "j%";
				args[2] = "k%";
				args[3] = "l%";
			} else if (swg.g_buttonPressed == 4) {
				args[0] = "m%";
				args[1] = "n%";
				args[2] = "o%";
				args[3] = "p%";
			} else if (swg.g_buttonPressed == 5) {
				args[0] = "q%";
				args[1] = "r%";
				args[2] = "s%";
				args[3] = "t%";
			}
			try {

				info.open();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Log.v(getPackageName(), "dialogCall Bhenchod_1");
			cursor = info.ourDatabase.query(info.DATABASE_TABLE, columns,
					where, args, null, null, null);
			cursor.moveToFirst();
			Log.v(getPackageName(), "cursor mil gaya");

			String[] fromColumns = { info.KEY_WORD, info.KEY_MEANING };
			int[] toViews = { R.id.item_text_1, R.id.item_text_2 };
			SimpleCursorAdapter mAdapter;
			mAdapter = new SimpleCursorAdapter(this, R.layout.item_layout,
					cursor, fromColumns, toViews);
			ListView myList = (ListView) findViewById(R.id.listViewFromDb);
			myList.setAdapter(mAdapter);
			info.close();
			// String[] args = {"a%","b%","c%","d%"};
		} else if (swg.g_buttonPressed == 6) {
			try {
				info.open();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.v(getPackageName(), "dialogCall Bhenchod_1");
			args6[0] = "u%";
			args6[1] = "v%";
			args6[2] = "w%";
			args6[3] = "x%";
			args6[4] = "y%";
			args6[5] = "z%";
			where = info.KEY_WORD + " LIKE ? OR " + info.KEY_WORD
					+ " LIKE ? OR " + info.KEY_WORD + " LIKE ? OR "
					+ info.KEY_WORD + " LIKE ? OR " + info.KEY_WORD
					+ " LIKE ? OR " + info.KEY_WORD + " LIKE ?";
			cursor = info.ourDatabase.query(info.DATABASE_TABLE, columns,
					where, args6, null, null, null);
			cursor.moveToFirst();
			Log.v(getPackageName(), "cursor mil gaya");

			String[] fromColumns = { info.KEY_WORD, info.KEY_MEANING };
			int[] toViews = { R.id.item_text_1, R.id.item_text_2 };
			SimpleCursorAdapter mAdapter;
			mAdapter = new SimpleCursorAdapter(this, R.layout.item_layout,
					cursor, fromColumns, toViews);
			ListView myList = (ListView) findViewById(R.id.listViewFromDb);
			myList.setAdapter(mAdapter);
			info.close();
		}

	}

	private void registerOnClickListCallBack() {

		ListView myList = (ListView) findViewById(R.id.listViewFromDb);
		myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long idInDb) {

				String[] columns = { info.KEY_WORD, info.KEY_MEANING };

				try {
					Log.v(getPackageName(), "dialogCall Before Open");
					info.open();
					Log.v(getPackageName(), "dialogCall open");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Cursor cursor = info.ourDatabase.query(info.DATABASE_TABLE,
						columns, info.KEY_ROWID + "=" + idInDb, null, null,
						null, null);
				if (cursor.moveToFirst()) {
					Log.v(getPackageName(), "dialogCall after moveToFirst");
					word = cursor.getString(cursor
							.getColumnIndex(info.KEY_WORD));
					mean = cursor.getString(cursor
							.getColumnIndex(info.KEY_MEANING));
				}

				AlertDialog.Builder builder = new AlertDialog.Builder(
						ViewWordsActivity.this);
				builder.setTitle(word);
				String[] options = { "Edit", "Delete", "Share" };
				builder.setItems(options,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case 1:
									boolean diditwork;
									Words info1 = new Words(getBaseContext());
									try {

										info1.open();
										info1.ourDatabase.delete(
												info1.DATABASE_TABLE,
												info.KEY_WORD + "=?",
												new String[] { word });
									} catch (SQLException e) {
										Toast.makeText(ViewWordsActivity.this,
												"Oops. Damn it!",
												Toast.LENGTH_SHORT).show();
									} finally {
										Toast.makeText(ViewWordsActivity.this,
												"Deleted", Toast.LENGTH_SHORT)
												.show();
										info1.close();
										populateFromDB();
									}
									break;
								case 0:
									Intent intent = new Intent(
											ViewWordsActivity.this,
											Update.class);
									intent.putExtra("word1", word);
									intent.putExtra("mean1", mean);
									startActivityForResult(intent,
											REQUEST_UPDATE);
									break;
								case 2:
									String thisApp = "https://play.google.com/store/apps/details?id=com.fuzzylabs.fuzzywords";
									String body = "***Fuzzy Words***\n **Fuzzy Labs**\n \n"
											+ word
											+ "\n\n"
											+ mean
											+ "\n\n Download this app: "
											+ thisApp;

									Intent sendIntent = new Intent();
									sendIntent.setAction(Intent.ACTION_SEND);
									sendIntent
											.putExtra(Intent.EXTRA_TEXT, body);
									sendIntent.setType("text/plain");
									startActivity(Intent.createChooser(
											sendIntent, "Fuzzy Labs | Words"));
									break;
								}
							}

						});
				cursor.close();
				info.close();
				AlertDialog alertDialog = builder.create();
				alertDialog.show();
			}

		});
	}

	@Override
	protected void onDestroy() {
		cursor.close();
		super.onDestroy();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			doMySearch(query);
		}
	}
}
