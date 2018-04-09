package com.example.rosboxone.uwa.Utils;

public enum MissionEndCommand
{
    AUTO((byte)0x01),

    INITIAL((byte)0x02),

    RC((byte)0x03),

    WAYPOINT((byte)0x04);

    private final byte EndMission;

    private MissionEndCommand(byte EndMission) {
        this.EndMission = EndMission;
    }

}

//TODO change to bytes


