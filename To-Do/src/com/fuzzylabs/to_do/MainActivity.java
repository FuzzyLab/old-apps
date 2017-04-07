package com.fuzzylabs.to_do;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Calendar;

import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.DatePicker.OnDateChangedListener;

public class MainActivity extends Activity implements OnNavigationListener {

	TodoDb info = new TodoDb(this);
	Cursor cursor = null;
	final DecimalFormat df = new DecimalFormat("00");
	private NotifyReceiverMain receiverNotify;
	private AlarmReceiverMain receiverAlarm;
	private final String[] dropdownValues = { "TODAY", "ALL JOBS",
			"Very Urgent", "Urgent", "Medium", "Low", "DATE" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				actionBar.getThemedContext(),
				android.R.layout.simple_spinner_item, android.R.id.text1,
				dropdownValues);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		actionBar.setListNavigationCallbacks(adapter, this);

		ListView list = (ListView) findViewById(R.id.jobsList);
		// setTitle("Today");

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {

				View toolbar = view.findViewById(R.id.toolbar);
				ExpandAnimation expandAni = new ExpandAnimation(toolbar, 300);
				toolbar.startAnimation(expandAni);
			}
		});

		populate(Selection.type);
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		Selection.type = position;
		if (position == 6) {
			Calendar calendar = Calendar.getInstance();
			final int day = calendar.get(Calendar.DAY_OF_MONTH);
			int month = calendar.get(Calendar.MONTH);
			int year = calendar.get(Calendar.YEAR);
			final DatePickerDialog dpd = new DatePickerDialog(
					MainActivity.this,
					new DatePickerDialog.OnDateSetListener() {
						@Override
						public void onDateSet(DatePicker view, int yearS,
								int monthOfYear, int dayOfMonth) {
							int day = dayOfMonth;
							int month = monthOfYear + 1;
							int year = yearS;
							String dueDate = "" + year + "-" + df.format(month)
									+ "-" + df.format(day);
							Selection.date = dueDate;
							populate(Selection.type);
						}
					}, year, month, day);
			dpd.show();
		} else {
			populate(Selection.type);
		}
		return true;
	}

	public void populate(int position) {

		try {
			info.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH) + 1;
		int year = calendar.get(Calendar.YEAR);

		String today = "" + year + "-" + df.format(month) + "-"
				+ df.format(day);
		ContentValues cv = new ContentValues();
		cv.put(info.IS_DONE, 1);
		String where = "Date(dueDate) < Date(?)";
		String[] args = { today };
		info.ourDatabase.update(info.DATABASE_TABLE, cv, where, args);
		info.close();

		if (position == 0) {
			populateList(Selection.type);
		} else if (position == 1) {
			populateList(Selection.type);
		} else if (position == 2) {
			populateOnFilterUrgency(0);
		} else if (position == 3) {
			populateOnFilterUrgency(1);
		} else if (position == 4) {
			populateOnFilterUrgency(2);
		} else if (position == 5) {
			populateOnFilterUrgency(3);
		} else if (position == 6) {
			populateOnFilterDate(Selection.date);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		{
			IntentFilter filterNotify = new IntentFilter(
					NotifyReceiverMain.ACTION_NOTIFICATION);
			filterNotify.addCategory(Intent.CATEGORY_DEFAULT);
			receiverNotify = new NotifyReceiverMain();
			registerReceiver(receiverNotify, filterNotify);
		}
		{
			IntentFilter filterAlarm = new IntentFilter(
					AlarmReceiverMain.ACTION_ALARM);
			filterAlarm.addCategory(Intent.CATEGORY_DEFAULT);
			receiverAlarm = new AlarmReceiverMain();
			registerReceiver(receiverAlarm, filterAlarm);
		}

		final Context context = getApplicationContext();
		if (getIntent().hasExtra("Alarm")) {
			final String id = getIntent().getStringExtra("Alarm");

			AlertDialog.Builder builder = new AlertDialog.Builder(
					MainActivity.this);
			builder.setCancelable(false);
			builder.setTitle("Alarm");
			String[] options = { "Stop", "Snooze" };
			builder.setItems(options, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						Intent stopIntent1 = new Intent(context,
								AlarmService.class);
						stopIntent1.putExtra("job_id", id);
						context.stopService(stopIntent1);

						try {
							info.open();
							ContentValues cv = new ContentValues();
							cv.put(info.IS_NOTIFICATION, 0);
							cv.put(info.IS_ALARM, 0);
							cv.put(info.IS_DONE, 1);
							String where = info.KEY_ROWID + " LIKE ?";
							String[] arg = { id };
							info.ourDatabase.update(info.DATABASE_TABLE, cv,
									where, arg);
							info.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						populate(Selection.type);
						break;
					case 1:
						Intent stopIntent2 = new Intent(context,
								AlarmService.class);
						stopIntent2.putExtra("job_id", id);
						context.stopService(stopIntent2);

						final AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
						Intent intent = new Intent(MainActivity.this,
								AlarmService.class);
						intent.putExtra("job_id", id);
						final PendingIntent pi = PendingIntent.getService(
								MainActivity.this, Integer.parseInt(id),
								intent, 0);
						final Calendar calendar = Calendar.getInstance();
						calendar.setTimeInMillis(System.currentTimeMillis());

						AlertDialog.Builder snoBuilder = new AlertDialog.Builder(
								MainActivity.this);
						snoBuilder.setCancelable(false);
						snoBuilder.setTitle("Snooze By");
						String[] options = { "5 Min", "10 Min", "15 Min",
								"30 Min", "1 Hour" };
						snoBuilder.setItems(options,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										switch (which) {
										case 0:
											calendar.add(Calendar.MINUTE, 5);
											am.set(AlarmManager.RTC_WAKEUP,
													calendar.getTimeInMillis(),
													pi);
											break;
										case 1:
											calendar.add(Calendar.MINUTE, 10);
											am.set(AlarmManager.RTC_WAKEUP,
													calendar.getTimeInMillis(),
													pi);
											break;
										case 2:
											calendar.add(Calendar.MINUTE, 15);
											am.set(AlarmManager.RTC_WAKEUP,
													calendar.getTimeInMillis(),
													pi);
											break;
										case 3:
											calendar.add(Calendar.MINUTE, 30);
											am.set(AlarmManager.RTC_WAKEUP,
													calendar.getTimeInMillis(),
													pi);
											break;
										case 4:
											calendar.add(Calendar.MINUTE, 60);
											am.set(AlarmManager.RTC_WAKEUP,
													calendar.getTimeInMillis(),
													pi);
											break;
										}
									}
								});
						snoBuilder.show();
						break;
					}
				}
			});
			AlertDialog alertDialog = builder.create();
			alertDialog.show();
			getIntent().removeExtra("Alarm");
		}
		populate(Selection.type);
	};

	public void populateList(int type) {
		try {
			info.open();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH) + 1;
		int year = calendar.get(Calendar.YEAR);

		String today = "" + year + "-" + df.format(month) + "-"
				+ df.format(day);

		String[] arg = { today };
		String where = info.DUE_DATE + " like ?";

		if (type == 1) {
			where = null;
			arg = null;
		}
		String[] columns = { info.KEY_ROWID, info.JOB_NAME, info.DUE_DATE,
				info.DUE_TIME, info.IS_NOTIFICATION, info.IS_ALARM,
				info.IS_DONE, info.URGENCY, info.NOTES };
		cursor = info.ourDatabase.query(info.DATABASE_TABLE, columns, where,
				arg, null, null, "Date(dueDate)");

		String[] fromColumns = { info.JOB_NAME, info.DUE_DATE, info.DUE_TIME };
		int[] toViews = { R.id.job, R.id.date, R.id.time };
		SimpleCursorAdapter mAdapter;
		mAdapter = new Custom_Adapter(MainActivity.this, R.layout.list_item,
				cursor, fromColumns, toViews);
		ListView myList = (ListView) findViewById(R.id.jobsList);
		myList.setAdapter(mAdapter);

		info.close();
	}

	public void populateOnFilterJob(String job) {

		try {
			info.open();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		job = "%" + job + "%";
		String[] arg = { job };
		String[] columns = { info.KEY_ROWID, info.JOB_NAME, info.DUE_DATE,
				info.DUE_TIME, info.IS_NOTIFICATION, info.IS_ALARM,
				info.IS_DONE, info.URGENCY, info.NOTES };
		cursor = info.ourDatabase.query(info.DATABASE_TABLE, columns,
				info.JOB_NAME + " like ?", arg, null, null, "Date(dueDate)");

		String[] fromColumns = { info.JOB_NAME, info.DUE_DATE, info.DUE_TIME };
		int[] toViews = { R.id.job, R.id.date, R.id.time };
		SimpleCursorAdapter mAdapter;
		mAdapter = new Custom_Adapter(MainActivity.this, R.layout.list_item,
				cursor, fromColumns, toViews);
		ListView myList = (ListView) findViewById(R.id.jobsList);
		myList.setAdapter(mAdapter);

		info.close();
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);

		SearchManager searchManager = (SearchManager) getSystemService(this.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.search)
				.getActionView();

		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		searchView.setIconifiedByDefault(true);
		searchView.setOnQueryTextListener(queryTextListener);

		MenuItem menuItem = menu.findItem(R.id.search);
		menuItem.setOnActionExpandListener(new OnActionExpandListener() {

			@Override
			public boolean onMenuItemActionCollapse(MenuItem arg0) {
				populate(Selection.type);
				return true;
			}

			@Override
			public boolean onMenuItemActionExpand(MenuItem arg0) {
				return true;
			}

		});

		return true;
	}

	final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
		@Override
		public boolean onQueryTextChange(String newText) {
			populateOnFilterJob(newText);
			return true;
		}

		@Override
		public boolean onQueryTextSubmit(String query) {
			return true;
		}
	};

	public void populateOnFilterUrgency(int urgency) {

		try {
			info.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String[] arg = { String.valueOf(urgency) };
		String[] columns = { info.KEY_ROWID, info.JOB_NAME, info.DUE_DATE,
				info.DUE_TIME, info.IS_NOTIFICATION, info.IS_ALARM,
				info.IS_DONE, info.URGENCY, info.NOTES };
		cursor = info.ourDatabase.query(info.DATABASE_TABLE, columns,
				info.URGENCY + " like ?", arg, null, null, "Date(dueDate)");

		String[] fromColumns = { info.JOB_NAME, info.DUE_DATE, info.DUE_TIME };
		int[] toViews = { R.id.job, R.id.date, R.id.time };
		SimpleCursorAdapter mAdapter;
		mAdapter = new Custom_Adapter(MainActivity.this, R.layout.list_item,
				cursor, fromColumns, toViews);
		ListView myList = (ListView) findViewById(R.id.jobsList);
		myList.setAdapter(mAdapter);

		info.close();
	}

	public void populateOnFilterDate(String date) {

		try {
			info.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String[] arg = { date };
		String[] columns = { info.KEY_ROWID, info.JOB_NAME, info.DUE_DATE,
				info.DUE_TIME, info.IS_NOTIFICATION, info.IS_ALARM,
				info.IS_DONE, info.URGENCY, info.NOTES };
		cursor = info.ourDatabase.query(info.DATABASE_TABLE, columns,
				info.DUE_DATE + " like ?", arg, null, null, null);

		String[] fromColumns = { info.JOB_NAME, info.DUE_DATE, info.DUE_TIME };
		int[] toViews = { R.id.job, R.id.date, R.id.time };
		SimpleCursorAdapter mAdapter;
		mAdapter = new Custom_Adapter(MainActivity.this, R.layout.list_item,
				cursor, fromColumns, toViews);
		ListView myList = (ListView) findViewById(R.id.jobsList);
		myList.setAdapter(mAdapter);

		info.close();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			LayoutInflater inflater = getLayoutInflater();
			builder.setView(inflater.inflate(R.layout.add_dialog, null));
			final AlertDialog ad = builder.create();
			ad.setTitle("Add a Task");
			ad.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			ad.setButton(AlertDialog.BUTTON_POSITIVE, "Add",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							boolean diditwork = true;
							EditText jobView = (EditText) ad
									.findViewById(R.id.addJob);
							String jobName = jobView.getText().toString();
							DatePicker dp = (DatePicker) ad
									.findViewById(R.id.addDueDate);
							int day = dp.getDayOfMonth();
							int month = dp.getMonth() + 1;
							int year = dp.getYear();
							Spinner urg = (Spinner) ad
									.findViewById(R.id.urgencyList);
							int urgval = urg.getSelectedItemPosition();
							String dueDate = "" + year + "-" + df.format(month)
									+ "-" + df.format(day);
							try {
								info.open();
								info.createEntry(jobName, dueDate, "", 0, 0, 0,
										urgval, "");
								info.close();
							} catch (SQLException e) {
								diditwork = false;
								e.printStackTrace();
							} finally {
								if (diditwork) {
									Toast.makeText(MainActivity.this, "Added",
											Toast.LENGTH_SHORT).show();
									populate(Selection.type);
								} else
									Toast.makeText(MainActivity.this, "Oops",
											Toast.LENGTH_SHORT).show();
								ad.dismiss();
							}
						}
					});
			ad.show();
			return true;
		case R.id.clearAll:
			AlertDialog.Builder builder2 = new AlertDialog.Builder(
					MainActivity.this);
			builder2.setTitle("Delete All Done?");
			builder2.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}

					});
			builder2.setPositiveButton("Delete",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							try {
								info.open();
								info.ourDatabase.delete(info.DATABASE_TABLE,
										info.IS_DONE + "=1", null);
								info.close();
							} catch (SQLException e) {
							} finally {
								dialog.dismiss();
								populate(Selection.type);
							}
						}
					});
			AlertDialog alertDialog2 = builder2.create();
			alertDialog2.show();
			return true;
		case R.id.about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;
		case R.id.rate:
			String url = "https://play.google.com/store/apps/details?id=com.fuzzylabs.to_do";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			return true;
		case R.id.share:
			String thisApp = "https://play.google.com/store/apps/details?id=com.fuzzylabs.to_do";

			String body = "---To Do---\n --Fuzzy Labs--"
					+ "\n\n Download this app: " + thisApp;

			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, body);
			sendIntent.setType("text/plain");
			startActivity(Intent
					.createChooser(sendIntent, "Fuzzy Labs | To Do"));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public class Custom_Adapter extends SimpleCursorAdapter {

		private Context mContext;
		private Context appContext;
		private int layout;
		private Cursor cr;
		private final LayoutInflater inflater;

		public Custom_Adapter(Context context, int layout, Cursor c,
				String[] from, int[] to) {
			super(context, layout, c, from, to);
			this.layout = layout;
			this.mContext = context;
			this.inflater = LayoutInflater.from(context);
			this.cr = c;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return inflater.inflate(layout, null);
		}

		@Override
		public void bindView(final View view, final Context context,
				Cursor cursor) {
			super.bindView(view, context, cursor);

			View urgencyView = (View) view.findViewById(R.id.urgencyView);

			final int urgency = cursor.getInt(7);
			if (urgency == 0)
				urgencyView.setBackgroundColor(0xffee0000);
			else if (urgency == 1)
				urgencyView.setBackgroundColor(0xff0000ee);
			else if (urgency == 2)
				urgencyView.setBackgroundColor(0xff00ee00);
			else
				urgencyView.setBackgroundColor(0xffeeee00);

			final TextView jobView = (TextView) view.findViewById(R.id.job);
			final TextView dueDateView = (TextView) view
					.findViewById(R.id.date);
			Button delete = (Button) view.findViewById(R.id.deleteBtn);
			Button edit = (Button) view.findViewById(R.id.editBtn);
			Button setNotification = (Button) view
					.findViewById(R.id.setNotificationBtn);
			Button setAlarm = (Button) view.findViewById(R.id.setAlarmBtn);
			Button done = (Button) view.findViewById(R.id.setDoneBtn);
			Button notes = (Button) view.findViewById(R.id.notesBtn);

			final int is_done = cursor.getInt(6);
			if (is_done == 1) {
				((TextView) view.findViewById(R.id.job))
						.setTextColor(0xffee0000);
				((Button) view.findViewById(R.id.setDoneBtn))
						.setBackgroundResource(R.drawable.donedark);
				setNotification.setEnabled(false);
				setAlarm.setEnabled(false);
				setAlarm.setBackgroundResource(R.drawable.alarmdisabled);
				setNotification
						.setBackgroundResource(R.drawable.notificationdisabled);
			}

			final int is_notification = cursor.getInt(4);
			if (is_notification == 1) {
				((TextView) view.findViewById(R.id.time))
						.setTextColor(0xff0000ee);
				((Button) view.findViewById(R.id.setNotificationBtn))
						.setBackgroundResource(R.drawable.notificationdark);
			}
			final int is_alarm = cursor.getInt(5);
			if (is_alarm == 1) {
				((TextView) view.findViewById(R.id.time))
						.setTextColor(0xffee0000);
				((Button) view.findViewById(R.id.setAlarmBtn))
						.setBackgroundResource(R.drawable.alarmdark);
			}

			final String id = cursor.getString(0);

			notes.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							MainActivity.this);
					builder.setCancelable(false);
					String[] options = { "Notes", "Checklist" };
					builder.setItems(options,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									case 0:
										AlertDialog.Builder nbuilder = new AlertDialog.Builder(
												MainActivity.this);
										LayoutInflater ninflater = getLayoutInflater();
										nbuilder.setView(ninflater.inflate(
												R.layout.notes_dialog, null));
										final AlertDialog nad = nbuilder
												.create();
										nad.setTitle("Notes");
										nad.setButton(
												AlertDialog.BUTTON_NEGATIVE,
												"Clear",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int which) {
													}
												});
										nad.setButton(
												AlertDialog.BUTTON_POSITIVE,
												"Ok",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int which) {
													}
												});
										nad.show();

										nad.getButton(
												AlertDialog.BUTTON_NEGATIVE)
												.setOnClickListener(
														new OnClickListener() {
															@Override
															public void onClick(
																	View v) {
																try {
																	info.open();
																	ContentValues cv = new ContentValues();
																	cv.put(info.NOTES,
																			"");
																	String where = info.KEY_ROWID
																			+ " LIKE ?";
																	String[] arg = { id };
																	info.ourDatabase
																			.update(info.DATABASE_TABLE,
																					cv,
																					where,
																					arg);
																	info.close();

																} catch (SQLException e) {
																	e.printStackTrace();
																}

																((TextView) nad
																		.findViewById(R.id.notesView))
																		.setText("");
																Toast.makeText(
																		MainActivity.this,
																		"Notes Cleared",
																		Toast.LENGTH_SHORT)
																		.show();
															}

														});
										nad.getButton(
												AlertDialog.BUTTON_POSITIVE)
												.setOnClickListener(
														new OnClickListener() {

															@Override
															public void onClick(
																	View v) {
																nad.dismiss();
															}
														});

										String prenotes = "";
										try {
											info.open();
											String[] columns = { info.NOTES };
											String[] arg = { id };
											Cursor curse = info.ourDatabase
													.query(info.DATABASE_TABLE,
															columns,
															info.KEY_ROWID
																	+ " like ?",
															arg, null, null,
															null);
											curse.moveToFirst();
											prenotes = curse.getString(0);
											info.close();

										} catch (SQLException e) {
											e.printStackTrace();
										}

										((TextView) nad
												.findViewById(R.id.notesView))
												.setText(prenotes);

										Button nadd = (Button) nad
												.findViewById(R.id.addNote);
										nadd.setOnClickListener(new OnClickListener() {

											@Override
											public void onClick(View arg0) {
												String newtext = ((EditText) nad
														.findViewById(R.id.newNote))
														.getText().toString()
														.trim();
												if (!newtext.equals("")) {
													String prenotes = "";

													try {
														info.open();
														String[] columns = { info.NOTES };
														String[] arg = { id };
														Cursor curse = info.ourDatabase
																.query(info.DATABASE_TABLE,
																		columns,
																		info.KEY_ROWID
																				+ " like ?",
																		arg,
																		null,
																		null,
																		null);
														curse.moveToFirst();
														prenotes = curse
																.getString(0);
														info.close();

													} catch (SQLException e) {
														e.printStackTrace();
													}

													String newnotes = prenotes
															+ newtext + "\n";
													((TextView) nad
															.findViewById(R.id.notesView))
															.setText(newnotes);

													try {
														info.open();
														ContentValues cv = new ContentValues();
														cv.put(info.NOTES,
																newnotes);
														String where = info.KEY_ROWID
																+ " LIKE ?";
														String[] arg = { id };
														info.ourDatabase
																.update(info.DATABASE_TABLE,
																		cv,
																		where,
																		arg);
														info.close();

													} catch (SQLException e) {
														e.printStackTrace();
													}
												}
												((EditText) nad
														.findViewById(R.id.newNote))
														.setText("");
											}
										});
										break;
									case 1:
										AlertDialog.Builder cbuilder = new AlertDialog.Builder(
												MainActivity.this);
										LayoutInflater cinflater = getLayoutInflater();
										cbuilder.setView(cinflater
												.inflate(
														R.layout.checklist_dialog,
														null));
										final AlertDialog cad = cbuilder
												.create();
										cad.setTitle("Checklist");
										cad.setButton(
												AlertDialog.BUTTON_NEGATIVE,
												"Clear",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int which) {
													}
												});
										cad.setButton(
												AlertDialog.BUTTON_POSITIVE,
												"Ok",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int which) {
													}
												});
										cad.show();

										cad.getButton(
												AlertDialog.BUTTON_NEGATIVE)
												.setOnClickListener(
														new OnClickListener() {
															@Override
															public void onClick(
																	View v) {
																try {
																	info.open();
																	String where = info.JOB_ID
																			+ " LIKE ?";
																	String[] arg = { id };
																	info.ourDatabase
																			.delete(info.CHECKLIST_TABLE,
																					where,
																					arg);
																	
																	Cursor curse = info
																			.getAllRows(id);
																	String[] fromColumns = { info.ITEM_NAME };
																	int[] toViews = { R.id.checkText };
																	SimpleCursorAdapter mAdapter;
																	mAdapter = new Checklist_Adapter(
																			MainActivity.this,
																			R.layout.checklist_item, cad,
																			curse, fromColumns, toViews);
																	ListView myList = (ListView) cad.findViewById(R.id.checklistView);
																	myList.setAdapter(mAdapter);
																	
																	info.close();

																} catch (SQLException e) {
																	e.printStackTrace();
																}

																Toast.makeText(
																		MainActivity.this,
																		"Checklist Cleared",
																		Toast.LENGTH_SHORT)
																		.show();
															}

														});
										cad.getButton(
												AlertDialog.BUTTON_POSITIVE)
												.setOnClickListener(
														new OnClickListener() {

															@Override
															public void onClick(
																	View v) {
																cad.dismiss();
															}
														});

										try {
											info.open();
											Cursor curse = info
													.getAllRows(id);
											String[] fromColumns = { info.ITEM_NAME };
											int[] toViews = { R.id.checkText };
											SimpleCursorAdapter mAdapter;
											mAdapter = new Checklist_Adapter(
													MainActivity.this,
													R.layout.checklist_item, cad,
													curse, fromColumns, toViews);
											ListView myList = (ListView) cad.findViewById(R.id.checklistView);
											myList.setAdapter(mAdapter);
											info.close();

										} catch (SQLException e) {
											e.printStackTrace();
										}

										Button cadd = (Button) cad
												.findViewById(R.id.addItem);
										cadd.setOnClickListener(new OnClickListener() {

											@Override
											public void onClick(View view) {
												String newItem = ((EditText) cad
														.findViewById(R.id.newItemText))
														.getText().toString()
														.trim();
												if (!newItem.equals("")) {

													try {
														info.open();
														info.createEntry(
																id, newItem, 0);

														Cursor curse = info
																.getAllRows(id);
														String[] fromColumns = { info.ITEM_NAME };
														int[] toViews = { R.id.checkText };
														SimpleCursorAdapter mAdapter;
														mAdapter = new Checklist_Adapter(
																MainActivity.this,
																R.layout.checklist_item, cad,
																curse,
																fromColumns,
																toViews);
														ListView myList = (ListView) cad.findViewById(R.id.checklistView);
														myList.setAdapter(mAdapter);
														info.close();

													} catch (SQLException e) {
														e.printStackTrace();
													}
												}
												((EditText) cad
														.findViewById(R.id.newItemText))
														.setText("");
											}
										});
										break;
									}
								}
							});
					AlertDialog alertDialog = builder.create();
					alertDialog.show();

				}
			});

			done.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (is_done == 0) {
						{
							Intent stopIntent = new Intent(
									getApplicationContext(), AlarmService.class);
							stopIntent.putExtra("job_id", id);
							context.stopService(stopIntent);
						}

						{
							AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
							Intent intent = new Intent(MainActivity.this,
									AlarmService.class);
							intent.putExtra("job_id", id);
							PendingIntent pi = PendingIntent.getService(
									MainActivity.this, Integer.parseInt(id),
									intent, 0);
							am.cancel(pi);
						}
						{
							AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
							Intent intent = new Intent(MainActivity.this,
									NotificationReceiver.class);
							intent.putExtra("job_id", id);
							PendingIntent pi = PendingIntent.getBroadcast(
									MainActivity.this, Integer.parseInt(id),
									intent, 0);
							am.cancel(pi);
						}
						{
							NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
							mNotificationManager.cancel(Integer.parseInt(id));
						}

						try {
							info.open();
							ContentValues cv = new ContentValues();
							cv.put(info.IS_NOTIFICATION, 0);
							cv.put(info.IS_ALARM, 0);
							cv.put(info.IS_DONE, 1);
							String where = info.KEY_ROWID + " LIKE ?";
							String[] arg = { id };
							info.ourDatabase.update(info.DATABASE_TABLE, cv,
									where, arg);
							info.close();
						} catch (SQLException e) {
							e.printStackTrace();
						} finally {
							populate(Selection.type);
						}
						Toast.makeText(
								MainActivity.this,
								"Task set as done. Alarm/Notification cancelled.",
								Toast.LENGTH_SHORT).show();
					} else {
						try {
							info.open();
							ContentValues cv = new ContentValues();
							cv.put(info.IS_DONE, 0);
							String where = info.KEY_ROWID + " LIKE ?";
							String[] arg = { id };
							info.ourDatabase.update(info.DATABASE_TABLE, cv,
									where, arg);
							info.close();
						} catch (SQLException e) {
							e.printStackTrace();
						} finally {
							populate(Selection.type);
						}
						Toast.makeText(MainActivity.this,
								"Task set as not done", Toast.LENGTH_SHORT)
								.show();
					}
				}
			});

			setAlarm.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					if (is_alarm == 0) {

						final TimePickerDialog tpd = new TimePickerDialog(
								MainActivity.this, new OnTimeSetListener() {

									@Override
									public void onTimeSet(TimePicker tp,
											int hour, int minute) {
										{
											AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
											Intent intent = new Intent(
													MainActivity.this,
													NotificationReceiver.class);
											intent.putExtra("job_id", id);
											PendingIntent pi = PendingIntent
													.getBroadcast(
															MainActivity.this,
															Integer.parseInt(id),
															intent, 0);
											am.cancel(pi);
										}
										((TextView) view.findViewById(R.id.job))
												.setTextColor(0xff000000);
										AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
										Intent intent = new Intent(
												MainActivity.this,
												AlarmService.class);
										intent.putExtra("job_id", id);
										PendingIntent pi = PendingIntent
												.getService(MainActivity.this,
														Integer.parseInt(id),
														intent, 0);
										Calendar calendar = Calendar
												.getInstance();
										calendar.setTimeInMillis(System
												.currentTimeMillis());
										String[] date = dueDateView.getText()
												.toString().split("-");
										int dueDay = Integer.parseInt(date[2]);
										int dueMonth = Integer
												.parseInt(date[1]) - 1;
										int dueYear = Integer.parseInt(date[0]);
										calendar.set(dueYear, dueMonth, dueDay,
												hour, minute, 0);
										am.set(AlarmManager.RTC_WAKEUP,
												calendar.getTimeInMillis(), pi);
										String dueTime = "" + df.format(hour)
												+ " : " + df.format(minute)
												+ " : 00";
										Toast.makeText(MainActivity.this,
												"Alarm set", Toast.LENGTH_SHORT)
												.show();

										try {
											info.open();
											ContentValues cv = new ContentValues();
											cv.put(info.DUE_TIME, dueTime);
											cv.put(info.IS_DONE, 0);
											cv.put(info.IS_NOTIFICATION, 0);
											cv.put(info.IS_ALARM, 1);
											String where = info.KEY_ROWID
													+ " LIKE ?";
											String[] arg = { id };
											info.ourDatabase.update(
													info.DATABASE_TABLE, cv,
													where, arg);
											info.close();
										} catch (SQLException e) {
											Toast.makeText(MainActivity.this,
													"Error", Toast.LENGTH_SHORT)
													.show();
										} finally {
											populate(Selection.type);
										}
									}

								}, layout, layout, false);
						tpd.setTitle("Set Alarm");
						tpd.show();
					} else {
						AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
						Intent intent = new Intent(MainActivity.this,
								AlarmService.class);
						intent.putExtra("job_id", id);
						PendingIntent pi = PendingIntent.getService(
								MainActivity.this, Integer.parseInt(id),
								intent, 0);
						am.cancel(pi);
						try {
							info.open();
							ContentValues cv = new ContentValues();
							cv.put(info.IS_ALARM, 0);
							String where = info.KEY_ROWID + " LIKE ?";
							String[] arg = { id };
							info.ourDatabase.update(info.DATABASE_TABLE, cv,
									where, arg);
							info.close();
						} catch (SQLException e) {
							Toast.makeText(MainActivity.this, "Error",
									Toast.LENGTH_SHORT).show();
						} finally {
							populate(Selection.type);
						}
						Toast.makeText(MainActivity.this, "Alarm cancelled",
								Toast.LENGTH_SHORT).show();
					}
				}
			});

			setNotification.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					if (is_notification == 0) {

						final TimePickerDialog tpd = new TimePickerDialog(
								MainActivity.this, new OnTimeSetListener() {

									@Override
									public void onTimeSet(TimePicker tp,
											int hour, int minute) {
										{
											AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
											Intent intent = new Intent(
													MainActivity.this,
													AlarmService.class);
											intent.putExtra("job_id", id);
											PendingIntent pi = PendingIntent
													.getService(
															MainActivity.this,
															Integer.parseInt(id),
															intent, 0);
											am.cancel(pi);
										}
										((TextView) view.findViewById(R.id.job))
												.setTextColor(0xff000000);
										AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
										Intent intent = new Intent(
												MainActivity.this,
												NotificationReceiver.class);
										intent.putExtra("job_id", id);
										PendingIntent pi = PendingIntent
												.getBroadcast(
														MainActivity.this,
														Integer.parseInt(id),
														intent, 0);
										Calendar calendar = Calendar
												.getInstance();
										calendar.setTimeInMillis(System
												.currentTimeMillis());
										String[] date = dueDateView.getText()
												.toString().split("-");
										int dueDay = Integer.parseInt(date[2]);
										int dueMonth = Integer
												.parseInt(date[1]) - 1;
										int dueYear = Integer.parseInt(date[0]);
										calendar.set(dueYear, dueMonth, dueDay,
												hour, minute, 0);
										am.set(AlarmManager.RTC_WAKEUP,
												calendar.getTimeInMillis(), pi);
										String dueTime = "" + df.format(hour)
												+ " : " + df.format(minute)
												+ " : 00";
										Toast.makeText(MainActivity.this,
												"Notification set",
												Toast.LENGTH_SHORT).show();

										try {
											info.open();
											ContentValues cv = new ContentValues();
											cv.put(info.DUE_TIME, dueTime);
											cv.put(info.IS_DONE, 0);
											cv.put(info.IS_NOTIFICATION, 1);
											cv.put(info.IS_ALARM, 0);
											String where = info.KEY_ROWID
													+ " LIKE ?";
											String[] arg = { id };
											info.ourDatabase.update(
													info.DATABASE_TABLE, cv,
													where, arg);
											info.close();
										} catch (SQLException e) {
											Toast.makeText(MainActivity.this,
													"Error", Toast.LENGTH_SHORT)
													.show();
										} finally {
											populate(Selection.type);
										}
									}

								}, layout, layout, false);
						tpd.setTitle("Set Notification");
						tpd.show();
					} else {
						AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
						Intent intent = new Intent(MainActivity.this,
								NotificationReceiver.class);
						intent.putExtra("job_id", id);
						PendingIntent pi = PendingIntent.getBroadcast(
								MainActivity.this, Integer.parseInt(id),
								intent, 0);
						am.cancel(pi);
						try {
							info.open();
							ContentValues cv = new ContentValues();
							cv.put(info.IS_NOTIFICATION, 0);
							String where = info.KEY_ROWID + " LIKE ?";
							String[] arg = { id };
							info.ourDatabase.update(info.DATABASE_TABLE, cv,
									where, arg);
							info.close();
						} catch (SQLException e) {
							Toast.makeText(MainActivity.this, "Error",
									Toast.LENGTH_SHORT).show();
						} finally {
							populate(Selection.type);
						}
						Toast.makeText(MainActivity.this,
								"Notification cancelled", Toast.LENGTH_SHORT)
								.show();
					}
				}
			});
			delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					AlertDialog.Builder builder = new AlertDialog.Builder(
							MainActivity.this);
					builder.setTitle("Delete "
							+ ((TextView) view.findViewById(R.id.job))
									.getText().toString() + "?");
					builder.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}

							});
					builder.setPositiveButton("Delete",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									{
										AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
										Intent intent = new Intent(
												MainActivity.this,
												AlarmService.class);
										intent.putExtra("job_id", id);
										PendingIntent pi = PendingIntent
												.getService(MainActivity.this,
														Integer.parseInt(id),
														intent, 0);
										am.cancel(pi);
									}
									{
										AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
										Intent intent = new Intent(
												MainActivity.this,
												NotificationReceiver.class);
										intent.putExtra("job_id", id);
										PendingIntent pi = PendingIntent
												.getBroadcast(
														MainActivity.this,
														Integer.parseInt(id),
														intent, 0);
										am.cancel(pi);
									}

									try {
										info.open();
										info.ourDatabase
												.delete(info.DATABASE_TABLE,
														info.KEY_ROWID + "="
																+ id, null);
										info.close();
									} catch (SQLException e) {
										Toast.makeText(MainActivity.this,
												"Error", Toast.LENGTH_SHORT)
												.show();
									} finally {
										populate(Selection.type);
									}
									Toast.makeText(MainActivity.this,
											"Task deleted", Toast.LENGTH_SHORT)
											.show();
								}
							});
					AlertDialog alertDialog = builder.create();
					alertDialog.show();
				}
			});
			edit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					AlertDialog.Builder builder = new AlertDialog.Builder(
							MainActivity.this);
					LayoutInflater inflater = MainActivity.this
							.getLayoutInflater();
					builder.setView(inflater.inflate(R.layout.add_dialog, null));
					final AlertDialog ad = builder.create();
					ad.setTitle("Edit Task");

					ad.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
					ad.setButton(AlertDialog.BUTTON_POSITIVE, "Update",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									boolean diditwork = true;
									EditText addJobView = (EditText) ad
											.findViewById(R.id.addJob);
									String jobName = addJobView.getText()
											.toString();
									DatePicker dp = (DatePicker) ad
											.findViewById(R.id.addDueDate);
									int day = dp.getDayOfMonth();
									int month = dp.getMonth() + 1;
									int year = dp.getYear();
									String dueDate = "" + year + "-"
											+ df.format(month) + "-"
											+ df.format(day);
									Spinner urg = (Spinner) ad
											.findViewById(R.id.urgencyList);
									int urgval = urg.getSelectedItemPosition();
									try {
										info.open();
										ContentValues cv = new ContentValues();
										cv.put(info.JOB_NAME, jobName);
										cv.put(info.DUE_DATE, dueDate);
										cv.put(info.IS_DONE, 0);
										cv.put(info.URGENCY, urgval);
										String where = info.KEY_ROWID
												+ " LIKE ?";
										String[] arg = { id };
										info.ourDatabase.update(
												info.DATABASE_TABLE, cv, where,
												arg);
										info.close();
									} catch (SQLException e) {
										diditwork = false;
										e.printStackTrace();
									} finally {
										if (diditwork) {
											Toast.makeText(MainActivity.this,
													"Task Updated",
													Toast.LENGTH_SHORT).show();
											populate(Selection.type);
										} else
											Toast.makeText(MainActivity.this,
													"Update Error",
													Toast.LENGTH_SHORT).show();
										ad.dismiss();
									}

									if (is_notification == 1) {
										{
											AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
											Intent intent = new Intent(
													MainActivity.this,
													NotificationReceiver.class);
											intent.putExtra("job_id", id);
											PendingIntent pi = PendingIntent
													.getBroadcast(
															MainActivity.this,
															Integer.parseInt(id),
															intent, 0);
											am.cancel(pi);
										}

										String[] time = ((TextView) view
												.findViewById(R.id.time))
												.getText().toString()
												.split(" : ");
										int hour = Integer.parseInt(time[0]);
										int minute = Integer.parseInt(time[1]);

										AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
										Intent iNotification = new Intent(
												context,
												NotificationReceiver.class);
										iNotification.putExtra("job_id", id);
										PendingIntent pi = PendingIntent
												.getBroadcast(context,
														Integer.parseInt(id),
														iNotification, 0);
										Calendar calendar = Calendar
												.getInstance();
										calendar.setTimeInMillis(System
												.currentTimeMillis());
										calendar.set(year, month - 1, day,
												hour, minute, 0);
										am.set(AlarmManager.RTC_WAKEUP,
												calendar.getTimeInMillis(), pi);
										Toast.makeText(MainActivity.this,
												"Notification Reset",
												Toast.LENGTH_SHORT).show();

									} else if (is_alarm == 1) {

										{
											AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
											Intent intent = new Intent(
													MainActivity.this,
													AlarmService.class);
											intent.putExtra("job_id", id);
											PendingIntent pi = PendingIntent
													.getService(
															MainActivity.this,
															Integer.parseInt(id),
															intent, 0);
											am.cancel(pi);
										}

										String[] time = ((TextView) view
												.findViewById(R.id.time))
												.getText().toString()
												.split(" : ");
										int hour = Integer.parseInt(time[0]);
										int minute = Integer.parseInt(time[1]);

										AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
										Intent iNotification = new Intent(
												context, AlarmService.class);
										iNotification.putExtra("job_id", id);
										PendingIntent pi = PendingIntent
												.getService(context,
														Integer.parseInt(id),
														iNotification, 0);
										Calendar calendar = Calendar
												.getInstance();
										calendar.setTimeInMillis(System
												.currentTimeMillis());
										calendar.set(year, month - 1, day,
												hour, minute, 0);
										am.set(AlarmManager.RTC_WAKEUP,
												calendar.getTimeInMillis(), pi);
										Toast.makeText(MainActivity.this,
												"Alarm Reset",
												Toast.LENGTH_SHORT).show();

									}
								}
							});
					ad.show();
					EditText addJobView = (EditText) ad
							.findViewById(R.id.addJob);
					addJobView.setText(jobView.getText().toString());
					((Spinner) ad.findViewById(R.id.urgencyList))
							.setSelection(urgency);
					DatePicker dp = (DatePicker) ad
							.findViewById(R.id.addDueDate);
					String[] date = dueDateView.getText().toString().split("-");
					dp.init(Integer.parseInt(date[0]),
							Integer.parseInt(date[1]) - 1,
							Integer.parseInt(date[2]),
							new OnDateChangedListener() {
								@Override
								public void onDateChanged(DatePicker dp_get,
										int year_get, int month_get, int day_get) {
								}

							});
				}
			});
		}
	}

	public class NotifyReceiverMain extends BroadcastReceiver {
		public static final String ACTION_NOTIFICATION = "ACTION_NOTIFICATION";

		@Override
		public void onReceive(Context context, Intent intent) {
			{
				String id = intent.getStringExtra("id");
				NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.cancel(Integer.parseInt(id));
			}
			populate(Selection.type);
		}
	}

	public class AlarmReceiverMain extends BroadcastReceiver {
		public static final String ACTION_ALARM = "ACTION_ALARM";

		@Override
		public void onReceive(final Context context, Intent intent) {
			{
				final String id = intent.getStringExtra("id");

				NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.cancel(Integer.parseInt(id));

				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
				builder.setCancelable(false);
				builder.setTitle("Alarm");
				String[] options = { "Stop", "Snooze" };
				builder.setItems(options,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case 0:
									Intent stopIntent1 = new Intent(context,
											AlarmService.class);
									stopIntent1.putExtra("job_id", id);
									context.stopService(stopIntent1);

									try {
										info.open();
										ContentValues cv = new ContentValues();
										cv.put(info.IS_NOTIFICATION, 0);
										cv.put(info.IS_ALARM, 0);
										cv.put(info.IS_DONE, 1);
										String where = info.KEY_ROWID
												+ " LIKE ?";
										String[] arg = { id };
										info.ourDatabase.update(
												info.DATABASE_TABLE, cv, where,
												arg);
										info.close();
									} catch (SQLException e) {
										e.printStackTrace();
									}
									populate(Selection.type);
									break;
								case 1:
									Intent stopIntent2 = new Intent(context,
											AlarmService.class);
									stopIntent2.putExtra("job_id", id);
									context.stopService(stopIntent2);

									final AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
									Intent intent = new Intent(
											MainActivity.this,
											AlarmService.class);
									intent.putExtra("job_id", id);
									final PendingIntent pi = PendingIntent
											.getService(MainActivity.this,
													Integer.parseInt(id),
													intent, 0);
									final Calendar calendar = Calendar
											.getInstance();
									calendar.setTimeInMillis(System
											.currentTimeMillis());

									AlertDialog.Builder snoBuilder = new AlertDialog.Builder(
											MainActivity.this);
									snoBuilder.setCancelable(false);
									snoBuilder.setTitle("Snooze By");
									String[] options = { "5 Min", "10 Min",
											"15 Min", "30 Min", "1 Hour" };
									snoBuilder
											.setItems(
													options,
													new DialogInterface.OnClickListener() {

														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															switch (which) {
															case 0:
																calendar.add(
																		Calendar.MINUTE,
																		5);
																am.set(AlarmManager.RTC_WAKEUP,
																		calendar.getTimeInMillis(),
																		pi);
																break;
															case 1:
																calendar.add(
																		Calendar.MINUTE,
																		10);
																am.set(AlarmManager.RTC_WAKEUP,
																		calendar.getTimeInMillis(),
																		pi);
																break;
															case 2:
																calendar.add(
																		Calendar.MINUTE,
																		15);
																am.set(AlarmManager.RTC_WAKEUP,
																		calendar.getTimeInMillis(),
																		pi);
																break;
															case 3:
																calendar.add(
																		Calendar.MINUTE,
																		30);
																am.set(AlarmManager.RTC_WAKEUP,
																		calendar.getTimeInMillis(),
																		pi);
																break;
															case 4:
																calendar.add(
																		Calendar.MINUTE,
																		60);
																am.set(AlarmManager.RTC_WAKEUP,
																		calendar.getTimeInMillis(),
																		pi);
																break;
															}
														}
													});
									snoBuilder.show();
									break;
								}
							}
						});
				AlertDialog alertDialog = builder.create();
				alertDialog.show();
				intent.removeExtra("id");
				getIntent().removeExtra("Alarm");
			}
			populate(Selection.type);
		}
	}
}
