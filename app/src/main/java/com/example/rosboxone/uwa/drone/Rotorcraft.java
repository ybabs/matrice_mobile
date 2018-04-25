package com.example.rosboxone.uwa.drone;


/**
 * Created by rosboxone on 08/03/18.
 */


import android.os.Handler;
import android.widget.Toast;

import com.example.rosboxone.uwa.MainActivity;

import java.util.logging.LogRecord;

import dji.common.flightcontroller.FlightControllerState;
import dji.sdk.base.BaseProduct;
import  dji.sdk.flightcontroller.FlightController;
import dji.sdk.battery.Battery;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.common.battery.BatteryState;

public class Rotorcraft {


    private FlightController mFlightController;
    private Battery mBattery;
    private  BaseProduct mProduct;
    private  Aircraft mAircraft;
    private FlightControllerState.Callback flightControllerStateCallback;




    // Constructor for the drone
    public Rotorcraft()
    {
        //Start Drone comms here
       // communication = new Communication();


    }


    public void initFlightController()
    {
        mProduct = getProductInstance();
        if (mProduct != null && mProduct.isConnected()) {
            if (mProduct instanceof Aircraft) {
                mFlightController = ((Aircraft) mProduct).getFlightController();
                mAircraft = (Aircraft) mProduct;
                mFlightController.setStateCallback(flightControllerStateCallback);
                mFlightController.setOnboardSDKDeviceDataCallback(onboardSDKDeviceDataCallback);

            }
        }

    }

    public void returnBattery()
    {
        BaseProduct product = getProductInstance();
        if (mBattery == null)
        {
            if(product instanceof Aircraft) {
                mBattery = ((Aircraft) product).getBattery();
            }
        }


    }

    
    public FlightController getFlightControllerInstance()
    {
        if(mFlightController == null)
        {
            return null;
        }

        return mFlightController;

    }

    public  Aircraft getAircraftInstance()
    {
        return mAircraft;

    }


    public Battery getBatteryInstance()
    {
        return mBattery;

    }

//    public float getBatteryTemp() {
//        return batteryTemp;
//    }
//
//    public int getBatteryChargeRemaining()
//    {
//        return batteryChargeRemaining;
//    }
//
//    public int getBatteryVoltage()
//    {
//        return batteryVoltage;
//
//    }


    private FlightController.OnboardSDKDeviceDataCallback onboardSDKDeviceDataCallback = new FlightController.OnboardSDKDeviceDataCallback() {
        @Override
        public void onReceive(byte[] bytes) {

            Toast.makeText(MainActivity.getInstance().getApplicationContext(), "I got some data", Toast.LENGTH_LONG).show();

        }
    };


    public  synchronized BaseProduct getProductInstance()
    {
        if (null == mProduct)
        {
            mProduct = DJISDKManager.getInstance().getProduct();
        }

        return mProduct;
    }

    public BaseProduct getBaseProduct() {
        return mProduct;
    }

    public void onProductConnectionChanged()
    {
        initFlightController();
    }





}