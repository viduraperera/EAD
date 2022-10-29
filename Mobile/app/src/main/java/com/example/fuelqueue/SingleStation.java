package com.example.fuelqueue;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fuelqueue.models.Owner;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class SingleStation extends AppCompatActivity {
    String userId;
    String stationId;
    Integer count;
    TextView QueueCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_station);

        SQLiteDatabase db = openOrCreateDatabase("FuelManagement",MODE_PRIVATE,null);
        Cursor resultSet = db.rawQuery("Select * from User",null);
        resultSet.moveToFirst();
        userId = resultSet.getString(0);

        TextView stationName = findViewById(R.id.station_name_single);
        TextView location = findViewById(R.id.location_single);
        TextView arrivalTime = findViewById(R.id.fuel_arrival_time_single);
        TextView finishTime = findViewById(R.id.fuel_finish_time_single);
        QueueCount = findViewById(R.id.QueueCount);


        Bundle bundle = getIntent().getExtras();

        this.stationId = bundle.getString("id");
        String sStation = bundle.getString("name");
        String sLocation = bundle.getString("location");
        String sArrivalTime = bundle.getString("arrival_time");
        String sFinishTime = bundle.getString("finish_time");

        stationName.setText(sStation.toString());
        location.setText(sLocation.toString());
        arrivalTime.setText(sArrivalTime.toString());
        finishTime.setText(sFinishTime.toString());
        loadQueueData();

        Button joinButton = findViewById(R.id.joinButton);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    JoinQueue();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Button afterButton = findViewById(R.id.afterButton);
        afterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Exit(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Button beforeButton = findViewById(R.id.beforeButton);
        beforeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Exit(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadQueueData() {
        RequestQueue volleyQueue = Volley.newRequestQueue(SingleStation.this);
        String url = "https://fuel-management-api.herokuapp.com/owners/queue/" + stationId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                (Response.Listener<JSONObject>) response -> {
                    try {
                        Integer count = (Integer) response.get("queue");
                        QueueCount.setText(count.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                (Response.ErrorListener) error -> {
                    System.out.println(error);
                    Toast.makeText(SingleStation.this, "Some error occurred! Cannot fetch owner data", Toast.LENGTH_LONG).show();
                    Log.e("MainActivity", "Load owner error: ${error.localizedMessage}");
                }
        );
        volleyQueue.add(jsonObjectRequest);
    }

    private void JoinQueue() throws JSONException {
        RequestQueue volleyQueue = Volley.newRequestQueue(SingleStation.this);
        String url = "https://fuel-management-api.herokuapp.com/customers/arrival/" + userId;
        JSONObject user = new JSONObject();
        user.put("FuelStationId", this.stationId);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PATCH,
                url,
                user,
                (Response.Listener<JSONObject>) response -> {
                    loadQueueData();
                },
                (Response.ErrorListener) error -> {
                    System.out.println("Error");
                    Toast.makeText(SingleStation.this, "Some error occurred! Cannot update owner data", Toast.LENGTH_LONG).show();
                    Log.e("MainActivity", "Update owner error: ${error.localizedMessage}");
                }
        );
        volleyQueue.add(jsonObjectRequest);
    }
    private void Exit(boolean DidPumpedFuel) throws JSONException {
        RequestQueue volleyQueue = Volley.newRequestQueue(SingleStation.this);
        String url = "https://fuel-management-api.herokuapp.com/customers/departure/" + userId;
        JSONObject user = new JSONObject();
        user.put("DidPumpedFuel", DidPumpedFuel);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PATCH,
                url,
                user,
                (Response.Listener<JSONObject>) response -> {
                    loadQueueData();
                },
                (Response.ErrorListener) error -> {
                    System.out.println("Error");
                    Toast.makeText(SingleStation.this, "Some error occurred! Cannot update owner data", Toast.LENGTH_LONG).show();
                    Log.e("MainActivity", "Update owner error: ${error.localizedMessage}");
                }
        );
        volleyQueue.add(jsonObjectRequest);
    }
}
