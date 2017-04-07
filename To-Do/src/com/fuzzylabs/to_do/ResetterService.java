package com.fuzzylabs.to_do;

import java.sql.SQLException;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;

public class ResetterService extends Service
{

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @SuppressWarnings("deprecation")
	@Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
    	Context context = getApplicationContext();
    	
    	TodoDb info = new TodoDb(context);
		Cursor cursor = null;
		try {
			info.open();
			String[] columns = { info.KEY_ROWID, info.JOB_NAME, info.DUE_DATE,
					info.DUE_TIME, info.IS_NOTIFICATION, info.IS_ALARM,
					info.IS_DONE, info.URGENCY };
			cursor = info.ourDatabase.query(info.DATABASE_TABLE, columns, null,
					null, null, null, null);

			while (cursor.moveToNext()) {

				String id = cursor.getString(0);

				if (cursor.getInt(4) == 1) {
					
					String[] date = cursor.getString(2).split("-");
					int day = Integer.parseInt(date[2]);
					int month = Integer.parseInt(date[1]) - 1;
					int year = Integer.parseInt(date[0]);

					String[] time = cursor.getString(3).split(" : ");
					int hour = Integer.parseInt(time[0]);
					int minute = Integer.parseInt(time[1]);

					AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
					Intent iNotification = new Intent(context,
							NotificationReceiver.class);
					iNotification.putExtra("job_id", id);
					PendingIntent pi = PendingIntent.getBroadcast(context,
							Integer.parseInt(id), iNotification, 0);
					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(System.currentTimeMillis());
					calendar.set(year, month, day, hour, minute, 0);
					am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
							pi);
					
				} else if (cursor.getInt(5) == 1) {
					
					String[] date = cursor.getString(2).split("-");
					int day = Integer.parseInt(date[2]);
					int month = Integer.parseInt(date[1]) - 1;
					int year = Integer.parseInt(date[0]);

					String[] time = cursor.getString(3).split(" : ");
					int hour = Integer.parseInt(time[0]);
					int minute = Integer.parseInt(time[1]);
					
					AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
					Intent iNotification = new Intent(context,
							AlarmService.class);
					iNotification.putExtra("job_id", id);
					PendingIntent pi = PendingIntent.getService(context,
							Integer.parseInt(id), iNotification, 0);
					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(System.currentTimeMillis());
					calendar.set(year, month, day, hour, minute, 0);
					am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
							pi);
					
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			info.close();
		}
    	
        return START_NOT_STICKY;
    }
}
