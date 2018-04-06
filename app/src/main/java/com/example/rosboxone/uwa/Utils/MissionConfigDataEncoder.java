package com.example.rosboxone.uwa.Utils;

import com.google.android.gms.maps.model.LatLng;
import static com.example.rosboxone.uwa.Utils.DataUtil.FloatToBytes;
import static com.example.rosboxone.uwa.Utils.DataUtil.DoubleToBytes;

//TODO change array size so you can send orientation and mission_end in just bytes

public class MissionConfigDataEncoder {



    private static final int ARRAY_SIZE = 27;
    private static final int WAYPOINT_LATITUDE = 0;
    private static final int WAYPOINT_LONGITUDE = 8;
    private static final int WAYPOINT_ALTITUDE = 16;
    private static final int WAYPOINT_ORIENTATION = 17;
    private static final int WAYPOINT_SPEED = 21;
    private static final int MISSION_END = 25;
    private static final int NULL_POSITION = 26;


    private  double mLatitude;
    private  double mLongitude;
    private  float mAltitude;
    private  byte mOrientation;
    private  byte mMissionEnd;
    private  float mSpeed;

    public MissionConfigDataEncoder()
    {
        mLatitude = 0L;
        mLongitude = 0L;
        mAltitude = 0L;
        mOrientation = 0x0;
        mSpeed = 0L;
        mMissionEnd = 0x0;
    }


    public MissionConfigDataEncoder(LatLng location, float altitude,float speed, byte orientation, byte mission_end )
    {
        mLatitude = (float)location.latitude;
        mLongitude = (float)location.longitude;

        mAltitude = altitude;
        mOrientation = orientation;
        mMissionEnd = mission_end;
        mSpeed = speed;

    }

    public byte [] getConfigData()
    {
        byte[] configData = new byte [ARRAY_SIZE];

        DoubleToBytes(configData, WAYPOINT_LATITUDE, mLatitude);
        DoubleToBytes(configData, WAYPOINT_LONGITUDE, mLongitude);
        FloatToBytes(configData, WAYPOINT_ALTITUDE, mAltitude);
        configData[WAYPOINT_ORIENTATION] = mOrientation;
        FloatToBytes(configData, WAYPOINT_SPEED, mSpeed);
        configData[MISSION_END] = mMissionEnd;
        configData[NULL_POSITION] = (byte)0x0;

        return configData;
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






}
