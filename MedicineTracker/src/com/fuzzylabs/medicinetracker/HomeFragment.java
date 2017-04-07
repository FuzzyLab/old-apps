package com.fuzzylabs.medicinetracker;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Calendar;

import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ActionBar.OnNavigationListener;
import android.support.v4.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class HomeFragment extends Fragment implements OnNavigationListener {

	MedicineDB mediInfo = null;
	DoseDB dosesInfo = null;
	Cursor cursor = null;
	final DecimalFormat df = new DecimalFormat("00");
	private NotifyReceiver receiver;
	private ArrayAdapter<String> sAdapter = null;
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static HomeFragment homeFragment = null;
	
	public static Fragment newInstance(int sectionNumber) {
		if(homeFragment == null)
			homeFragment = new HomeFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		homeFragment.setArguments(args);
		return homeFragment;
	}

	private HomeFragment() {
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		
		mediInfo = new MedicineDB(getActivity().getApplicationContext());
		dosesInfo = new DoseDB(getActivity().getApplicationContext());
		
		Members.fetchList(getActivity().getApplicationContext());
		
		try {
			mediInfo.open();
			cursor = mediInfo.getAllRows(MedicineDB.MEDI_NAME+" Like ?", new String[]{""});
			while(cursor.moveToNext()){
				String mediId = cursor.getString(0);
				Medicine.deleteBlankMedicine(getActivity().getApplicationContext(), mediId);
			}
			mediInfo.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		ListView list = (ListView) getActivity().findViewById(R.id.mediListView);

		receiver = new NotifyReceiver();

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {

				TextView mediView = (TextView) view.findViewById(R.id.mediView);
				String mediName = (String) mediView.getText().toString();

				Intent myIntent = new Intent(getActivity(),
						EditActivity.class);
				myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				myIntent.putExtra("mediid", Medicine.getMediId(
						getActivity().getApplicationContext(), mediName, Members.mainSelected));
				startActivity(myIntent);
			}
		});
		populateList(Members.mainSelected);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_home, container, false);
		return rootView;
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		Members.mainSelected = Members.membersList.get(position);
		populateList(Members.mainSelected);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		populateList(Members.mainSelected);

		IntentFilter filter = new IntentFilter(NotifyReceiver.ACTION_RESP);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		receiver = new NotifyReceiver();
		getActivity().registerReceiver(receiver, filter);	
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		getActivity().getMenuInflater().inflate(R.menu.home, menu);
		super.onCreateOptionsMenu(menu, inflater);
		
		final ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		sAdapter = new ArrayAdapter<String>(actionBar.getThemedContext(),
				android.R.layout.simple_spinner_item, android.R.id.text1,
				Members.membersList);
		sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		actionBar.setListNavigationCallbacks(sAdapter, this);
	}

	public void populateList(String member) {
		try {
			mediInfo.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		cursor = mediInfo.getAllRows(MedicineDB.FOR_WHO + " LIKE ? ",
				new String[] { member });

		String[] fromColumns = { MedicineDB.MEDI_NAME, MedicineDB.DUE_DATE,
				MedicineDB.DUE_TIME };
		int[] toViews = { R.id.mediView, R.id.dateView, R.id.timeView };
		SimpleCursorAdapter mAdapter;
		mAdapter = new Custom_Adapter(getActivity().getApplicationContext(), R.layout.main_item,
				cursor, fromColumns, toViews);
		ListView myList = (ListView) getActivity().findViewById(R.id.mediListView);
		myList.setAdapter(mAdapter);

		mediInfo.close();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add:
			boolean alreadyAdded = false;
			try {
				mediInfo.open();
				int countBefore = mediInfo.getCount();
				mediInfo.createEntry("", 1, "YYYY-MM-DD", "YYYY-MM-DD",
						"hh:mm:ss", Members.mainSelected, 1, 1, 0, "");
				int countAfter = mediInfo.getCount();
				if (countBefore == countAfter)
					alreadyAdded = true;
			} catch (SQLException e) {
			} finally {
				if (alreadyAdded) {
					Toast.makeText(
							getActivity().getApplicationContext(),
							"A Medicine with blank MedicineName already exists. Cannot add another.",
							Toast.LENGTH_SHORT).show();
				} else
					Toast.makeText(getActivity(), "A blank Medicine Added",
							Toast.LENGTH_SHORT).show();
				if (!alreadyAdded) {
					String mediId = Medicine.getMediId(getActivity().getApplicationContext(),
							"", Members.mainSelected);
					Intent myIntent = new Intent(getActivity(),
							EditActivity.class);
					myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					myIntent.putExtra("mediid", mediId);
					startActivity(myIntent);
				}
			}
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
			final String mediId = cursor.getString(0);
			final String mediName = cursor.getString(1);
			TextView snoozeTimeView = (TextView) view.findViewById(R.id.snoozeTimeView);
			
			DoseDB doseDB = new DoseDB(context);
			try {
				doseDB.open();
				Cursor curs = doseDB.getAllRows(DoseDB.SNOOZE_TIME + " != " + "'' and " + DoseDB.MEDI_ID + " = " + mediId, null);
				String snoozeTime = "";
				if(curs.getCount() > 0) {
					curs.moveToNext();
					snoozeTime = curs.getString(6);
				}
				snoozeTimeView.setText(snoozeTime);
				doseDB.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			Button deleteMedi = (Button) view.findViewById(R.id.deleteMedi);
			deleteMedi.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getActivity());
					builder.setTitle("Delete " + mediName + "?");
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
									Medicine.deleteMedicine(context, mediId);
									populateList(Members.mainSelected);
								}
							});
					AlertDialog alertDialog = builder.create();
					alertDialog.show();
				}
			});
			RelativeLayout mainArea = (RelativeLayout) view.findViewById(R.id.mainArea);
			mainArea.setBackgroundColor(Color.WHITE);
			mainArea.invalidate();
			
			String dueDate = cursor.getString(3);
			String dueTime = cursor.getString(5);
			if(!(dueTime.equals("hh:mm:ss") | dueTime.equals("Computing.."))) {
				
				String[] date = dueDate.split("-");
				String[] time = dueTime.split(":");
				
				int day = Integer.parseInt(date[2]);
				int month = Integer.parseInt(date[1]) - 1;
				int year = Integer.parseInt(date[0]);
				
				int hourOfDay = Integer.parseInt(time[0]);
				int minute = Integer.parseInt(time[1]);
				
				Calendar now = Calendar.getInstance();
				now.setTimeInMillis(System.currentTimeMillis());
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(System.currentTimeMillis());
				calendar.set(year, month, day, hourOfDay, minute, 0);
				calendar.add(Calendar.HOUR_OF_DAY, -2);

				if(now.compareTo(calendar) >= 0) {
					mainArea.setBackgroundColor(Color.parseColor("#F5DAE5"));
					mainArea.invalidate();
				}
				else {
					mainArea.setBackgroundColor(Color.WHITE);
					mainArea.invalidate();
				}
				
			}
		}
	}

	public class NotifyReceiver extends BroadcastReceiver {
		public static final String ACTION_RESP = "DATABASE_UPDATED";

		@Override
		public void onReceive(Context context, Intent intent) {
			if(NavigationDrawerFragment.mCurrentSelectedPosition == 1)
				populateList(Members.mainSelected);
		}
	}
}
