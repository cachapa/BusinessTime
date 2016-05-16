package net.cachapa.businesstime.repository;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;

import net.cachapa.businesstime.manager.TimeManager;

public class WifiDetector extends BroadcastReceiver {
    private static final String PREFS_SSID = "monitor_ssid";

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                String ssid = null;

                if (networkInfo.isConnected()) {
                    WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);

                    ssid = wifiInfo.getSSID();
                    ssid = ssid.substring(1, ssid.length() - 1);

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    String prefSsid = prefs.getString(PREFS_SSID, null);

                    if (!ssid.equals(prefSsid)) {
                        return;
                    }

                    TimeManager.getInstance(context).startWork();
                    Log.d("Wifi", ssid + " connected");
                } else {
                    TimeManager.getInstance(context).stopWork();
                }
            }
        }
    }
}
