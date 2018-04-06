package com.example.rosboxone.uwa.drone;


/**
 * Created by rosboxone on 08/03/18.
 */

import android.content.Context;
import android.widget.Toast;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseProduct;
import  dji.sdk.flightcontroller.FlightController;
import dji.sdk.battery.Battery;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class Rotorcraft {


    private FlightController mFlightController;
    private Battery mBattery;
    private  BaseProduct mProduct;
    private  Aircraft mAircraft;


    // Constructor for the drone
    public Rotorcraft()
    {


    }


    public void initFlightController()
    {
        mProduct = getProductInstance();
        if (mProduct != null && mProduct.isConnected()) {
            if (mProduct instanceof Aircraft) {
                mFlightController = ((Aircraft) mProduct).getFlightController();
                mAircraft = (Aircraft) mProduct;

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