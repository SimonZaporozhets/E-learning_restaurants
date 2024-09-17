package com.example.e_learning_restaurants;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.e_learning_restaurants.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("LATITUDE", 0);
        double longitude = intent.getDoubleExtra("LONGITUDE", 0);
        String restaurantName = intent.getStringExtra("RESTAURANT_NAME");
        // Add a marker in Sydney and move the camera
        LatLng restaurantLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(restaurantLocation).title(restaurantName));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(restaurantLocation));
    }
}