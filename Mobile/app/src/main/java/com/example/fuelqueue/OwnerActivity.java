package com.example.fuelqueue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fuelqueue.models.Owner;
import com.google.android.material.chip.Chip;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class OwnerActivity extends AppCompatActivity {
    Owner owner;
    String userId;
    ProgressDialog dialog;
    ArrayList<Boolean> loadingArray;
    MutableLiveData<Boolean> loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);
        Button setArriveTimeBtn = findViewById(R.id.set_arrive_time);
        Button setFinishTimeBtn = findViewById(R.id.finish_button);
        Button updateButton = findViewById(R.id.update_button);
        SQLiteDatabase db = openOrCreateDatabase("FuelManagement",MODE_PRIVATE,null);
        Cursor resultSet = db.rawQuery("Select * from User",null);
        resultSet.moveToFirst();
        userId = resultSet.getString(0);

        loadingArray = new ArrayList<>();
        loading = new MutableLiveData<>();

        loading.setValue(false);

        loading.observe(OwnerActivity.this,new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loadingValue) {
                if (loadingValue) {
                    loadingArray.add(true);
                } else if (loadingArray.size() > 0) {
                    loadingArray.remove(loadingArray.size() - 1);
                }
                if (loadingArray.size() > 0) {
                    dialog.show();
                } else {
                    dialog.hide();
                }
            }
        });

        dialog = new ProgressDialog(OwnerActivity.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.swiperefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadQueueData();
                pullToRefresh.setRefreshing(false);
            }
        });


        loadOwnerData();
        loadQueueData();

        SwitchCompat switchCompat = findViewById(R.id.switchButton);

        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                owner.setFuelType("Diesel");
            }
        });


        EditText setArriveTimeText = findViewById(R.id.arrive);
        EditText setFinishTimeText = findViewById(R.id.finish);

        setArriveTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date currentTime = Calendar.getInstance().getTime();
                LocalDateTime date = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    date = LocalDateTime.now();
                    owner.setArrivalTime(date.toString());
                }
                String time = currentTime.toLocaleString();
                setArriveTimeText.setText(time);
                owner.setStatus("Fuel Available");
                setChipColors();
            }
        });

        setFinishTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date currentTime = Calendar.getInstance().getTime();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    LocalDateTime date = LocalDateTime.now();
                    owner.setFinishTime(date.toString());
                }
                String time = currentTime.toLocaleString();
                setFinishTimeText.setText(time);
                owner.setStatus("Fuel Not Available");
                setChipColors();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    UpdateOwnerData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setChipColors() {
        Chip status = findViewById(R.id.status);
        if (owner.getStatus().equals("Fuel Available")) {
            status.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(OwnerActivity.this, R.color.green)));
        } else {
            status.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(OwnerActivity.this, R.color.red)));
        }
    }

    private void loadQueueData() {
        loading.setValue(true);
        RequestQueue volleyQueue = Volley.newRequestQueue(OwnerActivity.this);
        String url = "https://fuel-management-api.herokuapp.com/owners/queue/" + userId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                (Response.Listener<JSONObject>) response -> {
                    try {
                        System.out.println(response);
                        String durationTemp = (String) response.get("estimatedTime");
                        String duration = durationTemp.split("[.]")[0];

                        TextView durationText = findViewById(R.id.estimatedTime);
                        String durationMessage = "Estimated Time: " + duration;
                        durationText.setText(durationMessage);

                        Integer count = (Integer) response.get("queue");
                        TextView QueueCount = findViewById(R.id.QueueCount);
                        String queueMessage = "Users in Queue: " + count.toString();
                        QueueCount.setText(queueMessage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    loading.setValue(false);
                },
                (Response.ErrorListener) error -> {
                    loading.setValue(false);
                    System.out.println(error);
                    Toast.makeText(OwnerActivity.this, "Some error occurred! Cannot fetch owner data", Toast.LENGTH_LONG).show();
                    Log.e("MainActivity", "Load owner error: ${error.localizedMessage}");
                }
        );
        volleyQueue.add(jsonObjectRequest);
    }

    private void setOwnerData() {
        EditText setNameText = findViewById(R.id.name_input);
        EditText setLocationText = findViewById(R.id.location_input);
        EditText setArrivalTimeText = findViewById(R.id.arrive);
        EditText setFinishTimeText = findViewById(R.id.finish);
        Chip status = findViewById(R.id.status);
        Button setFinishTimeBtn = findViewById(R.id.finish_button);
        Button setArriveTimeBtn = findViewById(R.id.set_arrive_time);

        setNameText.setText(owner.getName());
        setLocationText.setText(owner.getLocation());
        String ownerArrivalTime = owner.getArrivalTime();
        String ownerFinishTime = owner.getFinishTime();
        status.setText("Status: " + owner.getStatus());
        setChipColors();

        String [] arrivalTimeArray = ownerArrivalTime.split("T");
        String arrivalDate = arrivalTimeArray[0];
        String time = arrivalTimeArray[1].split("\\.")[0];
        String formattedArrivalTime = arrivalDate + " (" + time + ")";

        if (!owner.getStatus().equals("Fuel Available")) {
            String [] finishTimeArray = ownerFinishTime.split("T");
            String finishDate = finishTimeArray[0];
            String finishTime = finishTimeArray[1].split("\\.")[0];
            String formattedFinishTime = finishDate + " (" + finishTime + ")";
            setFinishTimeText.setText(formattedFinishTime);
            setArrivalTimeText.setText("");
            setFinishTimeBtn.setEnabled(false);
            setArriveTimeBtn.setEnabled(true);
        } else {
            setFinishTimeBtn.setEnabled(true);
            setArriveTimeBtn.setEnabled(false);
            setArrivalTimeText.setText(formattedArrivalTime);
            setFinishTimeText.setText("");
        }

        SwitchCompat switchCompat = (SwitchCompat) findViewById(R.id.switchButton);
        if (owner.getFuelType().equals("Petrol")) {
            switchCompat.setChecked(false);
        } else {
            switchCompat.setChecked(true);
        }
    }
    private void loadOwnerData() {
        loading.setValue(true);
        RequestQueue volleyQueue = Volley.newRequestQueue(OwnerActivity.this);
        String url = "https://fuel-management-api.herokuapp.com/owners/" + userId;
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
                    loading.setValue(false);
                },
                (Response.ErrorListener) error -> {
                    loading.setValue(false);
                    Toast.makeText(OwnerActivity.this, "Some error occurred! Cannot fetch owner data", Toast.LENGTH_LONG).show();
                    Log.e("MainActivity", "Load owner error: ${error.localizedMessage}");
                }
        );
        volleyQueue.add(jsonObjectRequest);
    }

    private void UpdateOwnerData() throws JSONException {
        loading.setValue(true);
        RequestQueue volleyQueue = Volley.newRequestQueue(OwnerActivity.this);
        String url = "https://fuel-management-api.herokuapp.com/owners/" + userId;
        Gson gson = new Gson();
        String userJson = gson.toJson(owner);
        JSONObject user = new JSONObject(userJson);
        System.out.println(user);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PATCH,
                url,
                user,
                (Response.Listener<JSONObject>) response -> {
                    try {
                        System.out.println(response);
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
                    loading.setValue(false);
                },
                (Response.ErrorListener) error -> {
                    System.out.println("Error");
                    loading.setValue(false);
                    Toast.makeText(OwnerActivity.this, "Some error occurred! Cannot update owner data", Toast.LENGTH_LONG).show();
                    Log.e("MainActivity", "Update owner error: ${error.localizedMessage}");
                }
        );
        volleyQueue.add(jsonObjectRequest);
    }
}
