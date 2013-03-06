package eu.vstepik.feelings.notify;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import eu.vstepik.feelings.ui.SettingsActivity;

public class AlarmSetter extends BroadcastReceiver {

	@SuppressWarnings("unused")
	private static final String TAG = "AlarmSetter";

	@Override
	public void onReceive(Context context, Intent intent) {
		// set the alarm
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 234,
				new Intent(context, NotificationStarter.class), 0);
		Calendar alarmTime = new GregorianCalendar();
		// start every day at specified time
		alarmTime.set(Calendar.HOUR_OF_DAY,
				SettingsActivity.getNotificationHour(context));
		alarmTime.set(Calendar.MINUTE,
				SettingsActivity.getNotificationMinute(context));
		Calendar now = new GregorianCalendar();
		// if it's past the alarm, schedule it for the next day
		if (alarmTime.before(now)) {
			alarmTime.setTimeInMillis(alarmTime.getTimeInMillis()
					+ AlarmManager.INTERVAL_DAY);
		}
		alarmManager.setRepeating(AlarmManager.RTC,
				alarmTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
				pendingIntent);
	}

}
