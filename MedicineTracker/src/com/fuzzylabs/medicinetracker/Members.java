package com.fuzzylabs.medicinetracker;

import java.sql.SQLException;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;

public class Members {
	private static MemberDB membersInfo = null;
	public static ArrayList<String> membersList = new ArrayList<String>();
	public static String mainSelected = "";
	public static String historySelected = "";

	public static void fetchList(Context context) {
		membersInfo = new MemberDB(context);
		membersList.clear();
		try {
			membersInfo.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Cursor cursor = membersInfo.getAllRows();
		if (cursor.getCount() == 0) {
			membersInfo.createEntry("ME");
			membersList.add("ME");
		} else {
			while (cursor.moveToNext()) {
				membersList.add(cursor.getString(1));
			}
		}
		mainSelected = membersList.get(0);
		historySelected = membersList.get(0);
		membersInfo.close();
	}
}
