package com.codingbuffalo.businesstime.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.codingbuffalo.businesstime.R;
import com.codingbuffalo.businesstime.activity.MainActivity;
import com.codingbuffalo.businesstime.manager.TimeManager;
import com.codingbuffalo.businesstime.model.WorkDay;

public class NotificationHelper {
	private static final int NOTIFICATION_ID = 0;

	private static NotificationManager mNotificationManager;
	private static int mNotificationColor = -1;
	private static NotificationCompat.Builder mBuilder;


	public static void showAtWork(Context context) {
		init(context);

		mBuilder.setTicker("At work")
				.setContentTitle("At work")
				.setContentText("Work time: " + getWorkTime(context))
				.setSmallIcon(R.drawable.ic_notification_at_work)
				.setAutoCancel(false)
				.setOngoing(true);

		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}

	public static void showLeftWork(Context context) {
		init(context);

		mBuilder.setTicker("Left work")
				.setContentTitle("Left work")
				.setContentText("Work time: " + getWorkTime(context))
				.setSmallIcon(R.drawable.ic_notification_left_work)
				.setAutoCancel(true)
				.setOngoing(false);

		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}

	public static void clearNotifications(Context context) {
		init(context);

		mNotificationManager.cancelAll();
	}

	private static void init(Context context) {
		if (mNotificationManager == null) {
			mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

			mNotificationColor = context.getResources().getColor(R.color.primary);

			Intent resultIntent = new Intent(context, MainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, 0);
			mBuilder = new NotificationCompat.Builder(context)
					.setColor(mNotificationColor)
					.setContentIntent(pendingIntent)
					.setPriority(NotificationCompat.PRIORITY_LOW)
					.extend(new NotificationCompat.WearableExtender()
							.setBackground(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_wear_background)));
		}
	}

	private static String getWorkTime(Context context) {
		WorkDay workDay = TimeManager.getInstance(context).getWorkDay(System.currentTimeMillis());
		return TimeFormatter.formatElapsedTime(workDay.getWorkTime() / 1000);
	}
}
