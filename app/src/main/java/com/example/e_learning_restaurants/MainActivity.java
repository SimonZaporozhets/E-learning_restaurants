package com.example.e_learning_restaurants;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private Button btn_AddRestaurant;
    private Button btnShowAllRestaurants;
    private ProgressBar progressBar;


    private ListView lv_Restaurant;

    private final ArrayList<String> restaurantsList = new ArrayList<String>();
    private final ArrayList<String> latLongList = new ArrayList<String>();
    ArrayAdapter<String> restaurantsAdapter;

    private void initViews() {
        btn_AddRestaurant = findViewById(R.id.btn_AddRestaurant);
        btnShowAllRestaurants = findViewById(R.id.btnShowAllRestaurants);
        lv_Restaurant = findViewById(R.id.lv_Restaurants);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        SharedPreferences preferences = getSharedPreferences("userPreferences", Activity.MODE_PRIVATE);
        String userId = preferences.getString("userId", "1");

        restaurantsAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, restaurantsList);
        lv_Restaurant.setAdapter(restaurantsAdapter);
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://dev.imagit.pl/mobilne/api/restaurants/" + userId;

        progressBar.setVisibility(View.VISIBLE);
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String JSON = new String(bytes);
                try {
                    JSONArray jsonArray = new JSONArray(JSON);
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(j);
                        String restaurantName = jsonObject.getString("RESTAURANT_NAME");
                        String restaurantPhone = jsonObject.getString("RESTAURANT_PHONE");
                        restaurantsList.add(restaurantName + " " + restaurantPhone);

                        String rlat = jsonObject.getString("RESTAURANT_LAT");
                        String rlong = jsonObject.getString("RESTAURANT_LONG");
                        if (rlat.isEmpty() || rlong.isEmpty()) continue;
                        latLongList.add(rlat + ":" + rlong + ":" + restaurantName);
                    }
                    lv_Restaurant.setAdapter(restaurantsAdapter);

                    lv_Restaurant.setOnItemClickListener((parent, view, position, id) -> {
                        new AlertDialog.Builder(MainActivity.this).setPositiveButton("delete restaurant", (dialog, which) -> {
                            deleteRestaurant(jsonArray, position);
                        }).setNegativeButton("show restaurant on the map", (dialog, which) -> {
                            showRestaurantOnMap(jsonArray, position);
                        }).show();
                    });
                    ;
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                progressBar.setVisibility(View.GONE);
            }
        });

        btn_AddRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddRestaurantActivity.class);
                startActivity(intent);
            }
        });

        // zadanie 1
        btnShowAllRestaurants.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            intent.putStringArrayListExtra("latLongList", latLongList);
            startActivity(intent);
        });
    }

    private void showRestaurantOnMap(JSONArray jsonArray, int position) {
        try {
            JSONObject selectedRestaurant = jsonArray.getJSONObject(position);

            double latitude = selectedRestaurant.getDouble("RESTAURANT_LAT");
            double longitude = selectedRestaurant.getDouble("RESTAURANT_LONG");
            String restaurantName = selectedRestaurant.getString("RESTAURANT_NAME");

            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            intent.putExtra("LATITUDE", latitude);
            intent.putExtra("LONGITUDE", longitude);
            intent.putExtra("RESTAURANT_NAME", restaurantName);
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // zadanie 3
    private void deleteRestaurant(JSONArray jsonArray, int position) {
        try {
            SharedPreferences preferences = getSharedPreferences("userPreferences", Activity.MODE_PRIVATE);
            String userId = preferences.getString("userId", "1");

            JSONObject selectedRestaurant = jsonArray.getJSONObject(position);
            String restaurantId = selectedRestaurant.getString("RESTAURANT_ID");

            AsyncHttpClient client = new AsyncHttpClient();
            String url = "http://dev.imagit.pl/mobilne/api/restaurant/delete/" + userId + "/" + restaurantId;

            progressBar.setVisibility(View.VISIBLE);
            client.get(url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String JSON = new String(bytes);
                    if ("OK".equals(JSON)) {
                        restaurantsList.remove(position);
                        restaurantsAdapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "Restauracja usunięta", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Restauracja nie usunięta", Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
        }

    }
}