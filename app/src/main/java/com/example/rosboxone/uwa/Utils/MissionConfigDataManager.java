package com.example.rosboxone.uwa.Utils;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MissionConfigDataManager {

    private final static String TAG = MissionConfigDataManager.class.getName();

    private final Handler mHandler = new Handler();

    private Runnable mListener;

    private List<MissionConfigDataEncoder> waypointsList = new ArrayList<>();

    private Boolean mReady = false;


    // Set current GPS Data to be sent
    public void setMissionData(MissionConfigDataEncoder missionData)
    {

        waypointsList.add(missionData);

        if(!mReady)
        {
            Log.d(TAG, "Mission Manager Ready");
            mReady = true;


            // Broadcast that Mission manager is ready
            if (mListener != null)
            {
                mHandler.post(mListener);

            }
        }

    }

    // get next Mission Data

    public MissionConfigDataEncoder getNextMissionData()
    {
        MissionConfigDataEncoder dataEncoder = new MissionConfigDataEncoder();

        if(waypointsList.size() > 0)
        {
            for (int i = 0; i < waypointsList.size(); i++) {

                    dataEncoder = waypointsList.get(i);

            }
        }

        return dataEncoder;

    }

    // Send a signal saying it's ready to send data again
    public void ready()
    {
        Log.d(TAG, "Restarting Data Encoder Manager");
        mReady = false;
        waypointsList = null;

    }

    // Is invoked when Manager is ready to send data
    public void setReadyListener(Runnable listener)
    {
        mListener = listener;
    }


}


