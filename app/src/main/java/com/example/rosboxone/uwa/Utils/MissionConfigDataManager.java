package com.example.rosboxone.uwa.Utils;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.Toast;

import com.example.rosboxone.uwa.MainActivity;
//import com.example.rosboxone.uwa.drone.Rotorcraft;
import com.example.rosboxone.uwa.drone.Registration;
import com.google.android.gms.maps.model.LatLng;


import java.util.ArrayList;
import java.util.List;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseProduct;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;

import static com.example.rosboxone.uwa.Utils.DataUtil.FloatToBytes;
import static com.example.rosboxone.uwa.Utils.DataUtil.DoubleToBytes;

//TODO change array size so you can send orientation and mission_end in just bytes

public class MissionConfigDataManager {

    private static final String TAG = MissionConfigDataManager.class.getName();
//    private  final Handler mHandler;
//    private final HandlerThread mThread;


    private List<byte[]>ConfigDataList = new ArrayList<>();


    private static final int ARRAY_SIZE = 22;
    private static final int COMMAND_POSITION = 0;
    private static final int WAYPOINT_LATITUDE = 1;
    private static final int WAYPOINT_LONGITUDE = 9;
    private static final int WAYPOINT_ALTITUDE = 17;
    private static final int NULL_POSITION = 21;


    private static final int PARAM_ARRAY_SIZE = 8;
    private static final int PARAM_COMMAND_POSITION = 0;
    private static final int PARAM_WAYPOINT_SPEED = 1;
    private static final int PARAM_WAYPOINT_ORIENTATION = 5;
    private static final int PARAM_MISSION_END = 6;
    private static final int PARAM_NULL_POSITION = 7;



    private  double mLatitude;
    private  double mLongitude;
    private  float mAltitude;
    private  byte mOrientation;
    private  byte mMissionEnd;
    private  float mSpeed;
    private boolean mReady;
    byte[] dataToSend;


    private FlightController djiFC;




    public MissionConfigDataManager()
    {

        mLatitude = 0L;
        mLongitude = 0L;
        mAltitude = 0L;
        mOrientation = 0x0;
        mSpeed = 0L;
        mMissionEnd = 0x0;

        BaseProduct product = Registration.getProductInstance();
        if (product != null && product.isConnected()) {
            if (product instanceof Aircraft) {
                djiFC = ((Aircraft) product).getFlightController();
            }
        }

    }

    public byte [] getConfigData()
    {
        byte[] configData = new byte [ARRAY_SIZE];
        configData[COMMAND_POSITION] = (byte)0x2f;
        DoubleToBytes(configData, WAYPOINT_LATITUDE, mLatitude);
        DoubleToBytes(configData, WAYPOINT_LONGITUDE, mLongitude);
        FloatToBytes(configData, WAYPOINT_ALTITUDE, mAltitude);
        configData[NULL_POSITION] = (byte)0x0;

        ConfigDataList.add(configData);

        return configData;
    }

    public byte [] getParams()
    {
        byte[] paramConfigData = new byte [PARAM_ARRAY_SIZE];
        paramConfigData[PARAM_COMMAND_POSITION] = (byte)0x4d;
        FloatToBytes(paramConfigData, PARAM_WAYPOINT_SPEED, mSpeed);
        paramConfigData[PARAM_WAYPOINT_ORIENTATION] = mOrientation;
        paramConfigData[PARAM_MISSION_END] = mMissionEnd;
        paramConfigData[PARAM_NULL_POSITION] = (byte)0x0;


        return paramConfigData;
    }



    public void sendMissionData(byte [] data,  FlightController djiFC)
    {

                 if(djiFC != null)
                 {
                      djiFC.sendDataToOnboardSDKDevice(data, new CommonCallbacks.CompletionCallback() {
                                 @Override
                                 public void onResult(DJIError djiError) {

                                     if (djiError == null) {
                                         Log.d(TAG, "Sent Data to Onboard Device");
                                     } else {
                                         Log.e(TAG, djiError.getDescription());
                                         Toast.makeText(MainActivity.getInstance().getApplicationContext(), djiError.getDescription(), Toast.LENGTH_SHORT).show();

                                     }
                                 }
                             });
                         }


                 else
                 {
                     Toast.makeText(MainActivity.getInstance().getApplicationContext(), "NO FC", Toast.LENGTH_LONG).show();
                 }
    }




    public void sendCommand(byte []data , FlightController djiFC)
    {
        if(djiFC!= null)
        {
            djiFC.sendDataToOnboardSDKDevice(data, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if(djiError == null)
                    {

                    }

                    else
                    {
                        Toast.makeText(MainActivity.getInstance().getApplicationContext(), djiError.getDescription(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void sendMissionParam(byte[] data, FlightController djiFC)
    {
        if(djiFC!= null)
        {
            djiFC.sendDataToOnboardSDKDevice(data, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if(djiError == null)
                    {

                    }

                    else
                    {
                        Toast.makeText(MainActivity.getInstance().getApplicationContext(), djiError.getDescription(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public String toString()
    {
        return "Latitude: " + getLatitude() + "\nLongitude: " + getLongitude()
                +"\nAltitude: " + getAltitude() + "\nSpeed: " + getSpeed()
                +"\nOrientation: " + getOrientation() + "\nMissionEnd: "+ getMissionEnd();

    }


    public String getMissionEnd()
    {

        if(mMissionEnd == 1)
        {
            return "HOVER";
        }
        if(mMissionEnd == 2)
        {
            return  "FIRST WAYPOINT";
        }

        if(mMissionEnd == 3)
        {
            return  "RETURN HOME";
        }

        if(mMissionEnd == 4)
        {
            return "AUTO LAND";
        }

        return "No Mission End Behaviour";
    }
    public double getLatitude() {
        return mLatitude;
    }

    public  double getLongitude()
    {
        return mLongitude;
    }

    public  float getAltitude()
    {
        return mAltitude;
    }

    public float getSpeed()
    {
        return mSpeed;
    }

    public String getOrientation()
    {
        if (mOrientation == 1)
        {
            return "AUTO";
        }

        if(mOrientation == 2)
        {
            return "INITIAL";
        }

        if(mOrientation == 3)
        {
            return "RC";
        }

        if(mOrientation == 4)
        {
            return "WAYPOINT";
        }

        return "NO ORIENTATION";
    }

    public void setLatitude(double latitude)
    {
        this.mLatitude = latitude;
    }

    public void setLongitude(double longitude)
    {
        this.mLongitude = longitude;
    }

    public void setSpeed(float speed)
    {
        this.mSpeed = speed;
    }
    public void setAltitude(float altitude)
    {
        this.mAltitude = altitude;
    }

    public void setCourseLock(byte orientation)
    {
        this.mOrientation = orientation;
    }

    public void setMissionEnd(byte missionEndVal)
    {
        this.mMissionEnd = missionEndVal;
    }

    public void clearAllPoints()
    {
        mLatitude = 0;
        mLongitude = 0;
        mAltitude = 0;
        ConfigDataList.clear();
    }






}
