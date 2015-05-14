package com.codingbuffalo.businesstime.application;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.codingbuffalo.businesstime.manager.TimeManager;
import com.codingbuffalo.businesstime.service.NotificationService;

public class BusinessTimeApplication extends Application implements TimeManager.OnTimeListener, SharedPreferences.OnSharedPreferenceChangeListener {
	@Override
	public void onCreate() {
		super.onCreate();
		TimeManager.getInstance(this).addOnTimeListener(this);
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onTimeModified() {
		NotificationService.handleNotification(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		NotificationService.handleNotification(this);
	}
}
