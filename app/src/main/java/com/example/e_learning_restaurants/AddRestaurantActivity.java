package com.example.e_learning_restaurants;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddRestaurantActivity extends AppCompatActivity {

    String gps_lat = "";
    String gps_lon = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_restaurant);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btn_AddRestaurantSubmit = findViewById(R.id.btn_AddRestaurantSubmit);
        Button btn_GetGPS = findViewById(R.id.btn_GetGPS);
        TextView tv_Position = findViewById(R.id.btn_GetGPS);
        EditText et_RestaurantName = findViewById(R.id.et_RestaurantName);
        EditText et_RestaurantPhone = findViewById(R.id.et_RestaurantPhone);


        SharedPreferences preferences = getSharedPreferences("userPreferences", Activity.MODE_PRIVATE);
        String userId = preferences.getString("userId", "0");

        final String locationProvider = LocationManager.GPS_PROVIDER;
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        btn_GetGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(AddRestaurantActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddRestaurantActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(locationProvider, 5000, 50, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        gps_lat = ""+location.getLatitude();
                        gps_lon = ""+location.getLongitude();

                        tv_Position.setText(gps_lat+" "+gps_lon);
                    }
                });
            }
        });
    }
}