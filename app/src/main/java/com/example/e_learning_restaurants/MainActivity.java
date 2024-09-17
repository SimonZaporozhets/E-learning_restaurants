package com.example.e_learning_restaurants;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

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

        Button btn_AddRestaurant = findViewById(R.id.btn_AddRestaurant);

        ListView lv_Restaurant = findViewById(R.id.lv_Restaurants);
        SharedPreferences preferences = getSharedPreferences("userPreferences", Activity.MODE_PRIVATE);
        String userId = preferences.getString("userId", "1");

        final ArrayList<String> restaurantsList = new ArrayList<String>();
        ArrayAdapter<String> restaurantsAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, restaurantsList);
        lv_Restaurant.setAdapter(restaurantsAdapter);
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://dev.imagit.pl/mobilne/api/restaurants/" + userId;

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
                    }
                    lv_Restaurant.setAdapter(restaurantsAdapter);

                    lv_Restaurant.setOnItemClickListener((parent, view, position, id) -> {
                        try {
                            JSONObject selectedRestaurant = jsonArray.getJSONObject(position);

                            double latitude = selectedRestaurant.getDouble("LAT");
                            double longitude = selectedRestaurant.getDouble("LON");
                            String restaurantName = selectedRestaurant.getString("RESTAURANT_NAME");

                            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                            intent.putExtra("LATITUDE", latitude);
                            intent.putExtra("LONGITUDE", longitude);
                            intent.putExtra("RESTAURANT_NAME", restaurantName);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });;
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });

        btn_AddRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddRestaurantActivity.class);
                startActivity(intent);
            }
        });
    }
}