package com.example.fuelqueue;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SingleStation extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_station);

        TextView stationName = findViewById(R.id.station_name_single);
        TextView location = findViewById(R.id.location_single);
        TextView arrivalTime = findViewById(R.id.fuel_arrival_time_single);
        TextView finishTime = findViewById(R.id.fuel_finish_time_single);


        Bundle bundle = getIntent().getExtras();

        String sStation = bundle.getString("name");
        String sLocation = bundle.getString("location");
        String sArrivalTime = bundle.getString("arrival_time");
        String sFinishTime = bundle.getString("finish_time");

        stationName.setText(sStation.toString());
        location.setText(sLocation.toString());
        arrivalTime.setText(sArrivalTime.toString());
        finishTime.setText(sFinishTime.toString());
    }
}
