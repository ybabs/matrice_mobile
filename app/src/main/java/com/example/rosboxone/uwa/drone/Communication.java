package com.example.rosboxone.uwa.drone;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;

import com.example.rosboxone.uwa.MainActivity;
import com.example.rosboxone.uwa.R;

import dji.sdk.flightcontroller.FlightController;

public class Communication implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = Communication.class.getName();

    private final HandlerThread mThread;
    private final Handler mHandler;
    private Runnable mSender;
    private int mDelay;

    private Rotorcraft rotorcraft = new Rotorcraft();
    private String mKey;
    private FlightController mFlightController;



    public Communication()
    {
        Context context = MainActivity.getInstance().getApplicationContext();

        mFlightController = rotorcraft.getFlightControllerInstance();

        mKey = context.getResources().getString(R.string.freq_update_key);

        // Create Handler Thread
        mThread = new HandlerThread("Comms");
        mThread.start();
        mHandler = new Handler(mThread.getLooper());

        MainActivity mainActivity = MainActivity.getInstance();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mainActivity);

        onSharedPreferenceChanged(sharedPreferences, mKey);

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        // Callback to receive external Data from the drone....
        if(mFlightController != null) {
            mFlightController.setOnboardSDKDeviceDataCallback(new FlightController.OnboardSDKDeviceDataCallback() {
                @Override
                public void onReceive(byte[] bytes) {

                }
            });
        }


    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key)
    {
        //Confirm Set frequency
        if(key != mKey)
        {
            return;
        }

        // Update Frequency
        double frequency = Integer.parseInt(preferences.getString(mKey, "20"));
        mDelay = (int) (1000.0/ frequency);
    }




}
