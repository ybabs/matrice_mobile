package com.example.rosboxone.uwa;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.rosboxone.uwa.Utils.MathUtil;
import com.example.rosboxone.uwa.Utils.MissionConfigDataManager;
import com.example.rosboxone.uwa.drone.Registration;
//import com.example.rosboxone.uwa.drone.Rotorcraft;
import com.example.rosboxone.uwa.ros.MatriceFlightDataSubscriberNode;
import com.example.rosboxone.uwa.ros.RosNodeConnection;
import com.example.rosboxone.uwa.ui.SettingsActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import dji.common.battery.BatteryState;
import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.common.flightcontroller.FlightControllerState;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleMap.OnMapClickListener, OnMapReadyCallback{

    private static MainActivity mainActivityInstance;

    private static final String[] REQUIRED_PERMISSION_LIST = new String[]{
            Manifest.permission.VIBRATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
    };

    private AtomicBoolean isRegistrationInProgress = new AtomicBoolean(false);
    private static final int REQUEST_PERMISSION_CODE = 12345;
    private List<String> missingPermission = new ArrayList<>();
    public static final String FLAG_CONNECTION_CHANGE = "dji_sdk_connection_change";


    private GoogleMap googleMap;
    private Handler mHandler;
    ArrayList<Marker> markerArrayList = new ArrayList<>();
    private static BaseProduct mProduct;

    private float mAltitude = 10.0f;
    private float mSpeed = 2.0f;


    private static final String TAG = MainActivity.class.getName();

    MissionConfigDataManager missionConfigDataManager;

    private LatLng homeLatLng;

    // Buttons on the HomeScreen;
    private LinearLayout takeoff, land, goHome;
    private Button start, abort;
    private LinearLayout newMission;

    //Buttons on the mission config page
    private Button missionOk, missionCancel;

    // Seekbars in flight config page
    private SeekBar speedSeekbar;
    private SeekBar heightSeekbar;

    private RelativeLayout flightConfigPanel;


    // radioGroups on Flight config page
    private RadioGroup orientationRadioGroup;
    private RadioGroup missionEndRadioGroup;

    //Telemetry Status
    private ImageView batteryStatusImageView;
    private ImageView rcSignalImageView;
    private ImageView gpsSignalImageView;



    //Homepage TextViews
    private TextView droneStatusTextView;
    private TextView satelliteCountTextView;
    private TextView batteryLevelTextView;
    private TextView latitudeTextView;
    private TextView longitudeTextView;
    private  TextView altitudeTextView;
    private TextView homeDistanceTextView;
    private TextView verticalspeedtextView;
    private TextView horizontalspeedtextView;

   private double droneLocationLatitude;
   private double droneLocationLongitude;
   private double droneLocationAltitude;
   private double droneDistanceToHome;
   private double droneVerticalSpeed;
   private double droneHorizontalSpeed;

    private TextView speedSeekbarTextView;
    private TextView heightSeekbarTextView;
    private RelativeLayout mainActivityLayout;

    private int batteryVoltage;
    private float batteryTemp;
    private int batteryChargeRemaining;
    private int satelliteCount = -1;


    // Homepage Hamburger Menu
    private ImageView leftMenu;





    //Lists to store Mission Waypoints
    private List<LatLng> missionWaypoints = new ArrayList<>();

    //Hamburger Menu

    private Button rosSettingButton;
    private RelativeLayout hamburgerRelativeLayout;

    private FusedLocationProviderClient mFusedLocationClient;
    SupportMapFragment mapFragment;

//    private Rotorcraft rotorcraft;
    private RosNodeConnection rosNodeConnection;
    private MatriceFlightDataSubscriberNode matriceFlightDataSubscriberNode;
    private FlightController mFlightController;



    public static MainActivity getInstance()
    {
        return mainActivityInstance;
    }

    public MainActivity()
    {
        if (mainActivityInstance == null)
        {
            mainActivityInstance = this;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
//        if(rotorcraft == null)
//        {
//            rotorcraft = new Rotorcraft();
//            mProduct = rotorcraft.getBaseProduct();
//        }

        //flightController = rotorcraft.getFlightControllerInstance();
        missionConfigDataManager = new MissionConfigDataManager();
        rosNodeConnection = new RosNodeConnection();
        matriceFlightDataSubscriberNode = new MatriceFlightDataSubscriberNode();


        RosNodeConnection.getRosNodeInstance().launchNode(matriceFlightDataSubscriberNode);


        super.onCreate(savedInstanceState);



        setWindowAttributes();

        // When the compile and target version is higher than 22, please request the following permission at runtime to ensure the SDK works well.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermissions();
        }

        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Registration.FLAG_CONNECTION_CHANGE);
        registerReceiver(mReceiver, filter);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mHandler = new Handler(Looper.getMainLooper());


        initFlightController();
        initUI();
        initOnClickListener();



    }

    //TODO Testing to clean up later.
    private void initFlightController()
    {

        BaseProduct product = Registration.getProductInstance();
        if (product != null && product.isConnected()) {
            if (product instanceof Aircraft) {
                mFlightController = ((Aircraft) product).getFlightController();
            }
        }

        if (mFlightController != null) {

            updateFlightData();
            addCallback();
            //updateBatteryStatus();
        }


    }
