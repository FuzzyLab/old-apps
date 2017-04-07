package com.fuzzylabs.medicinetracker;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Calendar;

import com.fuzzylabs.medicinetracker.HomeFragment.NotifyReceiver;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

	public static final String SKIP = "skip";
	public static final String SNOOZE = "snooze";

	public static final String DOSE_ID = "doseId";
	public static final String MEDI_ID = "mediId";
	public static final String DUE_DATE = "dueDate";
	public static final String DUE_TIME = "dueTime";
	
	final DecimalFormat df = new DecimalFormat("00");
	private MedicineDB mediInfo;
	private DoseDB doseInfo;
	private SettingsDB settingsInfo;

	@Override
	public void onReceive(Context context, Intent intent) {
		String doseId = intent.getExtras().getString(DOSE_ID);
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(Integer.parseInt(doseId));

		String action = intent.getAction();

		if (action.equals(SKIP)) {
			String mediId = intent.getExtras().getString(MEDI_ID);
			String dueDate = intent.getExtras().getString(DUE_DATE);
			String dueTime = intent.getExtras().getString(DUE_TIME);

			mediInfo = new MedicineDB(context);
			try {
				mediInfo.open();
				mediInfo.createMissedEntry(mediId, dueDate, dueTime);
				mediInfo.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (action.equals(SNOOZE)) {
			String mediId = intent.getExtras().getString(MEDI_ID);
			String dueDate = intent.getExtras().getString(DUE_DATE);
			String dueTime = intent.getExtras().getString(DUE_TIME);
			
			settingsInfo = new SettingsDB(context);
			int snoozeDuration = 10;
			try {
				settingsInfo.open();
				snoozeDuration = settingsInfo.getSnoozeTime();
				settingsInfo.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			final Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.SECOND, 0);
			calendar.add(Calendar.MINUTE, snoozeDuration);
			
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int minute = calendar.get(Calendar.MINUTE);
			
			int date = calendar.get(Calendar.DATE);
			int month = calendar.get(Calendar.MONTH) + 1;
			int year = calendar.get(Calendar.YEAR);
			
			String snoozeTime = df.format(hour)+":"+df.format(minute)+":00";
			String snoozeDate = ""+year+"-"+df.format(month)+"-"+df.format(date);

			doseInfo = new DoseDB(context);
			try {
				doseInfo.open();
				ContentValues cv = new ContentValues();
				cv.put(DoseDB.SNOOZE_TIME, snoozeTime);
				cv.put(DoseDB.SNOOZE_DATE, snoozeDate);
				cv.put(DoseDB.SNOOZE_DUE_DATE, dueDate);
				String where = DoseDB.KEY_ROWID + " LIKE ?";
				String[] arg = { doseId };
				doseInfo.ourDatabase.update(DoseDB.DOSE_TABLE, cv, where,
						arg);
				doseInfo.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			AlarmManager am = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			Intent sIntent = new Intent(context, SnoozeReceiver.class);
			sIntent.putExtra("dose_id", doseId);
			sIntent.putExtra("medi_id", mediId);
			sIntent.putExtra("due_date", dueDate);
			sIntent.putExtra("due_time", dueTime);
			PendingIntent pSIntent = PendingIntent.getBroadcast(context,
					Integer.parseInt(doseId), sIntent, 0);
			am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pSIntent);
			
			Intent broadcastIntent = new Intent();
	        broadcastIntent.setAction(NotifyReceiver.ACTION_RESP);
	        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
	        context.sendBroadcast(broadcastIntent);
		}
	}
}