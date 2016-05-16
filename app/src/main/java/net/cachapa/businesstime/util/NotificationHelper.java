package net.cachapa.businesstime.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import net.cachapa.businesstime.R;
import net.cachapa.businesstime.activity.MainActivity;
import net.cachapa.businesstime.manager.TimeManager;
import net.cachapa.businesstime.model.WorkDay;
import net.cachapa.businesstime.service.NotificationService;

public class NotificationHelper {
    private static final int NOTIFICATION_ID = 0;

    private static NotificationManager mNotificationManager;
    private static Bitmap mWearBitmapAtWork;
    private static Bitmap mWearBitmapLeftWork;
    private static NotificationCompat.Builder mBuilder;

    public static void showAtWork(Context context) {
        init(context);

        mBuilder.setTicker("At work")
                .setContentTitle("At work")
                .setContentText("Work time: " + getWorkTime(context))
                .setSmallIcon(R.drawable.ic_notification_at_work)
                .setAutoCancel(false)
                .extend(new NotificationCompat.WearableExtender().setBackground(mWearBitmapAtWork));

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public static void showLeftWork(Context context) {
        init(context);

        mBuilder.setTicker("Left work")
                .setContentTitle("Left work")
                .setContentText("Work time: " + getWorkTime(context))
                .setSmallIcon(R.drawable.ic_notification_left_work)
                .setAutoCancel(true)
                .extend(new NotificationCompat.WearableExtender().setBackground(mWearBitmapLeftWork));

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public static void clearNotifications(Context context) {
        init(context);

        mNotificationManager.cancelAll();
    }

    private static void init(Context context) {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            int notificationColor = context.getResources().getColor(R.color.primary);

            Intent clickIntent = new Intent(context, MainActivity.class);
            PendingIntent clickPendingIntent = PendingIntent.getActivity(context, 0, clickIntent, 0);

            Intent dismissIntent = new Intent(context, NotificationService.NotificationDismissReceiver.class);
            PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, dismissIntent, 0);

            mBuilder = new NotificationCompat.Builder(context)
                    .setColor(notificationColor)
                    .setContentIntent(clickPendingIntent)
                    .setDeleteIntent(dismissPendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_LOW);

            mWearBitmapAtWork = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_wear_background_at_work);
            mWearBitmapLeftWork = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_wear_background_left_work);
        }
    }

    private static String getWorkTime(Context context) {
        WorkDay workDay = TimeManager.getInstance(context).getWorkDay(System.currentTimeMillis());
        return TimeFormatter.formatElapsedTime(workDay.getWorkTime() / 1000);
    }
}
