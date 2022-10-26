package com.example.fuelqueue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fuelqueue.models.Owner;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class OwnerActivity extends AppCompatActivity {
    Owner owner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);
        loadOwnerData();
        Button setArriveTimeBtn = findViewById(R.id.set_arrive_time);

        EditText setArriveTimeText = findViewById(R.id.arrive);

        setArriveTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date currentTime = Calendar.getInstance().getTime();
                String time = currentTime.toLocaleString();
                setArriveTimeText.setText(time);
            }
        });
    }

    private void setOwnerData() {
        EditText setNameText = findViewById(R.id.name_input);
        EditText setLocationText = findViewById(R.id.location_input);
        EditText setArrivalTimeText = findViewById(R.id.arrive);


        setNameText.setText(owner.getName());
        setLocationText.setText(owner.getLocation());
        String arrivalTime = owner.getArrivalTime();
        System.out.println("ffffffffffffffffffffffffffffffffffffffffff");
        System.out.println(arrivalTime.split("T")[0]);
        System.out.println(arrivalTime.split("T")[1]);
//        Date newDate = new Date(arrivalTime.split("T")[0]);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        try {
            Date date = format.parse(arrivalTime);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setArrivalTimeText.setText(owner.getArrivalTime());
        SwitchCompat switchCompat = (SwitchCompat) findViewById(R.id.switchButton);
        if (owner.getFuelType().equals("Petrol")) {
            switchCompat.setChecked(false);
        } else {
            switchCompat.setChecked(true);
        }
    }
    private void loadOwnerData() {
        RequestQueue volleyQueue = Volley.newRequestQueue(OwnerActivity.this);
        String url = "https://fuel-management-api.herokuapp.com/owners/a8317746-6c5e-45dc-8f9f-ed0a83aa25c5";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                (Response.Listener<JSONObject>) response -> {
                    try {
                        owner = new Owner();
                        owner.setId((String) response.get("id"));
                        owner.setName((String) response.get("name"));
                        owner.setEmail((String) response.get("email"));
                        owner.setStatus((String)response.get("status"));
                        owner.setLocation((String)response.get("location"));
                        owner.setFuelType((String) response.get("fuelType"));
                        owner.setArrivalTime((String) response.get("arrivalTime"));
                        owner.setFinishTime((String) response.get("finishTime"));
                        setOwnerData();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                (Response.ErrorListener) error -> {
                    Toast.makeText(OwnerActivity.this, "Some error occurred! Cannot fetch owner data", Toast.LENGTH_LONG).show();
                    Log.e("MainActivity", "loadDogImage error: ${error.localizedMessage}");
                }
        );
        volleyQueue.add(jsonObjectRequest);
    }
}
