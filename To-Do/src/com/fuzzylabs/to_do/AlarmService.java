package com.fuzzylabs.to_do;

import java.sql.SQLException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.fuzzylabs.to_do.MainActivity.AlarmReceiverMain;
import com.fuzzylabs.to_do.R;
import com.fuzzylabs.to_do.MainActivity.NotifyReceiverMain;

public class AlarmService extends Service {
	private static Ringtone ringtone;
	Cursor cursor = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Context context = getApplicationContext();
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
			info.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

		if (uri == null) {
			uri = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			if (uri == null) {
				uri = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			}
		}

		ringtone = RingtoneManager.getRingtone(context, uri);
		ringtone.setStreamType(AudioManager.STREAM_ALARM);

		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int volume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
		if (volume == 0) {
			volume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
		}
		audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume,
				AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
		
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int state = telephonyManager.getCallState();
        
        if(state == telephonyManager.CALL_STATE_IDLE) {
        	ringtone.play();
        }
        
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent notIntent = new Intent(context, MainActivity.class);
		notIntent.putExtra("Alarm", id);
		notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent pIntent = PendingIntent.getActivity(context, Integer.parseInt(id),
				notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new Notification(R.drawable.ic_launcher,
				"Job Due", System.currentTimeMillis());
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_ALL;
		notification.setLatestEventInfo(context, "To Do | Alarm", jobName
				+ " is due", pIntent);
		notificationManager.notify(Integer.parseInt(id), notification);
		
		Intent broadcastIntent = new Intent();
		broadcastIntent.putExtra("id", id);
        broadcastIntent.setAction(AlarmReceiverMain.ACTION_ALARM);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        context.sendBroadcast(broadcastIntent);

		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		ringtone.stop();
	}
}
