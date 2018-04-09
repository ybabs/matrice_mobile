package com.example.rosboxone.uwa.drone;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.rosboxone.uwa.MainActivity;
import com.example.rosboxone.uwa.R;
import com.example.rosboxone.uwa.Utils.MissionConfigDataEncoder;
import com.example.rosboxone.uwa.Utils.MissionConfigDataManager;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;

public class Communication implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = Communication.class.getName();

    private final HandlerThread mThread;
    private final Handler mHandler;
    private Runnable mSender;
    private int mDelay;

    private MissionConfigDataManager missionConfigDataManager = new MissionConfigDataManager();
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


        // * Set ready listener for missionCOnfigDataManager
        missionConfigDataManager.setReadyListener(new MissionDataManagerReadyListener());


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


    private class MissionDataManagerReadyListener implements Runnable {
        @Override
        public void run()
        {
            mSender = new MissionDataSender();
            mHandler.post(mSender);
        }
    }

    private class MissionDataSender implements Runnable
    {
        @Override
        public void run()
        {
            // confirm correct sender is being used
            if(mSender != this)
            {
                return;
            }

            //Retrieve current Data
            MissionConfigDataEncoder data = missionConfigDataManager.getNextMissionData();
            if(data == null)
            {
                return;
            }

            // Send Data
            byte[] bytes = data.getConfigData();
            mFlightController.sendDataToOnboardSDKDevice(bytes, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {

                    if (djiError == null)
                    {
                        Log.d(TAG, "DATA SENT TO ONBOARD DEVICE");
                    }

                    else
                    {
                        Log.e(TAG, djiError.getDescription());
                    }


                }
            });

            mHandler.postDelayed(this, mDelay);

        }
    }

    //Restart Comms Link
    private class DataManagerRestart implements  Runnable
    {
        @Override
        public void run()
        {
            //Send request to restart config dataManager
            missionConfigDataManager.ready();
        }
    }

    public MissionConfigDataManager getMissionConfigDataManager() {
        return missionConfigDataManager;
    }


}
