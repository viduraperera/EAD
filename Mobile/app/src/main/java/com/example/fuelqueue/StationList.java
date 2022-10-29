package com.example.fuelqueue;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.fuelqueue.models.Station;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StationList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RequestQueue requestQueue;
    private List<Station> stationList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.station_list);

        recyclerView = findViewById(R.id.staion_list_items);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        requestQueue = VolleyStationSingleton.getmInstance(this).getRequestQueue();

        stationList = new ArrayList<>();
        fetchStation();

    }

    private void fetchStation() {

        String URL = "https://fuel-management-api.herokuapp.com/owners";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0 ; i < response.length() ; i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        System.out.println(jsonObject);

                        String stationId = jsonObject.getString("id");
                        String stationName = jsonObject.getString("name");
                        String location = jsonObject.getString("location");
                        String arrivalTime = jsonObject.getString("arrivalTime");
                        String finishTime = jsonObject.getString("finishTime");
                        String fuelType = jsonObject.getString("fuelType");
                        String status = jsonObject.getString("status");

                        Station station = new Station(stationId, stationName, arrivalTime, finishTime, location, fuelType, status);
                        stationList.add(station);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    StationAdapter adapter = new StationAdapter(StationList.this, stationList);

                    recyclerView.setAdapter(adapter);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(StationList.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }
}
