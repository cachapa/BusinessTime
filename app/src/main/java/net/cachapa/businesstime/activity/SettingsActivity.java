package net.cachapa.businesstime.activity;

import android.os.Bundle;

import net.cachapa.businesstime.R;
import net.cachapa.businesstime.fragment.SettingsFragment;

import androidx.appcompat.app.AppCompatActivity;

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
