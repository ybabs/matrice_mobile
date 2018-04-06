package com.example.rosboxone.uwa.Utils;

public enum OrientationCommand {

    HOVER ((byte)0x01),

    FIRST_WAYPOINT ((byte)0x02),

    RETURN_HOME((byte)0x03),

    AUTO_LAND((byte)0x04);

    private  final byte orientation;

    private OrientationCommand (byte orientation)
    {
        this.orientation = orientation;
    }


}
//TODO change to bytes


//public enum OrientationCommand {
//
//    HOVER (1),
//
//    FIRST_WAYPOINT (2),
//
//    RETURN_HOME(3),
//
//    AUTO_LAND(4);
//
//    private  final float orientation;
//
//    private OrientationCommand (float orientation)
//    {
//        this.orientation = orientation;
//    }
//
//
//}
//
