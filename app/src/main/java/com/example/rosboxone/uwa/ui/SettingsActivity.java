package com.example.rosboxone.uwa.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.rosboxone.uwa.MainActivity;
import com.example.rosboxone.uwa.R;
import com.example.rosboxone.uwa.ros.MatriceFlightDataSubscriberNode;
import com.example.rosboxone.uwa.ros.RosNodeConnection;

public class SettingsActivity extends Activity {



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

        @Override
        public void onResume()
        {
            super.onResume();

        }

        @Override
        public void onPause()
        {
            super.onPause();


        }



    }




}
