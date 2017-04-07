package com.fuzzylabs.medicinetracker;

import java.sql.SQLException;
import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

public class Medicine {

	private static MedicineDB mediInfo = null;
	private static DoseDB dosesInfo = null;
	private static Cursor cursor = null;

	public static String getMediName(Context context, String mediId) {
		mediInfo = new MedicineDB(context);
		try {
			mediInfo.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String[] columns = { MedicineDB.KEY_ROWID, MedicineDB.MEDI_NAME };
		cursor = mediInfo.ourDatabase.query(MedicineDB.MEDICINE_TABLE, columns,
				MedicineDB.KEY_ROWID + " like ?", new String[] { mediId }, null,
				null, null);
		cursor.moveToFirst();
		String mediName = cursor.getString(1);
		mediInfo.close();
		return mediName;
	}
	
	public static String getMediId(Context context, String mediName, String forWho) {
		mediInfo = new MedicineDB(context);
		try {
			mediInfo.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String[] columns = { MedicineDB.KEY_ROWID };
		cursor = mediInfo.ourDatabase.query(MedicineDB.MEDICINE_TABLE, columns,
				MedicineDB.MEDI_NAME + " like ? AND " + MedicineDB.FOR_WHO
						+ " like ?", new String[] { mediName, forWho }, null,
				null, null);
		cursor.moveToFirst();
		String mediId = cursor.getString(0);
		mediInfo.close();
		return mediId;
	}
	
	public static String getDueDate(Context context, String mediId) {
		mediInfo = new MedicineDB(context);
		try {
			mediInfo.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String[] columns = { MedicineDB.KEY_ROWID, MedicineDB.DUE_DATE };
		cursor = mediInfo.ourDatabase.query(MedicineDB.MEDICINE_TABLE, columns,
				MedicineDB.KEY_ROWID + " like ?", new String[] { mediId }, null,
				null, null);
		cursor.moveToFirst();
		String dueDate = cursor.getString(1);
		mediInfo.close();
		return dueDate;
	}
	
	public static String getDueTime(Context context, String mediId) {
		mediInfo = new MedicineDB(context);
		try {
			mediInfo.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String[] columns = { MedicineDB.KEY_ROWID, MedicineDB.DUE_TIME };
		cursor = mediInfo.ourDatabase.query(MedicineDB.MEDICINE_TABLE, columns,
				MedicineDB.KEY_ROWID + " like ?", new String[] { mediId }, null,
				null, null);
		cursor.moveToFirst();
		String dueTime = cursor.getString(1);
		mediInfo.close();
		return dueTime;
	}
	
	public static ArrayList<String> getMediListForMember(Context context, String member) {
		mediInfo = new MedicineDB(context);
		try {
			mediInfo.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String[] columns = { MedicineDB.KEY_ROWID, MedicineDB.MEDI_NAME };
		cursor = mediInfo.ourDatabase.query(MedicineDB.MEDICINE_TABLE, columns,
				MedicineDB.FOR_WHO + " like ?", new String[] { member }, null,
				null, null);
		ArrayList<String> mediList = new ArrayList<String>();
		while(cursor.moveToNext()) {
			mediList.add(cursor.getString(1));
		}
		mediInfo.close();
		return mediList;
	}
	
	public static String getDoseString(Context context, String mediId) {
		mediInfo = new MedicineDB(context);
		try {
			mediInfo.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String[] columns = { MedicineDB.KEY_ROWID, MedicineDB.DOSE_AMOUNT, MedicineDB.DOSE_UNIT };
		cursor = mediInfo.ourDatabase.query(MedicineDB.MEDICINE_TABLE, columns,
				MedicineDB.KEY_ROWID + " like ?", new String[] { mediId }, null,
				null, null);
		cursor.moveToFirst();
		int doseAmount = cursor.getInt(1);
		int doseUnit = cursor.getInt(2);
		mediInfo.close();
		
		String[] typeArray = context.getResources().getStringArray(R.array.dosetypelist);
		String doseString = doseAmount + " " + typeArray[doseUnit];
		return doseString;
	}
	
	public static String getNotes(Context context, String mediId) {
		mediInfo = new MedicineDB(context);
		try {
			mediInfo.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String[] columns = { MedicineDB.KEY_ROWID, MedicineDB.NOTES };
		cursor = mediInfo.ourDatabase.query(MedicineDB.MEDICINE_TABLE, columns,
				MedicineDB.KEY_ROWID + " like ?", new String[] { mediId }, null,
				null, null);
		cursor.moveToFirst();
		String notes = cursor.getString(1);
		mediInfo.close();
		return notes;
	}

	public static void deleteMedicine(Context context, String mediId) {
		dosesInfo = new DoseDB(context);
		mediInfo = new MedicineDB(context);
		Cursor cursorDoses = null;
		try {
			dosesInfo.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		cursorDoses = dosesInfo.getAllRows(DoseDB.MEDI_ID + " LIKE ? ",
				new String[] { mediId });
		while (cursorDoses.moveToNext()) {
			String id = cursorDoses.getString(0);
			AlarmManager am = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			Intent nIntent = new Intent(context, NotificationReceiver.class);
			PendingIntent npi = PendingIntent.getBroadcast(context,
					Integer.parseInt(id), nIntent, 0);
			am.cancel(npi);
			
			Intent sIntent = new Intent(context, SnoozeReceiver.class);
			PendingIntent spi = PendingIntent.getBroadcast(context,
					Integer.parseInt(id), sIntent, 0);
			am.cancel(spi);
		}
		dosesInfo.close();
		try {
			mediInfo.open();
			mediInfo.ourDatabase.delete(MedicineDB.MEDICINE_TABLE,
					MedicineDB.KEY_ROWID + "=" + mediId, null);
			mediInfo.ourDatabase.delete(
					MedicineDB.MISSED_DOSE_TABLE,
					MedicineDB.MISSED_MEDI_ID + "="
							+ mediId, null);
			mediInfo.close();

			dosesInfo.open();
			dosesInfo.ourDatabase.delete(DoseDB.DOSE_TABLE,
					DoseDB.MEDI_ID + " LIKE ?", new String[] { mediId });
			dosesInfo.close();
		} catch (SQLException e) {
		}
	}

	public static void deleteBlankMedicine(Context context, String mediId) {
		dosesInfo = new DoseDB(context);
		mediInfo = new MedicineDB(context);
		try {
			mediInfo.open();
			mediInfo.ourDatabase.delete(MedicineDB.MEDICINE_TABLE,
					MedicineDB.KEY_ROWID + "=" + mediId, null);
			mediInfo.close();

			dosesInfo.open();
			dosesInfo.ourDatabase.delete(DoseDB.DOSE_TABLE,
					DoseDB.MEDI_ID + " LIKE ?", new String[] { mediId });
			dosesInfo.close();
		} catch (SQLException e) {
		}
	}
}
