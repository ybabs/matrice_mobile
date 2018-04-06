package com.example.rosboxone.uwa.ui;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.example.rosboxone.uwa.R;

public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Create Fragment with settings
        PreferenceFragment settings = new SettingsScreen();
        getFragmentManager().beginTransaction().replace(android.R.id.content, settings).commit();
    }

    public static class SettingsScreen extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceBundle)
        {
            super.onCreate(savedInstanceBundle);
            addPreferencesFromResource(R.xml.settings);
        }

    }

}
