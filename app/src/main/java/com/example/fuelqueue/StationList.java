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

        String URL = "https://run.mocky.io/v3/1c68caff-8dc1-4b07-858e-e8f6bfbfec36";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0 ; i < response.length() ; i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        String stationName = jsonObject.getString("name");
                        String location = jsonObject.getString("location");
                        String arrivalTime = jsonObject.getString("arrivalTime");
                        String finishTime = jsonObject.getString("finishTime");

                        Station station = new Station(stationName, arrivalTime, finishTime, location);
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
