package kr.ac.konkuk.locationtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {

    //HOW OFTEN
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;
    private static final int PERMISSIONS_FINE_LOCATION = 99;

    //ui
    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address, tv_wayPointCounts;
    Button btn_newWayPoint, btn_showWayPointList;
    Switch sw_locationsupdates, sw_gps;

    // variable for remember if we are tracking location or not
    boolean updateOn = false;

    //current location
    Location currentLocation;

    //List of saved locations
    List<Location> savedLocations;


    //Location Request is a config file for all setting realted ro FusedLocationProviderClient
    LocationRequest locationRequest;

    //location callback
    LocationCallback locationCallBack;

    // Google's API for location services. the majority of the app functions using this class
    FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * ui
         */
        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        tv_address = findViewById(R.id.tv_address);
        sw_locationsupdates = findViewById(R.id.sw_locationsupdates);
        sw_gps = findViewById(R.id.sw_gps);
        btn_newWayPoint = findViewById(R.id.btn_newWayPoint);
        btn_showWayPointList = findViewById(R.id.btn_showWayPointList);
        tv_wayPointCounts = findViewById(R.id.tv_countOfCrumbs);




        /*
         * set all propertiees of LocationRequest
         */

        locationRequest = new LocationRequest();

        // 디폴트 위치기록 주기 30secs
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        // 더 빠른 주기록 기록시 5secs
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);
        //high accuracy lowpower
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        /*
         * LOCATION CALLBACK
         * Event that is triggered whenever the update interval is met
         */
        locationCallBack = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);


                //save the location
//                Location location = locationResult.getLastLocation();
                updateUIValues(locationResult.getLastLocation());

            }
        };


        // NEW WAYPOINT
        btn_newWayPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the gps location

                //add the new location to global list
                MyApplication myApplication = (MyApplication)getApplicationContext();
                savedLocations = myApplication.getMyLocations(); //savedLocation 은 이제 글로벌 리스트이다.
                savedLocations.add(currentLocation);

            }
        });



        //gps accuracy
        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_gps.isChecked()) {
                    //true
                    //most accurate - use GPS
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS Sensors");
                } else {
                    //most accurate - use GPS
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Towers + WIFI");
                }
            }
        });


        // location updates!
        sw_locationsupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_locationsupdates.isChecked()) {
                    //turn on location updates
                    startLocationUpdates();

                } else {
                    //turn off location updates
                    stopLocationUpdates();
                }
            }
        });


        updateGPS();


    } // end of onCreate method

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        tv_updates.setText("Location is being tracked");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
        updateGPS();

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
//            updateGPS();
//        }
//        else {
//            // permissions not granted yet
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){ //최소 요구버전 M이상 이여야한다.
//                requestPermissions(new String [] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
//            }
//        }



    }

    private void stopLocationUpdates() {
        tv_updates.setText("Location is Not being tracked");
        tv_lat.setText("Not tracking location");
        tv_lon.setText("Not tracking location");
        tv_speed.setText("Not tracking location");
        tv_address.setText("Not tracking location");
        tv_accuracy.setText("Not tracking location");
        tv_altitude.setText("Not tracking location");
        tv_sensor.setText("Not tracking location");

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSIONS_FINE_LOCATION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    updateGPS();
                }
                else{
                    Toast.makeText(this, "This app requires permission to be granted in order to work properly", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void updateGPS() {
        // get permissions from the user to track GPS
        // get the current location from the fused client
        // update the UI - i.e. set all properties in their associated text view items

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this); //or this
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //user provided the permission
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //we got permissions. Put the values of  경도 위도 속도 드등을 가져다가 UI components 에 넣을것이다.
                    updateUIValues(location);
                    currentLocation = location;

                }
            });
        }
        else {
            // permissions not granted yet
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){ //최소 요구버전 M이상 이여야한다.
                requestPermissions(new String [] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    } //updateGPS



    private void updateUIValues(Location location) {

        //update all of the test view object with new location
        tv_lat.setText(valueOf(location.getLatitude())); //latitude는 double이라 파싱해서 스트링으로 전환
        tv_lon.setText(valueOf(location.getLongitude()));
        tv_accuracy.setText(valueOf(location.getAccuracy()));
        //나중에 여기에 시간 가져오기!


        if(location.hasAltitude()){ //모든 폰이 고도 측정은 안해서 고도도 가져옴
            tv_altitude.setText(valueOf(location.getAltitude()));
        }
        else {
            tv_altitude.setText("Not Available");
        }

        if(location.hasSpeed()){ //모든 폰이 고도 측정은 안해서 고도도 가져옴
            tv_speed.setText(valueOf(location.getSpeed()));
        }
        else {
            tv_speed.setText("Not Available");
        }


        //reverse - geocoding
        Geocoder geocoder = new Geocoder(MainActivity.this);

        try{
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);
            tv_address.setText(addresses.get(0).getAddressLine(0));
        }
        catch (Exception e){
            tv_address.setText("Unable to get street address");
        }


        // Crumbs / savedLocations
        MyApplication myApplication = (MyApplication)getApplicationContext();
        savedLocations = myApplication.getMyLocations();

        //show the number of waypoints saved.
        tv_wayPointCounts.setText(Integer.toString(savedLocations.size()));   //몇개 있는지




    } //updateUIValues


}






































