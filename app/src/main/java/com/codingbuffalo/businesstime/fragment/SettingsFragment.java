package com.codingbuffalo.businesstime.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.codingbuffalo.businesstime.R;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.main_preferences);
    }
}
