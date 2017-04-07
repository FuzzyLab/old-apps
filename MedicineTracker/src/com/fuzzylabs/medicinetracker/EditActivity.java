package com.fuzzylabs.medicinetracker;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.Spinner;
import android.widget.TextView;

public class EditActivity extends Activity {

	MedicineDB mediInfo = new MedicineDB(this);
	DoseDB dosesInfo = new DoseDB(this);
	Cursor cursor = null;
	
	private static String mediId;
	final DecimalFormat df = new DecimalFormat("00");
	private ToggleButton notificationState = null;
	private Spinner memberListView = null;
	private boolean permission = false;
	private boolean exists = false;
	private AutoCompleteTextView mediNameView;
	private EditText doseAmountView;
	private Spinner doseTypeListView;

	String[] mediNames;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		notificationState = (ToggleButton) findViewById(R.id.notificationState);
		mediNameView = (AutoCompleteTextView) findViewById(R.id.mediNameView);
		doseAmountView = (EditText) findViewById(R.id.doseAmountView);
		doseTypeListView = (Spinner) findViewById(R.id.doseTypeView);

		mediNames = getResources().getStringArray(R.array.medicineslist);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, mediNames);
		mediNameView.setThreshold(1);
		mediNameView.setAdapter(adapter);

		Intent i = getIntent();
		mediId = i.getStringExtra("mediid");

		try {
			mediInfo.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		cursor = mediInfo.getAllRows(
				MedicineDB.KEY_ROWID + " = " + Integer.parseInt(mediId), null);
		cursor.moveToFirst();

		boolean state = false;
		if (cursor.getString(7).equals("1"))
			state = true;

		mediNameView.setText(cursor.getString(1));
		((EditText) findViewById(R.id.intervalView)).setText(cursor
				.getString(2));
		((Button) findViewById(R.id.startDateView))
				.setText(cursor.getString(4));
		memberListView = (Spinner) findViewById(R.id.membersView);
		ArrayAdapter<String> spinnerMenuList = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, Members.membersList);
		spinnerMenuList
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		memberListView.setAdapter(spinnerMenuList);
		String forWho = cursor.getString(6);
		memberListView.setSelection(Members.membersList.indexOf(forWho));
		
		doseAmountView.setText(cursor.getString(8));
		doseTypeListView.setSelection(cursor.getInt(9));

		notificationState.setChecked(state);
		mediInfo.close();
		populateList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.done:
			onBackPressed();
			return true;
		case R.id.notes:
			AlertDialog.Builder nbuilder = new AlertDialog.Builder(EditActivity.this);
			LayoutInflater ninflater = getLayoutInflater();
			nbuilder.setView(ninflater.inflate(R.layout.notes_dialog, null));
			final AlertDialog nad = nbuilder.create();
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
			nad.getWindow().setLayout(700, 800);
			
			nad.getButton(
					AlertDialog.BUTTON_NEGATIVE)
					.setOnClickListener(
							new OnClickListener() {
								@Override
								public void onClick(
										View v) {
									try {
										mediInfo.open();
										ContentValues cv = new ContentValues();
										cv.put(MedicineDB.NOTES,
												"");
										String where = MedicineDB.KEY_ROWID
												+ " LIKE ?";
										String[] arg = { mediId };
										mediInfo.ourDatabase
												.update(MedicineDB.MEDICINE_TABLE,
														cv,
														where,
														arg);
										mediInfo.close();

									} catch (SQLException e) {
										e.printStackTrace();
									}

									((TextView) nad.findViewById(R.id.notesView)).setText("");
									Toast.makeText(
											getApplicationContext(),
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
				mediInfo.open();
				String[] columns = { mediInfo.NOTES };
				String[] arg = { mediId };
				Cursor curse = mediInfo.ourDatabase
						.query(MedicineDB.MEDICINE_TABLE,
								columns,
								mediInfo.KEY_ROWID
										+ " like ?",
								arg, null, null,
								null);
				curse.moveToFirst();
				prenotes = curse.getString(0);
				mediInfo.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}

			((TextView) nad.findViewById(R.id.notesView)).setText(prenotes);

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
							mediInfo.open();
							String[] columns = { MedicineDB.NOTES };
							String[] arg = { mediId };
							Cursor curse = mediInfo.ourDatabase
									.query(MedicineDB.MEDICINE_TABLE,
											columns,
											MedicineDB.KEY_ROWID
													+ " like ?",arg,null,null,null);
							curse.moveToFirst();
							prenotes = curse
									.getString(0);
							mediInfo.close();

						} catch (SQLException e) {
							e.printStackTrace();
						}

						String newnotes = prenotes
								+ newtext + "\n";
						((TextView) nad
								.findViewById(R.id.notesView))
								.setText(newnotes);

						try {
							mediInfo.open();
							ContentValues cv = new ContentValues();
							cv.put(MedicineDB.NOTES,
									newnotes);
							String where = MedicineDB.KEY_ROWID + " LIKE ?";
							String[] arg = { mediId };
							mediInfo.ourDatabase.update(MedicineDB.MEDICINE_TABLE, cv, where, arg);
							mediInfo.close();

						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					((EditText) nad.findViewById(R.id.newNote)).setText("");
				}
			});
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		okBtn();
		if (permission) {
			if (exists) {
				Toast.makeText(getApplicationContext(),
						"Medicine with this name already exists for this member",
						Toast.LENGTH_SHORT).show();
				exists = false;
				permission = false;
			} else {
				EditActivity.super.onBackPressed();
			}
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					EditActivity.this);

			builder.setTitle("Medicine Name cannot be blank. SET name / DELETE medicine?");
			builder.setNegativeButton("Set",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}

					});
			builder.setPositiveButton("Delete",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Medicine.deleteMedicine(getApplicationContext(),
									mediId);
							dialog.dismiss();
							EditActivity.super.onBackPressed();
						}
					});
			AlertDialog alertDialog = builder.create();
			alertDialog.show();
		}
	}

	public void populateList() {
		try {
			dosesInfo.open();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		cursor = dosesInfo.getAllRows(DoseDB.MEDI_ID + " like ?",
				new String[] { mediId });
		if (cursor.getCount() == 0) {
			dosesInfo.createEntry(mediId, "hh:mm:ss", 0, 0);
			dosesInfo.close();
			populateList();
		} else {
			String[] fromColumns = { DoseDB.DOSE };
			int[] toViews = { R.id.doseView };
			SimpleCursorAdapter mAdapter;
			mAdapter = new Custom_Adapter(EditActivity.this,
					R.layout.dose_item, cursor, fromColumns, toViews);
			ListView myList = (ListView) findViewById(R.id.dosesListView);
			myList.setAdapter(mAdapter);
			dosesInfo.close();
		}
	}

	public void okBtn() {
		String intervalText = ((EditText) findViewById(R.id.intervalView))
				.getText().toString();
		String mediNameText = mediNameView.getText().toString();
		int doseAmount = Integer.parseInt(doseAmountView.getText().toString());
		int doseUnit = doseTypeListView.getSelectedItemPosition();
		if (!mediNameText.equals("")) {
			permission = true;
			if (intervalText.equals("")) {
				intervalText = "0";
			}
			Integer interval = Integer.parseInt(intervalText);
			String startDate = ((Button) findViewById(R.id.startDateView))
					.getText().toString();
			try {
				mediInfo.open();
				ContentValues cv = new ContentValues();
				cv.put(MedicineDB.INTERVAL, interval);
				cv.put(MedicineDB.MEDI_NAME, mediNameText);
				cv.put(MedicineDB.FOR_WHO,
						(String) memberListView.getSelectedItem());
				cv.put(MedicineDB.DOSE_AMOUNT, doseAmount);
				cv.put(MedicineDB.DOSE_UNIT, doseUnit);
				String where = MedicineDB.KEY_ROWID + " LIKE ?";
				String[] arg = { mediId };
				int rows = mediInfo.ourDatabase.update(MedicineDB.MEDICINE_TABLE,
						cv, where, arg);
				if (rows == 0)
					exists = true;
				mediInfo.close();
			} catch (SQLException e) {
				Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT)
						.show();
			}

			try {
				dosesInfo.open();
				String whereDelete = DoseDB.IS_SET + " = " + 0;
				dosesInfo.ourDatabase.delete(DoseDB.DOSE_TABLE,
						whereDelete, null);

				ContentValues cv = new ContentValues();
				cv.put(DoseDB.IS_DONE, 0);
				String whereUpdate = DoseDB.MEDI_ID + " LIKE ?";
				String[] arg = { mediId };
				dosesInfo.ourDatabase.update(DoseDB.DOSE_TABLE, cv,
						whereUpdate, arg);
				dosesInfo.close();
			} catch (SQLException e) {
				Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT)
						.show();
			}

			try {
				dosesInfo.open();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			cursor = dosesInfo.getAllRows(DoseDB.MEDI_ID + " LIKE ? ",
					new String[] { mediId });
			int isDose = cursor.getCount();
			while (cursor.moveToNext()) {
				String id = cursor.getString(0);
				AlarmManager am = (AlarmManager) getApplicationContext()
						.getSystemService(Context.ALARM_SERVICE);
				Intent intent = new Intent(getApplicationContext(),
						NotificationReceiver.class);
				intent.putExtra("dose_id", id);
				intent.putExtra("medi_id", mediId);
				PendingIntent pi = PendingIntent.getBroadcast(
						getApplicationContext(), Integer.parseInt(id), intent,
						0);
				am.cancel(pi);
			}
			dosesInfo.close();

			if (interval == 0 | startDate.equals("YYYY-MM-DD") | isDose == 0
					| !notificationState.isChecked()) {

				try {
					mediInfo.open();
					ContentValues cv = new ContentValues();
					cv.put(MedicineDB.DUE_DATE, "YYYY-MM-DD");
					cv.put(MedicineDB.DUE_TIME, "hh:mm:ss");
					cv.put(MedicineDB.IS_NOTIFICATION, 0);
					String where = MedicineDB.KEY_ROWID + " LIKE ?";
					String[] arg = { mediId };
					mediInfo.ourDatabase.update(MedicineDB.MEDICINE_TABLE, cv,
							where, arg);
					mediInfo.close();
				} catch (SQLException e) {
					Toast.makeText(getApplicationContext(), "Error",
							Toast.LENGTH_SHORT).show();
				}
				if (!exists)
					Toast.makeText(getApplicationContext(),
							"Notification Not Set:\n1.Interval should be > 0.\n2.Start date should be set.\n3.Atleast 1 dose must be set.\n4.Notification should be ON.",
							Toast.LENGTH_LONG).show();
			} else {

				try {
					mediInfo.open();
					ContentValues cv = new ContentValues();
					cv.put(MedicineDB.IS_NOTIFICATION, 1);
					cv.put(MedicineDB.DUE_DATE, "Computing..");
					cv.put(MedicineDB.DUE_TIME, "Computing..");
					String where = MedicineDB.KEY_ROWID + " LIKE ?";
					String[] arg = { mediId };
					mediInfo.ourDatabase.update(MedicineDB.MEDICINE_TABLE, cv,
							where, arg);
					mediInfo.close();
				} catch (SQLException e) {
					Toast.makeText(getApplicationContext(), "Error",
							Toast.LENGTH_SHORT).show();
				}

				(new Thread() {
					@Override
					public void run() {
						(new NotificationSetter()).setNotification(
								getApplicationContext(), mediId, 0);
					}
				}).start();

				if (!exists)
					Toast.makeText(getApplicationContext(), "Notification Set.",
							Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(getApplicationContext(), "Medicine Name must be set",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void setStartDate(View view) {
		
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH);
		int year = calendar.get(Calendar.YEAR);
		
		final DatePickerDialog dPicker = new DatePickerDialog(
		        this,
		        null,
		        year, month, day);
		dPicker.setCancelable(true);
		dPicker.setCanceledOnTouchOutside(true);
		dPicker.setButton(DialogInterface.BUTTON_POSITIVE, "Done",
		        new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		            	int year = dPicker.getDatePicker().getYear();
		            	int month = dPicker.getDatePicker().getMonth();
		            	int date = dPicker.getDatePicker().getDayOfMonth();
		            	final String startDate = "" + year + "-"
								+ df.format(month + 1) + "-" + df.format(date);

						((Button) findViewById(R.id.startDateView))
								.setText(startDate);
						try {
							mediInfo.open();
							ContentValues cv = new ContentValues();
							cv.put(MedicineDB.START_DATE, startDate);
							String where = MedicineDB.KEY_ROWID + " LIKE ?";
							String[] arg = { mediId };
							mediInfo.ourDatabase.update(
									MedicineDB.MEDICINE_TABLE, cv, where, arg);
							mediInfo.close();
						} catch (SQLException e) {
							Toast.makeText(getApplicationContext(), "Error",
									Toast.LENGTH_SHORT).show();
						}
		            }
		        });
		dPicker.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", 
		        new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		            	dPicker.dismiss();
		            }
		        });
		dPicker.show();
	}

	private class Custom_Adapter extends SimpleCursorAdapter {

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
			final String id = cursor.getString(0);
			Button addDose = (Button) view.findViewById(R.id.addDoseView);
			Button deleteDose = (Button) view.findViewById(R.id.deleteDoseView);
			final Button timeDose = (Button) view.findViewById(R.id.doseView);
			addDose.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						dosesInfo.open();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					dosesInfo.createEntry(mediId, "hh:mm:ss", 0, 0);
					dosesInfo.close();
					populateList();
				}
			});

			deleteDose.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AlarmManager am = (AlarmManager) context
							.getSystemService(Context.ALARM_SERVICE);
					Intent intent = new Intent(context,
							NotificationReceiver.class);
					intent.putExtra("dose_id", id);
					intent.putExtra("medi_id", mediId);
					PendingIntent pi = PendingIntent.getBroadcast(context,
							Integer.parseInt(id), intent, 0);
					am.cancel(pi);

					try {
						dosesInfo.open();
						dosesInfo.ourDatabase.delete(DoseDB.DOSE_TABLE,
								DoseDB.KEY_ROWID + "=" + id, null);
					} catch (SQLException e) {
						Toast.makeText(getApplicationContext(), "Error",
								Toast.LENGTH_SHORT).show();
					} finally {
						dosesInfo.close();
						populateList();
					}
				}
			});

			timeDose.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					
					int setHour = 9;
					int setMinute = 0;
					String doseText = timeDose.getText().toString();
					if(!doseText.equals("hh:mm:ss")) {
						String[] dose = doseText.split(":");
						setHour = Integer.parseInt(dose[0]);
						setMinute = Integer.parseInt(dose[1]);
					}
					
					final TimePicker tPicker = new TimePicker(EditActivity.this);
				    tPicker.setIs24HourView(true);
				    tPicker.setCurrentHour(setHour);
				    tPicker.setCurrentMinute(setMinute);
				    tPicker.setIs24HourView(false);

				    new AlertDialog.Builder(EditActivity.this)
				            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

				                @Override
				                public void onClick(DialogInterface dialog, int which) {
				                	int hour = tPicker.getCurrentHour();
				                	int minute = tPicker.getCurrentMinute();
				                	String dueTime = "" + df.format(hour) + ":"
											+ df.format(minute) + ":00";

									try {
										dosesInfo.open();
										ContentValues cv = new ContentValues();
										cv.put(DoseDB.DOSE, dueTime);
										cv.put(DoseDB.IS_SET, 1);
										String where = DoseDB.KEY_ROWID
												+ " LIKE ?";
										String[] arg = { id };
										int rows = dosesInfo.ourDatabase
												.update(DoseDB.DOSE_TABLE,
														cv, where, arg);
										dosesInfo.close();
										if (rows == 0) {
											Toast.makeText(getApplicationContext(),
													"Time already added",
													Toast.LENGTH_SHORT).show();
										}
									} catch (SQLException e) {
										Toast.makeText(getApplicationContext(),
												"Error", Toast.LENGTH_SHORT)
												.show();
									} finally {
										populateList();
									}
				                }
				            })
				            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

				                @Override
				                public void onClick(DialogInterface dialog, int which) {
				                	dialog.dismiss();
				                }
				            })
				            .setView(tPicker)
				            .show();
				}
			});
		}
	}

}
