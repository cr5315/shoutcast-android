package com.cr5315.shoutcast;

import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class StreamService extends Service {
	private static final String TAG = "StreamService";
	MediaPlayer mp;
	boolean isPlaying;
	SharedPreferences prefs;
	SharedPreferences.Editor editor;
	Notification n;
	NotificationManager notificationManager;
	// Change this int to some number specifically for this app
	int notifId = 5315;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
		
		// Init the SharedPreferences and Editor
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		editor = prefs.edit();
		
		// Set up the buffering notification
		notificationManager = (NotificationManager) getApplicationContext()
				.getSystemService(NOTIFICATION_SERVICE);
		Context context = getApplicationContext();
		
		String notifTitle = context.getResources().getString(R.string.app_name);
		String notifMessage = context.getResources().getString(R.string.buffering);
		
		n = new Notification();
		n.icon = R.drawable.ic_launcher;
		n.tickerText = "Buffering";
		n.when = System.currentTimeMillis();
		
		Intent nIntent = new Intent(context, MainActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, nIntent, 0);
		
		n.setLatestEventInfo(context, notifTitle, notifMessage, pIntent);
		
		notificationManager.notify(notifId, n);
		
		// It's very important that you put the IP/URL of your ShoutCast stream here
		// Otherwise you'll get Webcom Radio
		String url = "http://176.31.115.196:8214/";
		mp = new MediaPlayer();
		mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			mp.setDataSource(url);
			mp.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "SecurityException");
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "IllegalStateException");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "IOException");
		}	
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(TAG, "onStart");
		mp.start();
		// Set the isPlaying preference to true
		editor.putBoolean("isPlaying", true);
		editor.commit();
		
		Context context = getApplicationContext();
		String notifTitle = context.getResources().getString(R.string.app_name);
		String notifMessage = context.getResources().getString(R.string.now_playing);
		
		n.icon = R.drawable.ic_launcher;
		n.tickerText = notifMessage;
		n.flags = Notification.FLAG_NO_CLEAR;
		n.when = System.currentTimeMillis();
		
		Intent nIntent = new Intent(context, MainActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, nIntent, 0);
		
		n.setLatestEventInfo(context, notifTitle, notifMessage, pIntent);
		// Change 5315 to some nother number
		notificationManager.notify(notifId, n);
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		mp.stop();
		mp.release();
		mp = null;
		editor.putBoolean("isPlaying", false);
		editor.commit();
		notificationManager.cancel(notifId);
	}

}