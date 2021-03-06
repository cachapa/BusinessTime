package net.cachapa.businesstime.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;

import net.cachapa.businesstime.manager.TimeManager;
import net.cachapa.businesstime.util.NotificationHelper;

public class NotificationService extends Service {
    private static final String SHOW_NOTIFICATIONS = "show_notifications";

    private static NotificationService mInstance;

    private Handler mTickHandler;
    private Runnable mTicker = new Runnable() {
        @Override
        public void run() {
            updateNotification();
        }
    };

    public static void handleNotification(Context context) {
        // Check if the user allows notifications
        if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SHOW_NOTIFICATIONS, true)) {
            stopService();
            NotificationHelper.clearNotifications(context);
            return;
        }

        TimeManager timeManager = TimeManager.getInstance(context);

        if (timeManager.isAtWork(System.currentTimeMillis())) {
            // Start service to handle ongoing notification
            Intent intent = new Intent(context, NotificationService.class);
            context.startService(intent);
        } else {
            // Stop service
            stopService();

            // Show "left work" notification
            NotificationHelper.showLeftWork(context);
        }
    }

    public static void stopService() {
        if (mInstance != null) {
            mInstance.stop();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mInstance = this;

        mTickHandler = new Handler();
        updateNotification();

        return START_STICKY;
    }

    private void stop() {
        // Stop notification updates
        mTickHandler.removeCallbacks(mTicker);

        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    private void updateNotification() {
        NotificationHelper.showAtWork(this);

        // Schedule another update in one minute
        mTickHandler.postDelayed(mTicker, DateUtils.MINUTE_IN_MILLIS);
    }

    public static class NotificationDismissReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopService();
        }
    }
}
