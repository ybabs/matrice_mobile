package com.example.rosboxone.uwa.Utils;

import android.os.Handler;
import android.util.Log;

public class MissionConfigDataManager {

    private final static String TAG = MissionConfigDataManager.class.getName();

    private final Handler mHandler = new Handler();

    private Runnable mListener;

    private MissionConfigDataEncoder Waypoints;

    private Boolean mReady = false;


    // Set current GPS Data to be sent
    public void setMissionData(MissionConfigDataEncoder missionData)
    {
        Waypoints = missionData;

    }

    // get next Mission Data

    public MissionConfigDataEncoder getNextMissionData()
    {
        MissionConfigDataEncoder dataEncoder = new MissionConfigDataEncoder();

        if(Waypoints != null)
        {
            dataEncoder = Waypoints;
            Waypoints = null;
        }


        return dataEncoder;

    }

    // Send a signal saying it's ready to send data again
    public void ready()
    {
        Log.d(TAG, "Restarting Data Encoder Manager");
        mReady = false;
        Waypoints = null;

    }

    // Is invoked when Manager is ready to send data
    public void setReadyListener(Runnable listener)
    {
        mListener = listener;
    }
}


