package com.example.rosboxone.uwa.ros;

import android.util.Log;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;



import sensor_msgs.NavSatFix;

public class MatriceFlightDataSubscriberNode extends AbstractNodeMain
{

    private static  final String TAG = MatriceFlightDataSubscriberNode.class.getName();
    private static final String NODE_NAME = "m100_flight_data_subscriber";


    private Subscriber<sensor_msgs.NavSatFix> gpsSubscriber;

    @Override
    public GraphName getDefaultNodeName()
    {
        return GraphName.of(NODE_NAME);
    }

    @Override
    public void onStart(ConnectedNode connectedNode)
    {
        final org.apache.commons.logging.Log log = connectedNode.getLog();
        gpsSubscriber = connectedNode.newSubscriber("dji_sdk/gps_position", NavSatFix._TYPE );
        gpsSubscriber.addMessageListener(new MessageListener<NavSatFix>() {
            @Override
            public void onNewMessage(NavSatFix navSatFix) {
                Log.i(TAG, "Received GPS FIX from ROS: " + navSatFix.toString());
            }
        });

    }

}