//    //TODO Use Data sent back from the Drone on ROS to update Battery Voltage;
//    private void  updateBatteryStatus() {
//
//
//        final Handler mHandler = new Handler();
//
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (mProduct != null && mProduct.isConnected()) {
//                    if (mProduct instanceof Aircraft) {
//                        mProduct.getBattery().setStateCallback(new BatteryState.Callback() {
//                            @Override
//                            public void onUpdate(BatteryState batteryState) {
//
//                                batteryVoltage = batteryState.getVoltage();
//                                batteryTemp = batteryState.getTemperature();
//                                //batteryChargeRemaining = batteryState.getChargeRemaining();
//                                updateBatteryImageView();
//
//                            }
//                        });
//                    }
//                }
//
//                mHandler.postDelayed(this, 1000);
//
//            }
//        }, 1000);
//
//
//    }




    private void addCallback() {
        mFlightController.setOnboardSDKDeviceDataCallback(new FlightController.OnboardSDKDeviceDataCallback() {
            @Override
            public void onReceive(byte[] bytes) {


                if(bytes[0] == 0x01)
                {
                    // Do velocity stuff here
                }

                if(bytes[0] == 0x04)
                {
                    // Do height Stuff here
                }

                if(bytes[0] == 0x02)
                {
                    // Battery Stuff should be set here
                    batteryChargeRemaining = bytes[1];
                    updateBatteryImageView();
                    //showToast("Battery: " + batteryChargeRemaining);
                }

                if(bytes[0] == 0x03)
                {

                    // GPS Health here.
                     satelliteCount = bytes[1];
                     //showToast("GPS Health:" + satelliteCount);
                     runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             satelliteCountTextView.setText(String.valueOf(satelliteCount));

                         }
                     });

                }

//                if(bytes[0] == 0x05)
//                {
//                    float Alt = bytes[3];
//                    showToast("Alt: " + Alt);
//                }


            }
        });
    }

    // Decided not to use the data from the onboard SDK for this bit.
    private void updateFlightData()
    {
        mFlightController.setStateCallback(new FlightControllerState.Callback() {
            @Override
            public void onUpdate(@NonNull FlightControllerState flightControllerState) {
                droneLocationLatitude = flightControllerState.getAircraftLocation().getLatitude();
                droneLocationLongitude = flightControllerState.getAircraftLocation().getLongitude();
                droneLocationAltitude = flightControllerState.getAircraftLocation().getAltitude();
                droneDistanceToHome = MathUtil.CoordinateToDistanceConverter(flightControllerState.getHomeLocation().getLatitude(),
                                      flightControllerState.getHomeLocation().getLongitude(), flightControllerState.getAircraftLocation().getLatitude(),
                                      flightControllerState.getAircraftLocation().getLongitude());

                double secondValue = MathUtil.LL2Distance(flightControllerState.getHomeLocation().getLatitude(),
                        flightControllerState.getHomeLocation().getLongitude(), flightControllerState.getAircraftLocation().getLatitude(),
                        flightControllerState.getAircraftLocation().getLongitude());

                //showToast("Distance = " + droneDistanceToHome + " vs" + secondValue);
                droneVerticalSpeed = (int) (flightControllerState.getVelocityZ() * 10) == 0 ? 0.0000f : (-1.0) * flightControllerState.getVelocityZ();
                droneHorizontalSpeed = MathUtil.computeScalarVelocity(flightControllerState.getVelocityX(), flightControllerState.getVelocityY());


            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                homeDistanceTextView.setText(String.valueOf(droneDistanceToHome));
                horizontalspeedtextView.setText(String.valueOf(droneHorizontalSpeed));
                verticalspeedtextView.setText(String.valueOf(droneVerticalSpeed));
            }
        });




    }

    private void initOnClickListener()
    {
        newMission.setClickable(true);

        // Make hamburger Menu Clickable
        leftMenu.setClickable(true);
        land.setClickable(true);
        takeoff.setClickable(true);
        goHome.setClickable(true);


        leftMenu.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hamburgerRelativeLayout.setVisibility(View.VISIBLE);

            }
        });

        newMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                flightConfigPanel.setVisibility(view.VISIBLE);

            }
        });


        land.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                byte [] LAND_CMD = {0x03};
                missionConfigDataManager.sendCommand(LAND_CMD, mFlightController);

            }
        });

        takeoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte [] TAKEOFF_CMD = {0x01};
                missionConfigDataManager.sendCommand(TAKEOFF_CMD, mFlightController);
            }
        });

        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte [] GOHOME_CMD = {0x02};
                missionConfigDataManager.sendCommand(GOHOME_CMD, mFlightController);

            }
        });
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume");

        super.onResume();
        initFlightController();
        rosNodeConnection.registerPreferencesChangeListener();
