package com.example.e_learning_restaurants;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getIntent();

        if (intent.getStringArrayListExtra("latLongList") != null) {

            ArrayList<String> latLongList = intent.getStringArrayListExtra("latLongList");

            for (String latLong : latLongList) {
                String[] latLongArray = latLong.split(":");
                double latitude = Double.parseDouble(latLongArray[0]);
                double longitude = Double.parseDouble(latLongArray[1]);
                String restName = latLongArray[2];
                LatLng restaurantLocation = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(restaurantLocation).title(restName));
            }

        } else {

            double latitude = intent.getDoubleExtra("LATITUDE", 0);
            double longitude = intent.getDoubleExtra("LONGITUDE", 0);
            String restaurantName = intent.getStringExtra("RESTAURANT_NAME");
            // Add a marker in Sydney and move the camera
            LatLng restaurantLocation = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(restaurantLocation).title(restaurantName));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(restaurantLocation));

        }

        mMap.setOnMapLongClickListener(latLng -> {
            showAlertDialog(latLng);
        });

        mMap.setOnMarkerClickListener(marker -> {
            String markerName = marker.getTitle();
            Toast.makeText(MapsActivity.this, "Clicked restaurant is " + markerName, Toast.LENGTH_SHORT).show();
            return false;
        });
    }

    //zadanie 2
    private void showAlertDialog(LatLng latLng) {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Restaurant Name");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            // send request here to add restaurant on the remote source as well
            String inputText = input.getText().toString();
            mMap.addMarker(new MarkerOptions().position(latLng).title(inputText));
            Toast.makeText(this, "You added: " + inputText, Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}