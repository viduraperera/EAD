package com.example.fuelqueue;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.fuelqueue.models.Register;
import com.example.fuelqueue.models.User;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private Button submitButton;
    private EditText name, email, password, fuelType;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        fuelType = findViewById(R.id.reFuelType);
        submitButton = findViewById(R.id.btnSignup);
        progressBar = findViewById(R.id.loadingPB);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().isEmpty() && email.getText().toString().isEmpty() && password.getText().toString().isEmpty() && fuelType.getText().toString().isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    postCustomerData(name.getText().toString(), email.getText().toString(), password.getText().toString(), fuelType.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void postCustomerData(String nameR, String emailR, String passwordR, String fuelTypeR) throws JSONException {
        ProgressDialog dialog=new ProgressDialog(RegisterActivity.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();
        RequestQueue volleyQueue = Volley.newRequestQueue(RegisterActivity.this);

        String url = "https://fuel-management-api.herokuapp.com/customers/register";

        Register userData = new Register(nameR, emailR, passwordR, fuelTypeR);
        Gson gson = new Gson();
        String userJson = gson.toJson(userData);

        JSONObject user = new JSONObject(userJson);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                user,
                (Response.Listener<JSONObject>) response -> {
                    dialog.hide();
                    Intent i = new Intent(RegisterActivity.this, Login.class);

                    startActivity(i);
                },
                (Response.ErrorListener) error -> {
                    System.out.println(error.getLocalizedMessage());
                    dialog.hide();
                    Toast.makeText(RegisterActivity.this, "Some error occurred! Cannot Register", Toast.LENGTH_LONG).show();
                    Log.e("RegisterActivity", "error.getLocalizedMessage()");
                }
        );
        volleyQueue.add(jsonObjectRequest);
    }
}