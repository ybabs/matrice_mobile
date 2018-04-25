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



    public static class SettingsScreen extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
    {

        @Override
        public void onCreate(final Bundle savedInstanceBundle)
        {
            super.onCreate(savedInstanceBundle);
            addPreferencesFromResource(R.xml.settings);

            onSharedPreferenceChanged(null, "");
            bindSummaryToValue(findPreference("address"));
            bindSummaryToValue(findPreference("frequency"));
            bindSummaryToValue(findPreference("port"));
//


        }

        @Override
        public void onResume()
        {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause()
        {
            super.onPause();
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);

        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            Context context = MainActivity.getInstance().getApplicationContext();

            EditTextPreference addressEP = (EditTextPreference) findPreference(context.getResources().getString(R.string.ip_address)) ;
            addressEP.setSummary("");

            Toast.makeText(context, "Preferences Changed", Toast.LENGTH_LONG).show();

        }


    }

    private static  void bindSummaryToValue(Preference preference)
    {
        preference.setOnPreferenceChangeListener(listener);
        listener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), ""));
    }

    private  static Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {


            String stringValue = newValue.toString();
            if(preference instanceof EditTextPreference)
            {
                preference.setSummary(stringValue);

            }
            return false;


        }
    };


}
