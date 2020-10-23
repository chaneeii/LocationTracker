package kr.ac.konkuk.locationtracker;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.core.app.ActivityCompat;

        import android.Manifest;
        import android.content.pm.PackageManager;
        import android.location.Location;
        import android.os.Build;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Switch;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.android.gms.location.FusedLocationProviderClient;
        import com.google.android.gms.location.LocationRequest;
        import com.google.android.gms.location.LocationServices;
        import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    //HOW OFTEN
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;
    private static final int PERMISSIONS_FINE_LOCATION = 99;

    //ui
    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address;
    Switch sw_locationsupdates, sw_gps;

    // variable for remember if we are tracking location or not
    boolean updateOn = false;

    //Location Request is a config file for all setting realted ro FusedLocationProviderClient
    LocationRequest locationRequest;

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



        /*
        * set all propertiees of LocationRequest
        */

        locationRequest = new LocationRequest();

        // 디폴트 위치기록 주기 30secs
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);

        // 더 빠른 주기록 기록시 5secs
        locationRequest.setInterval(1000 * FAST_UPDATE_INTERVAL);

        //high accuracy lowpower
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);



        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_gps.isChecked()){
                    //true
                    //most accurate - use GPS
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS Sensors");
                }
                else{
                    //most accurate - use GPS
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Towers + WIFI");
                }
            }
        });


        updateGPS();


    } // end of onCreate method

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
        tv_lat.setText(String.valueOf(location.getLatitude())); //latitude는 double이라 파싱해서 스트링으로 전환
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));
        //나중에 여기에 시간 가져오기!

        if(location.hasAltitude()){ //모든 폰이 고도 측정은 안해서 고도도 가져옴
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        }
        else {
            tv_altitude.setText("Not Available");
        }

        if(location.hasSpeed()){ //모든 폰이 고도 측정은 안해서 고도도 가져옴
            tv_speed.setText(String.valueOf(location.getSpeed()));
        }
        else {
            tv_speed.setText("Not Available");
        }



    } //updateUIValues


}






































