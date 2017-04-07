package com.fuzzylabs.medicinetracker;

import java.text.DecimalFormat;
import java.util.Calendar;

import com.fuzzylabs.medicinetracker.HomeFragment.NotifyReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

public class SnoozeSetter {

	private Cursor cursor = null;
	final DecimalFormat df = new DecimalFormat("00");

	public void setSnooze(final Context context, final String doseId) {

		final DoseDB doseInfo = new DoseDB(context);
			try {
				doseInfo.open();
				cursor = doseInfo.getAllRows(DoseDB.KEY_ROWID + " like ?", new String[] { doseId });
				cursor.moveToFirst();
				String mediId = cursor.getString(1);
				String doseTime = cursor.getString(2);
				String snoozeDate = cursor.getString(5);
				String snoozeTime = cursor.getString(6);
				String snoozeDueDate = cursor.getString(7);
				doseInfo.close();
				if(snoozeTime.equals("") | snoozeDate.equals(""))
					return;
					
				String date[] = snoozeDate.split("-");
				int day = Integer.parseInt(date[2]);
				int month = Integer.parseInt(date[1]) - 1;
				int year = Integer.parseInt(date[0]);
				String time[] = snoozeTime.split(":");
				int hour = Integer.parseInt(time[0]);
				int minute = Integer.parseInt(time[1]);
				
				Calendar calendar = Calendar.getInstance();
				calendar.set(year, month, day, hour, minute, 0);

				AlarmManager am = (AlarmManager) context
						.getSystemService(Context.ALARM_SERVICE);
				Intent sIntent = new Intent(context, SnoozeReceiver.class);
				sIntent.putExtra("dose_id", doseId);
				sIntent.putExtra("medi_id", mediId);
				sIntent.putExtra("due_date", snoozeDueDate);
				sIntent.putExtra("due_time", doseTime);
				PendingIntent pSIntent = PendingIntent.getBroadcast(context,
						Integer.parseInt(doseId), sIntent, 0);
				am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pSIntent);
				
				Intent broadcastIntent = new Intent();
		        broadcastIntent.setAction(NotifyReceiver.ACTION_RESP);
		        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		        context.sendBroadcast(broadcastIntent);
				
			} catch(Exception ex) {
				
			}
	}
}