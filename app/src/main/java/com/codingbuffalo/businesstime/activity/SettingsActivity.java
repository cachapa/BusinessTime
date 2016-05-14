package com.codingbuffalo.businesstime.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.codingbuffalo.businesstime.R;
import com.codingbuffalo.businesstime.fragment.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new SettingsFragment())
                    .commit();
        }
    }
}
