package com.fuzzylabs.medicinetracker;

import java.sql.SQLException;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MembersFragment extends Fragment {
	private MemberDB membersInfo = null;
	private MedicineDB mediInfo = null;
	Cursor cursor = null;
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static MembersFragment membersFragment = null;
	
	public static Fragment newInstance(int sectionNumber) {
		if(membersFragment == null)
			membersFragment = new MembersFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		membersFragment.setArguments(args);
		return membersFragment;
	}

	public MembersFragment() {
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
			
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.members);

		mediInfo = new MedicineDB(getActivity().getApplicationContext());
		membersInfo = new MemberDB(getActivity().getApplicationContext());
		
		Button addMemberBtn = (Button) getActivity().findViewById(R.id.addMemberBtn);
		addMemberBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String newMember = ((EditText) getActivity().findViewById(R.id.newMemberView))
						.getText().toString();
				if (!newMember.equals("")) {
					try {
						membersInfo.open();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					membersInfo.createEntry(newMember);
					membersInfo.close();
				}
				((EditText) getActivity().findViewById(R.id.newMemberView)).setText("");
				populateList();
				Members.fetchList(getActivity().getApplicationContext());
			}
		});
		
		populateList();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_members, container, false);
		return rootView;
	}

	private void populateList() {
		try {
			membersInfo.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		cursor = membersInfo.getAllRows();
		if (cursor.getCount() == 0) {
			membersInfo.createEntry("ME");
			populateList();
		} else {

			String[] fromColumns = { MemberDB.MEMBER_NAME };
			int[] toViews = { R.id.memberView };
			SimpleCursorAdapter mAdapter;
			mAdapter = new Custom_Adapter(getActivity().getApplicationContext(),
					R.layout.member_item, cursor, fromColumns, toViews);
			ListView myList = (ListView) getActivity().findViewById(R.id.membersList);
			myList.setAdapter(mAdapter);
		}
		membersInfo.close();
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
			final String memberName = cursor.getString(1);
			Button deleteMember = (Button) view.findViewById(R.id.deleteMember);
			deleteMember.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getActivity());
					builder.setTitle("Delete " + memberName + "?");
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
										Cursor cursorTemp = mediInfo
												.getAllRows(
														MedicineDB.FOR_WHO
																+ " LIKE ? ",
														new String[] { memberName });
										while (cursorTemp.moveToNext()) {
											String mediId = cursorTemp
													.getString(0);
											Medicine.deleteMedicine(context,
													mediId);
										}
										membersInfo.open();
										membersInfo.ourDatabase.delete(
												MemberDB.MEMBER_TABLE,
												MemberDB.KEY_ROWID + "="
														+ id, null);
										membersInfo.close();
									} catch (SQLException e) {
										Toast.makeText(getActivity(),
												"Error", Toast.LENGTH_SHORT)
												.show();
									} finally {
										populateList();
										Members.fetchList(getActivity().getApplicationContext());
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
