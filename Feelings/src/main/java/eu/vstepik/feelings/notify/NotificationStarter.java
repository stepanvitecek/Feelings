package eu.vstepik.feelings.notify;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import eu.vstepik.feelings.R;
import eu.vstepik.feelings.ui.NewFeelingActivity;

public class NotificationStarter extends BroadcastReceiver {

    @SuppressWarnings("unused")
    private static final String TAG = "NotificationStarter";

    @Override
    public void onReceive(Context context, Intent intent) {
        // fire the notification
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, NewFeelingActivity.class)
                        .setAction(Intent.ACTION_TIME_TICK), 0);
        @SuppressWarnings("deprecation")
        Notification notification = new Notification.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker(context.getString(R.string.your_feel))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(context.getString(R.string.your_feel))
                .setContentText(context.getString(R.string.touch))
                .getNotification();
        notificationManager.notify(1, notification);
    }
}