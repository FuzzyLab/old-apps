package com.fuzzylabs.to_do;

import java.sql.SQLException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.fuzzylabs.to_do.MainActivity.NotifyReceiverMain;
import com.fuzzylabs.to_do.R;

public class NotificationReceiver extends BroadcastReceiver {
	
	Cursor cursor = null;

	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Bundle bundle = intent.getExtras();
		final String id = bundle.getString("job_id");
		
		String jobName = "";

		final TodoDb info = new TodoDb(context);

		try {
			info.open();

			String[] columns = { info.KEY_ROWID, info.JOB_NAME };
			String[] args = { id };
			cursor = info.ourDatabase.query(info.DATABASE_TABLE, columns,
					info.KEY_ROWID + " like ?", args, null, null, null);
			cursor.moveToFirst();

			jobName = cursor.getString(1);

			ContentValues cv = new ContentValues();
			cv.put(info.IS_NOTIFICATION, 0);
			cv.put(info.IS_ALARM, 0);
			cv.put(info.IS_DONE, 1);
			String where = info.KEY_ROWID + " LIKE ?";
			String[] arg = { id };
			info.ourDatabase.update(info.DATABASE_TABLE, cv, where, arg);
			info.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

		}

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent notIntent = new Intent(context, MainActivity.class);
		notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent pIntent = PendingIntent.getActivity(context, Integer.parseInt(id), notIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new Notification(R.drawable.ic_launcher,
				"Job Due", System.currentTimeMillis());
		
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_ALL;
		notification.setLatestEventInfo(context, "To Do | Notification", jobName + " is due",
				pIntent);
		
		notificationManager.notify(Integer.parseInt(id), notification);
		
		Intent broadcastIntent = new Intent();
		broadcastIntent.putExtra("id", id);
        broadcastIntent.setAction(NotifyReceiverMain.ACTION_NOTIFICATION);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        context.sendBroadcast(broadcastIntent);
	}

}
