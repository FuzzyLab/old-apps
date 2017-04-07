package com.fuzzylabs.medicinetracker;

import java.text.DecimalFormat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

public class NotificationReceiver extends BroadcastReceiver {

	final DecimalFormat df = new DecimalFormat("00");

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, final Intent intent) {

		Bundle bundle = intent.getExtras();
		final String mediId = bundle.getString("medi_id");
		final String mediName = Medicine.getMediName(context, mediId);
		final String doseId = bundle.getString("dose_id");
		final String doseString = Medicine.getDoseString(context, mediId);
		final String dueDate = Medicine.getDueDate(context, mediId);
		final String dueTime = Medicine.getDueTime(context, mediId);

		final MedicineDB mediInfo = new MedicineDB(context);
		final DoseDB dosesInfo = new DoseDB(context);
		final SettingsDB settingsInfo = new SettingsDB(context);
		String member = "";
		String toneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString();
		int vibrate = 1;
		int light = 1;

		try {
			dosesInfo.open();
			ContentValues cv = new ContentValues();
			cv.put(DoseDB.IS_DONE, 1);
			String where = DoseDB.KEY_ROWID + " LIKE ?";
			String[] arg = { doseId };
			dosesInfo.ourDatabase.update(DoseDB.DOSE_TABLE, cv, where,
					arg);

			mediInfo.open();
			Cursor cursor = mediInfo.getAllRows(MedicineDB.KEY_ROWID + " = " + Integer.parseInt(mediId), null);
			cursor.moveToFirst();
			member = cursor.getString(6);
			
			settingsInfo.open();
			toneUri = settingsInfo.getTone();
			vibrate = settingsInfo.getVibrate();
			light = settingsInfo.getLight();
		} catch (Exception e) {
		} finally {
			dosesInfo.close();
			mediInfo.close();
			settingsInfo.close();
		}

		(new NotificationSetter()).setNotification(context, mediId, 1);

		Intent mainIntent = new Intent(context, MedicineActivity.class);
		mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent mainPIntent = PendingIntent.getActivity(context, Integer.parseInt(doseId), mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
        Intent rejectIntent = new Intent(context, NotificationBroadcastReceiver.class);
		rejectIntent.setAction(NotificationBroadcastReceiver.SKIP);
		rejectIntent.putExtra(NotificationBroadcastReceiver.DOSE_ID, doseId);
		rejectIntent.putExtra(NotificationBroadcastReceiver.MEDI_ID, mediId);
		rejectIntent.putExtra(NotificationBroadcastReceiver.DUE_DATE, dueDate);
		rejectIntent.putExtra(NotificationBroadcastReceiver.DUE_TIME, dueTime);
        PendingIntent rejectPIntent = PendingIntent.getBroadcast(context, Integer.parseInt(doseId), rejectIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        Intent snoozeIntent = new Intent(context, NotificationBroadcastReceiver.class);
		snoozeIntent.setAction(NotificationBroadcastReceiver.SNOOZE);
		snoozeIntent.putExtra(NotificationBroadcastReceiver.DOSE_ID, doseId);
		snoozeIntent.putExtra(NotificationBroadcastReceiver.MEDI_ID, mediId);
		snoozeIntent.putExtra(NotificationBroadcastReceiver.DUE_DATE, dueDate);
		snoozeIntent.putExtra(NotificationBroadcastReceiver.DUE_TIME, dueTime);
        PendingIntent snoozePIntent = PendingIntent.getBroadcast(context, Integer.parseInt(doseId), snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		Builder builder = new Notification.Builder(context)
		.setContentTitle(mediName + " | " + member)
        .setContentText(doseString + " : is due")
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentIntent(mainPIntent)
        .setSound(Uri.parse(toneUri))
        .setAutoCancel(true)
        .addAction(R.drawable.skip, "Skip", rejectPIntent)
        .addAction(R.drawable.snooze, "Snooze", snoozePIntent);
		
		if(vibrate == 1)
			builder.setDefaults(Notification.DEFAULT_VIBRATE);
		if(light == 1)
			builder.setDefaults(Notification.DEFAULT_LIGHTS);
		
		Notification notification = builder.build();
		
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(Integer.parseInt(doseId));
		notificationManager.notify(Integer.parseInt(doseId), notification);
	}
}
