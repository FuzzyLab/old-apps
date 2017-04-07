package com.fuzzylabs.medicinetracker;

import java.sql.SQLException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

public class NotificationResetterOnBoot extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {

		MedicineDB mediInfo = new MedicineDB(context);
		DoseDB doseInfo = new DoseDB(context);
		Cursor cursor = null;
		String where = "";

		try {
			mediInfo.open();
			doseInfo.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		where = MedicineDB.IS_NOTIFICATION + " = " + 1;
		cursor = mediInfo.getAllRows(where, null);
		while(cursor.moveToNext()) {
			final String mediId = cursor.getString(0);
			(new Thread() {
				@Override
				public void run() {
					(new NotificationSetter()).setNotification(
							context, mediId, 0);
				}
			}).start();
		}
		
		mediInfo.close();
		
		where = DoseDB.SNOOZE_TIME + " != " + "''";
		cursor = null;
		cursor = doseInfo.getAllRows(where, null);
		while(cursor.moveToNext()) {
			final String doseId = cursor.getString(0);
			(new Thread() {
				@Override
				public void run() {
					(new SnoozeSetter()).setSnooze(
							context, doseId);
				}
			}).start();
		}
		
		doseInfo.close();
	}
}
