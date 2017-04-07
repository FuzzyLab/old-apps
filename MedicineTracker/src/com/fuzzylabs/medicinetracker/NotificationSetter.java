package com.fuzzylabs.medicinetracker;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.fuzzylabs.medicinetracker.HomeFragment.NotifyReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

public class NotificationSetter {

	private Cursor cursor = null;
	final DecimalFormat df = new DecimalFormat("00");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public void setNotification(final Context context, final String mediId,
			int code) {

		final MedicineDB mediInfo = new MedicineDB(context);
		final DoseDB dosesInfo = new DoseDB(context);
		String nextDoseId = "";
		String nextTime = "";
		if (code == 0) {
			try {
				mediInfo.open();

				cursor = mediInfo.getAllRows(MedicineDB.KEY_ROWID + " like ?", new String[] { mediId });
				cursor.moveToFirst();
				int interval = Integer.parseInt(cursor.getString(2));
				String dueDate = cursor.getString(4);
				mediInfo.close();
				String date[] = dueDate.split("-");
				int day = Integer.parseInt(date[2]);
				int month = Integer.parseInt(date[1]) - 1;
				int year = Integer.parseInt(date[0]);

				Calendar calendar = Calendar.getInstance();
				calendar.clear();
				calendar.set(year, month, day);

				Calendar today = Calendar.getInstance();

				while (sdf.format(today.getTime()).compareTo(
						sdf.format(calendar.getTime())) > 0) {
					calendar.add(Calendar.DAY_OF_MONTH, interval);
				}
				day = calendar.get(Calendar.DAY_OF_MONTH);
				month = calendar.get(Calendar.MONTH) + 1;
				year = calendar.get(Calendar.YEAR);

				dueDate = "" + year + "-" + df.format(month) + "-"
						+ df.format(day);

				day = today.get(Calendar.DAY_OF_MONTH);
				month = today.get(Calendar.MONTH) + 1;
				year = today.get(Calendar.YEAR);

				String todayDate = "" + year + "-" + df.format(month) + "-"
						+ df.format(day);

				if (todayDate.equals(dueDate)) {
					try {
						dosesInfo.open();
						String[] columns1 = { DoseDB.KEY_ROWID,
								DoseDB.MEDI_ID, DoseDB.DOSE,
								DoseDB.IS_SET, DoseDB.IS_DONE };
						cursor = dosesInfo.ourDatabase.query(
								DoseDB.DOSE_TABLE, columns1,
								DoseDB.MEDI_ID + " LIKE ? ",
								new String[] { mediId }, null, null,
								"Time(dose)");

						while(cursor.moveToNext()) {
							String time[] = cursor.getString(2).split(":");
							String doseId = cursor.getString(0);
							calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
							calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));
							calendar.set(Calendar.SECOND, Integer.parseInt(time[2]));
							if (Calendar.getInstance().compareTo(calendar) > 0) {
								ContentValues cv = new ContentValues();
								cv.put(DoseDB.IS_DONE, 1);
								String whereUpdate = DoseDB.KEY_ROWID
										+ " LIKE ?";
								String[] arg = { doseId };
								dosesInfo.ourDatabase.update(
										DoseDB.DOSE_TABLE, cv,
										whereUpdate, arg);
							}
						}
						dosesInfo.close();
					} catch (Exception e) {
					}
				}
				mediInfo.open();
				ContentValues cv = new ContentValues();
				cv.put(MedicineDB.DUE_DATE, dueDate);
				String where = MedicineDB.KEY_ROWID + " LIKE ?";
				String[] arg = { mediId };
				mediInfo.ourDatabase.update(MedicineDB.MEDICINE_TABLE, cv, where,
						arg);

				mediInfo.close();
			} catch (Exception e2) {

				e2.printStackTrace();
			}
		}
		try {
			dosesInfo.open();
			String[] columnsBefore = { DoseDB.KEY_ROWID,
					DoseDB.MEDI_ID, DoseDB.DOSE, DoseDB.IS_SET,
					DoseDB.IS_DONE };
			cursor = dosesInfo.ourDatabase.query(DoseDB.DOSE_TABLE,
					columnsBefore, DoseDB.MEDI_ID + " LIKE ? AND "
							+ DoseDB.IS_DONE + " LIKE ? ", new String[] {
							mediId, "0" }, null, null, "Time(dose)");
			int count = cursor.getCount();
			
			dosesInfo.close();

			if (count == 0) {
				dosesInfo.open();
				ContentValues cv = new ContentValues();
				cv.put(DoseDB.IS_DONE, 0);
				String where = DoseDB.MEDI_ID + " LIKE ?";
				String[] arg = { mediId };
				dosesInfo.ourDatabase.update(DoseDB.DOSE_TABLE, cv,
						where, arg);
				dosesInfo.close();

				mediInfo.open();
				Cursor cursorTemp = null;
				cursorTemp = mediInfo.getAllRows(MedicineDB.KEY_ROWID + " like ?", new String[] { mediId });
				cursorTemp.moveToFirst();
				String dueDate = cursorTemp.getString(3);
				String interval = cursorTemp.getString(2);
				String date[] = dueDate.split("-");

				int day = Integer.parseInt(date[2]);
				int month = Integer.parseInt(date[1]) - 1;
				int year = Integer.parseInt(date[0]);
				Calendar calendar = Calendar.getInstance();
				calendar.set(year, month, day);
				calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(interval));

				day = calendar.get(Calendar.DAY_OF_MONTH);
				month = calendar.get(Calendar.MONTH);
				year = calendar.get(Calendar.YEAR);
				final String nextDueDate = "" + year + "-"
						+ df.format(month + 1) + "-" + df.format(day);

				ContentValues cv1 = new ContentValues();
				cv1.put(MedicineDB.DUE_DATE, nextDueDate);
				String where1 = MedicineDB.KEY_ROWID + " LIKE ?";
				String[] arg1 = { mediId };
				mediInfo.ourDatabase.update(MedicineDB.MEDICINE_TABLE, cv1,
						where1, arg1);
				mediInfo.close();
			}

			dosesInfo.open();
			String[] columnsAfter = { DoseDB.KEY_ROWID, DoseDB.MEDI_ID,
					DoseDB.DOSE, DoseDB.IS_SET, DoseDB.IS_DONE };
			cursor = dosesInfo.ourDatabase.query(DoseDB.DOSE_TABLE,
					columnsAfter, DoseDB.MEDI_ID + " LIKE ? AND "
							+ DoseDB.IS_DONE + " LIKE ? ", new String[] {
							mediId, "0" }, null, null, "Time(dose)");
			cursor.moveToFirst();
			nextDoseId = cursor.getString(0);
			nextTime = cursor.getString(2);
			dosesInfo.close();

			AlarmManager am = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, NotificationReceiver.class);
			intent.putExtra("dose_id", nextDoseId);
			intent.putExtra("medi_id", mediId);
			PendingIntent pi = PendingIntent.getBroadcast(context,
					Integer.parseInt(nextDoseId), intent, 0);
			String date[] = null;
			String time[] = null;

			mediInfo.open();
			ContentValues cv = new ContentValues();
			cv.put(MedicineDB.DUE_TIME, nextTime);
			String where = MedicineDB.KEY_ROWID + " LIKE ?";
			String[] arg = { mediId };
			mediInfo.ourDatabase
					.update(MedicineDB.MEDICINE_TABLE, cv, where, arg);

			Cursor cursorTemp = mediInfo.getAllRows(MedicineDB.KEY_ROWID
							+ " like ?", new String[] { mediId });
			cursorTemp.moveToFirst();
			String dueDate = cursorTemp.getString(3);
			date = dueDate.split("-");
			time = nextTime.split(":");
			mediInfo.close();

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			int dueDay = Integer.parseInt(date[2]);
			int dueMonth = Integer.parseInt(date[1]) - 1;
			int dueYear = Integer.parseInt(date[0]);
			int hour = Integer.parseInt(time[0]);
			int minute = Integer.parseInt(time[1]);
			calendar.set(dueYear, dueMonth, dueDay, hour, minute, 0);
			am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
			
			Intent broadcastIntent = new Intent();
	        broadcastIntent.setAction(NotifyReceiver.ACTION_RESP);
	        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
	        context.sendBroadcast(broadcastIntent);
			
		} catch (Exception e) {
		}
	}
}