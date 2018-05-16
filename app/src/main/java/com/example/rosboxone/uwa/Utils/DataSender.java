package com.example.rosboxone.uwa.Utils;

import android.os.HandlerThread;
import android.util.Log;
import android.widget.Toast;

import com.example.rosboxone.uwa.MainActivity;
import com.example.rosboxone.uwa.drone.Registration;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseProduct;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;

public class DataSender
{

    private static String TAG = DataSender.class.getName();
    private final Handler mHandler;
    private final HandlerThread mHandlerThread;


    private  FlightController djiFC;
    private Runnable mSenderRunnable;
    private MissionConfigDataManager missionConfigDataManager;




    public DataSender()
    {
        //* Thread for comms is this
        mHandlerThread = new HandlerThread("Comms");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());

        mSenderRunnable = new PayloadSender();

        BaseProduct product = Registration.getProductInstance();
        if (product != null && product.isConnected()) {
            if (product instanceof Aircraft) {
                djiFC = ((Aircraft) product).getFlightController();
            }
        }

    }

    private class PayloadSender implements Runnable
    {
        @Override
        public void run()
        {
            if(mSenderRunnable != this)
            {
                return;
            }

            byte [] bytes = missionConfigDataManager.getConfigData();
            djiFC.sendDataToOnboardSDKDevice(bytes, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {

                    mHandler.postDelayed(mSenderRunnable, 50);

                }
            });

            mHandler.postDelayed(this, 50);

        }
    }






}




