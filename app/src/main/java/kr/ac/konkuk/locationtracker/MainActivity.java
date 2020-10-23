package kr.ac.konkuk.locationtracker;

        import androidx.appcompat.app.AppCompatActivity;

        import android.os.Bundle;
        import android.widget.Switch;
        import android.widget.TextView;

        import com.google.android.gms.location.FusedLocationProviderClient;
        import com.google.android.gms.location.LocationRequest;

public class MainActivity extends AppCompatActivity {

    //HOW OFTEN
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;

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





    }
}