//        updateBatteryStatus();
//        updateSatelliteCount();


    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause");

        super.onPause();
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

    public void onReturn(View view){
        Log.e(TAG, "onReturn");
        this.finish();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        unregisterReceiver(mReceiver);
        rosNodeConnection.unregisterPreferencesChangeListener();
        super.onDestroy();
    }

    private void setWindowAttributes()
    {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                |View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                |View.SYSTEM_UI_FLAG_FULLSCREEN
                |View.SYSTEM_UI_FLAG_IMMERSIVE
                |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getWindow().getAttributes().systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
    }

    private void checkAndRequestPermissions() {
        // Check for permissions
        for (String eachPermission : REQUIRED_PERMISSION_LIST) {
            if (ContextCompat.checkSelfPermission(this, eachPermission) != PackageManager.PERMISSION_GRANTED) {
                missingPermission.add(eachPermission);
            }
        }
        // Request for missing permissions
        if (missingPermission.isEmpty()) {
            startSDKRegistration();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showToast("Need to grant the permissions!");
            ActivityCompat.requestPermissions(this,
                    missingPermission.toArray(new String[missingPermission.size()]),
                    REQUEST_PERMISSION_CODE);
        }

    }

    /**
     * Result of runtime permission request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check for granted permission and remove from missing list
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = grantResults.length - 1; i >= 0; i--) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    missingPermission.remove(permissions[i]);
                }
            }
        }
        // If there is enough permission, we will start the registration
        if (missingPermission.isEmpty()) {
            startSDKRegistration();
        } else {
            showToast("Missing permissions!!!");
        }
    }

    private void startSDKRegistration() {
        if (isRegistrationInProgress.compareAndSet(false, true)) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    showToast("registering, pls wait...");
                    DJISDKManager.getInstance().registerApp(MainActivity.this.getApplicationContext(), new DJISDKManager.SDKManagerCallback() {
                        @Override
                        public void onRegister(DJIError djiError) {
                            if (djiError == DJISDKError.REGISTRATION_SUCCESS) {
                                showToast("Register Success");
                                DJISDKManager.getInstance().startConnectionToProduct();
                            } else {
                                showToast("Register sdk fails, please check the bundle id and network connection!");
                            }
                            Log.v(TAG, djiError.getDescription());
                        }

                        @Override
                        public void onProductChange(BaseProduct oldProduct, BaseProduct newProduct) {

                            mProduct= newProduct;
                            if(mProduct != null) {
                                mProduct.setBaseProductListener(mDJIBaseProductListener);

                            }

                            if(mProduct == null)
                            {
                                return;
                            }

                            if(mProduct.isConnected())
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showToast("The Aircraft is connected");
                                        droneStatusTextView.setText(Registration.getProductInstance().getModel().toString());

                                    }
                                });

                            }


                            notifyStatusChange();
                        }
                    });
                }
            });
        }
    }

    private BaseProduct.BaseProductListener mDJIBaseProductListener = new BaseProduct.BaseProductListener() {
        @Override
        public void onComponentChange(BaseProduct.ComponentKey key, BaseComponent oldComponent, BaseComponent newComponent) {
            if(newComponent != null) {
                newComponent.setComponentListener(mDJIComponentListener);
            }
            notifyStatusChange();
        }
        @Override
        public void onConnectivityChange(boolean isConnected) {
            notifyStatusChange();
            if(isConnected)
            {
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       droneStatusTextView.setText(Registration.getProductInstance().getModel().toString());

                   }
               });
                showToast("The Aircraft is connected");
            }

            else
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("The Aircraft has been disconnected");
                        droneStatusTextView.setText("Aircraft is not connected");
                        satelliteCountTextView.setText("N/A");
                        latitudeTextView.setText("N/A");
                        longitudeTextView.setText("N/A");
                        altitudeTextView.setText("N/A");
                        homeDistanceTextView.setText("N/A");
                        verticalspeedtextView.setText("N/A");
                        horizontalspeedtextView.setText("N/A");

                    }
                });
            }


        }
    };
    private BaseComponent.ComponentListener mDJIComponentListener = new BaseComponent.ComponentListener() {
        @Override
        public void onConnectivityChange(boolean isConnected) {
            notifyStatusChange();
        }
    };

    private void notifyStatusChange() {
        mHandler.removeCallbacks(updateRunnable);
        mHandler.postDelayed(updateRunnable, 500);
    }

    private Runnable updateRunnable = new Runnable() {

        @Override
        public void run() {
            Intent intent = new Intent(FLAG_CONNECTION_CHANGE);
            sendBroadcast(intent);
        }
    };

    private void showToast(final String toastMsg) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();
            }
        });

    }


    private void initUI()
    {



        leftMenu = (ImageView) findViewById(R.id.hamburger_menu);
        takeoff = (LinearLayout) findViewById(R.id.takeoff);
        land = (LinearLayout) findViewById(R.id.land);
        goHome = (LinearLayout) findViewById(R.id.go_home);
        newMission = (LinearLayout)findViewById(R.id.new_mission);


        start = (Button) findViewById(R.id.start_btn);
        abort = (Button) findViewById(R.id.abort_btn);
        newMission = (LinearLayout) findViewById(R.id.new_mission);

        mainActivityLayout = (RelativeLayout) findViewById(R.id.main_relativelayout);
        flightConfigPanel = (RelativeLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.mission_configuration, null);
        RelativeLayout.LayoutParams missionParams = new RelativeLayout.LayoutParams(500, ViewGroup.LayoutParams.MATCH_PARENT);
        missionParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        mainActivityLayout.addView(flightConfigPanel, missionParams);
        flightConfigPanel.setVisibility(View.GONE);

        heightSeekbar = (SeekBar) flightConfigPanel.findViewById(R.id.height_param_seekbar);
        speedSeekbar = (SeekBar) flightConfigPanel.findViewById(R.id.speed_param_seekbar);
        missionOk = (Button) flightConfigPanel.findViewById(R.id.config_ok_btn);
        missionCancel= (Button)flightConfigPanel.findViewById(R.id.config_cancel_btn);

        orientationRadioGroup = (RadioGroup)flightConfigPanel.findViewById(R.id.orientation_rg);
        missionEndRadioGroup = (RadioGroup)flightConfigPanel.findViewById(R.id.mission_end_action_rg);

        gpsSignalImageView = (ImageView)findViewById(R.id.gps_signal);
        batteryStatusImageView = (ImageView)findViewById(R.id.battery_status);
        batteryLevelTextView = (TextView)findViewById(R.id.battery_level_text);



        droneStatusTextView = (TextView)findViewById(R.id.drone_Status);
        satelliteCountTextView = (TextView)findViewById(R.id.satellite_count_text);
        latitudeTextView = (TextView)findViewById(R.id.lat_text);
        longitudeTextView = (TextView) findViewById(R.id.lon_text);
        altitudeTextView = (TextView) findViewById(R.id.altitude_text);
        homeDistanceTextView = (TextView)findViewById(R.id.home_distance_text);
        verticalspeedtextView = (TextView)findViewById(R.id.v_speed_text);
        horizontalspeedtextView = (TextView) findViewById(R.id.h_speed_text);

        speedSeekbarTextView = (TextView)flightConfigPanel.findViewById(R.id.speed_param_text);
        heightSeekbarTextView= (TextView)flightConfigPanel.findViewById(R.id.height_param_text);


        hamburgerRelativeLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.settings_configuration, null);
        rosSettingButton = (Button) hamburgerRelativeLayout.findViewById(R.id.ros_settings_button);
        RelativeLayout.LayoutParams parameters = new RelativeLayout.LayoutParams(500, ViewGroup.LayoutParams.MATCH_PARENT);
        parameters.addRule(RelativeLayout.ALIGN_PARENT_START);
        hamburgerRelativeLayout.setElevation(Integer.MAX_VALUE);
        hamburgerRelativeLayout.setLayoutParams(parameters);
        mainActivityLayout.addView(hamburgerRelativeLayout);
        hamburgerRelativeLayout.setVisibility(View.GONE);

        start.setOnClickListener(this);
        abort.setOnClickListener(this);
        missionOk.setOnClickListener(this);
        missionCancel.setOnClickListener(this);
        rosSettingButton.setOnClickListener(this);

        heightSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                heightSeekbarTextView.setText(String.format(Locale.UK, "Height:  %d m", progress + 1));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        speedSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                speedSeekbarTextView.setText(String.format(Locale.UK, "Speed: %d m/s", progress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

       // updateBatteryStatus();
        //updateSatelliteCount();



    }

    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //rotorcraft.onProductConnectionChanged();
            onProductConnectionChange();

        }
    };

    private void onProductConnectionChange()
    {
        initFlightController();

    }


    private void setUpMap()
    {
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {

                markWayPoint(point);

                if(point != null) {

                    missionConfigDataManager.setLatitude(point.latitude);
                    missionConfigDataManager.setLongitude(point.longitude);
                    missionConfigDataManager.setAltitude(mAltitude);
                    missionConfigDataManager.setSpeed(mSpeed);

                }


            }
        });
    }


   // TODO Update this with gpsHealth from ROS
//
//    private void updateSatelliteCount() {
//        final Handler mHandler = new Handler();
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run()
//            {
//                 fcsCallback = new FlightControllerState.Callback() {
//                    @Override
//                    public void onUpdate(FlightControllerState flightControllerState) {
//
//                       // satelliteCount = flightControllerState.getSatelliteCount();
//                        //satelliteCountTextView.setText(String.valueOf(satelliteCount));
//
//                    }
//                };
//
//                mHandler.postDelayed(this, 1000);
//
//            }
//        }, 1000);
//
//
//    }

    private void updateBatteryImageView()
    {
        final int temp [] = new int[1];

        if(batteryChargeRemaining > 90 && batteryChargeRemaining <  100)
        {
            temp[0] = R.mipmap.battery10;
        }

        else if (batteryChargeRemaining > 80 && batteryChargeRemaining < 90)
        {
            temp[0] = R.mipmap.battery9;
        }

        else if (batteryChargeRemaining > 70 && batteryChargeRemaining < 80)
        {
            temp[0] = R.mipmap.battery8;
        }

        else if (batteryChargeRemaining > 60 && batteryChargeRemaining < 70)
        {
            temp[0] = R.mipmap.battery7;
        }

        else if (batteryChargeRemaining > 50 && batteryChargeRemaining < 60)
        {
            temp[0] = R.mipmap.battery6;
        }

        else if (batteryChargeRemaining > 40 && batteryChargeRemaining < 50)
        {
            temp[0] = R.mipmap.battery5;
        }

        else if (batteryChargeRemaining > 30 && batteryChargeRemaining < 40)
        {
            temp[0] = R.mipmap.battery4;
        }
        else if (batteryChargeRemaining > 20 && batteryChargeRemaining < 30)
        {
            temp[0] = R.mipmap.battery3;
        }

        else if (batteryChargeRemaining > 10 && batteryChargeRemaining < 20)
        {
            temp[0] = R.mipmap.battery2;
        }

        else if (batteryChargeRemaining < 10)
        {
            temp[0] = R.mipmap.battery1;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                batteryStatusImageView.setImageDrawable(MainActivity.this.getDrawable(temp[0]));
                batteryLevelTextView.setText(String.valueOf(batteryChargeRemaining) + "%");
            }
        });

    }

    // Updates Map with and marks current location of device in use
    //Make sure you check permissions if you're using the new Google Fused Location API
    // Older method for google Locations(FusedLocationProviderApi) has been deprecated.
    @Override
    public void onMapReady(GoogleMap gMap)
    {
        if(googleMap == null)
        {
            googleMap = gMap;
            setUpMap();
        }

        getLastLocation();
    }

    private void getLastLocation()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            homeLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(homeLatLng, 19);
                            googleMap.animateCamera(update);
                            markHomePoint(homeLatLng);
                        } else if (location == null) {
                            Toast.makeText(getApplicationContext(), "Can't get current location", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    // Marks the current homepoint on the map
    private void markHomePoint(LatLng point) {
        //Create MarkerOptions object
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        googleMap.addMarker(markerOptions);

    }

    @Override
    public void onMapClick(LatLng point)
    {
        hamburgerRelativeLayout.setVisibility(View.GONE);
        flightConfigPanel.setVisibility(View.GONE);

//        markWayPoint(point);
//
//        missionConfigDataEncoder.setLatitude(point.latitude);
//        missionConfigDataEncoder.setLongitude(point.longitude);
//        missionConfigDataEncoder.setAltitude(mAltitude);
//        missionConfigDataEncoder.setSpeed(mSpeed);


    }



    private void markWayPoint(LatLng point)
    {
        //Create MarkerOptions object
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        Marker marker = googleMap.addMarker(markerOptions);
        markerArrayList.add(marker);
    }



    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.ros_settings_button:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;

            case R.id.config_ok_btn:

                mAltitude = heightSeekbar.getProgress();
                mSpeed = speedSeekbar.getProgress();
                byte orientationCommand;
                byte missionEndCommand;

                switch (orientationRadioGroup.getCheckedRadioButtonId())
                {

                    //TODO Document Orientation commands properly
                    case R.id.orientInitial_radio_button:
                        orientationCommand = 1;
                        missionConfigDataManager.setCourseLock(orientationCommand);
                        break;

                    case R.id.orientNext_radio_button:
                        orientationCommand = 2;
                        missionConfigDataManager.setCourseLock(orientationCommand);
                        break;

                    case R.id.orientWaypoint_radio_button:
                        orientationCommand = 4;
                        missionConfigDataManager.setCourseLock(orientationCommand);
                        break;

                    case R.id.orientRc_radio_button:
                        orientationCommand = 3;
                        missionConfigDataManager.setCourseLock(orientationCommand);
                        break;
                }

                switch(missionEndRadioGroup.getCheckedRadioButtonId())
                {
                    //TODO Document Mission END commands properly
                    case R.id.hover_radio_button:
                        missionEndCommand = 1;
                        missionConfigDataManager.setMissionEnd(missionEndCommand);
                        break;

                    case R.id.waypoint_radio_button:
                        missionEndCommand = 2;
                        missionConfigDataManager.setMissionEnd(missionEndCommand);
                        break;

                    case R.id.returnhome_radio_button:
                        missionEndCommand = 3;
                        missionConfigDataManager.setMissionEnd(missionEndCommand);
                        break;

                    case R.id.autoland_radio_button:
                        missionEndCommand = 4;
                        missionConfigDataManager.setMissionEnd(missionEndCommand);
                        break;
                }

                 byte[] data = {0};

                if(markerArrayList.size() > 0)
                {
                    for(int i = 0; i < markerArrayList.size(); i++)
                    {
                        missionConfigDataManager.setLatitude(markerArrayList.get(i).getPosition().latitude);
                        missionConfigDataManager.setLongitude(markerArrayList.get(i).getPosition().longitude);
                        missionConfigDataManager.setAltitude(mAltitude);
                        missionConfigDataManager.setSpeed(mSpeed);

                        data = missionConfigDataManager.getConfigData();

                    }

                }
                missionConfigDataManager.sendMissionData(data, mFlightController);

                flightConfigPanel.setVisibility(v.GONE);

                break;

            case R.id.config_cancel_btn:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        googleMap.clear();
                    }

                });

                missionConfigDataManager = null;
                flightConfigPanel.setVisibility(v.GONE);

                break;

                //When Abort button is pressed, aircraft goes home.
            case R.id.abort_btn:
                byte [] GOHOME_CMD = {0x02};
                missionConfigDataManager.sendCommand(GOHOME_CMD, mFlightController);
                break;

            case R.id.start_btn:
                byte [] STARTMISSION_CMD = {0x1A};
                missionConfigDataManager.sendCommand(STARTMISSION_CMD, mFlightController);
                break;

            default:
                break;

        }



    }





}
