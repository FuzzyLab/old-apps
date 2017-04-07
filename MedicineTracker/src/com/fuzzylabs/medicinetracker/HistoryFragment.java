package com.fuzzylabs.medicinetracker;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ActionBar.OnNavigationListener;
import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class HistoryFragment extends Fragment implements OnNavigationListener {
	
	private ArrayAdapter<String> sAdapter = null;
	Cursor cursor = null;
	private Spinner historyMediSpinnerView = null;
	private Button addHistoryBtn = null;
	private MedicineDB mediInfo;
	private String mediId = "";
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static HistoryFragment historyFragment = null;
	final DecimalFormat df = new DecimalFormat("00");
	
	public static Fragment newInstance(int sectionNumber) {
		if(historyFragment == null)
			historyFragment = new HistoryFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		historyFragment.setArguments(args);
		return historyFragment;
	}

	public HistoryFragment() {
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		
		mediInfo = new MedicineDB(getActivity().getApplicationContext());
		
		historyMediSpinnerView = (Spinner) getActivity().findViewById(R.id.historyMediSpinnerView);
		historyMediSpinnerView.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	if(position >= 0) {
		    		String mediName = parentView.getSelectedItem().toString();
		    		mediId = Medicine.getMediId(getActivity().getApplicationContext(), mediName, Members.historySelected);
		    		populate(mediId);
		    	}
		    }
		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		    }
		});
		

		addHistoryBtn = (Button) getActivity().findViewById(R.id.addHistoryBtn);
		addHistoryBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(historyMediSpinnerView.getSelectedItemPosition() >= 0) {
					AlertDialog.Builder nbuilder = new AlertDialog.Builder(getActivity());
					LayoutInflater ninflater = getActivity().getLayoutInflater();
					nbuilder.setView(ninflater.inflate(R.layout.date_time_picker, null));
					final AlertDialog nad = nbuilder.create();
					nad.setTitle(historyMediSpinnerView.getSelectedItem().toString());
					nad.setButton(
							AlertDialog.BUTTON_NEGATIVE,
							"Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialog,
										int which) {
								}
							});
					nad.setButton(
							AlertDialog.BUTTON_POSITIVE,
							"Set",
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
											nad.dismiss();
										}
	
									});
					nad.getButton(
							AlertDialog.BUTTON_POSITIVE)
							.setOnClickListener(
									new OnClickListener() {
										@Override
										public void onClick(
												View v) {
											DatePicker dp = (DatePicker) nad.findViewById(R.id.historyDatePicker);
											TimePicker tp = (TimePicker) nad.findViewById(R.id.historyTimePicker);
											int year = dp.getYear();
											int month = dp.getMonth() + 1;
											int day = dp.getDayOfMonth();
											int hour = tp.getCurrentHour();
											int minute = tp.getCurrentMinute();
											
											String missedDate = ""+year+"-"+df.format(month)+"-"+df.format(day);
											String missedTime = ""+df.format(hour)+":"+df.format(minute)+":00";
											
											try {
												mediInfo.open();
												mediInfo.createMissedEntry(mediId, missedDate, missedTime);
												mediInfo.close();
											} catch (SQLException e) {
												e.printStackTrace();
											}
											nad.dismiss();
											populate(mediId);
										}
									});
				} else {
					Toast.makeText(getActivity(),
							"No Medicine Selected", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
				
		setDropDown(Members.historySelected);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_history, container, false);
		return rootView;
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		Members.historySelected = Members.membersList.get(position);
		setDropDown(Members.historySelected);
		return false;
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		getActivity().getMenuInflater().inflate(R.menu.history, menu);
		super.onCreateOptionsMenu(menu, inflater);
		
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		sAdapter = new ArrayAdapter<String>(actionBar.getThemedContext(),
				android.R.layout.simple_spinner_item, android.R.id.text1,
				Members.membersList);
		sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		actionBar.setListNavigationCallbacks(sAdapter, this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.deleteAllHistory:
			AlertDialog.Builder builder = new AlertDialog.Builder(
					getActivity());
			builder.setTitle("Delete History?");
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
							if(!mediId.equals("")) {
								try {
									mediInfo.open();
									mediInfo.ourDatabase.delete(
										MedicineDB.MISSED_DOSE_TABLE,
										MedicineDB.MISSED_MEDI_ID + "="
												+ mediId, null);
									mediInfo.close();
								} catch (SQLException e) {
									Toast.makeText(getActivity(),
										"Error", Toast.LENGTH_SHORT)
										.show();
								} finally {
									populate(mediId);
								}
							}
						}
					});
			AlertDialog alertDialog = builder.create();
			alertDialog.show();
			return true;
		case R.id.rate:
			String url = "https://play.google.com/store/apps/details?id=com.fuzzylabs.medicinetracker";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			return true;
		case R.id.share:
			String thisApp = "https://play.google.com/store/apps/details?id=com.fuzzylabs.medicinetracker";

			String body = "---Medicine Tracker---\n --Fuzzy Labs--"
					+ "\n\n Download this app: " + thisApp;

			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, body);
			sendIntent.setType("text/plain");
			startActivity(Intent.createChooser(sendIntent,
					"Fuzzy Labs | Medicine Tracker"));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void setDropDown(String member) {
		ArrayList<String> mediList = Medicine.getMediListForMember(getActivity().getApplicationContext(), member);
		ArrayAdapter<String> mediAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, mediList);
		mediAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		historyMediSpinnerView.setAdapter(mediAdapter);
		if(!mediAdapter.isEmpty()) {
			String mediName = historyMediSpinnerView.getSelectedItem().toString();
			mediId = Medicine.getMediId(getActivity().getApplicationContext(), mediName, member);
			populate(mediId);
		} else {
			ListView historyList = (ListView) getActivity().findViewById(R.id.historyDoseListView);
			historyList.setAdapter(null);
		}
	}
	
	private void populate(String mediId) {
		try {
			mediInfo.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		cursor = mediInfo.getAllDosesMissed(MedicineDB.MISSED_MEDI_ID + " LIKE ? ",
				new String[] { mediId });

		String[] fromColumns = { MedicineDB.MISSED_MEDI_DATE, MedicineDB.MISSED_MEDI_TIME };
		int[] toViews = { R.id.historyDateView, R.id.historyTimeView };
		SimpleCursorAdapter mAdapter = new Custom_Adapter(getActivity(), R.layout.history_item,
				cursor, fromColumns, toViews);
		ListView historyList = (ListView) getActivity().findViewById(R.id.historyDoseListView);
		historyList.setAdapter(mAdapter);
		
		mediInfo.close();
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
			Button deleteHistory = (Button) view.findViewById(R.id.deleteHistory);
			deleteHistory.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getActivity());
					builder.setTitle("Delete history?");
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
									try {
										mediInfo.open();
										mediInfo.ourDatabase.delete(
												MedicineDB.MISSED_DOSE_TABLE,
												MedicineDB.KEY_ROWID + "="
														+ id, null);
										mediInfo.close();
									} catch (SQLException e) {
										Toast.makeText(getActivity(),
												"Error", Toast.LENGTH_SHORT)
												.show();
									} finally {
										populate(mediId);
									}
								}
							});
					AlertDialog alertDialog = builder.create();
					alertDialog.show();
				}
			});
		}
	}
}