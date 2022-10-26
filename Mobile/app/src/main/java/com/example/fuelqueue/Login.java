package com.example.fuelqueue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fuelqueue.models.Owner;
import com.example.fuelqueue.models.User;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        TextView btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    login();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void login() throws JSONException {
        RequestQueue volleyQueue = Volley.newRequestQueue(Login.this);
        String url = "https://fuel-management-api.herokuapp.com/owners/login";

        EditText password = findViewById(R.id.password);
        EditText email = findViewById(R.id.email);

        User userData = new User(email.getText().toString(), password.getText().toString());
        Gson gson = new Gson();
        String userJson = gson.toJson(userData);

        JSONObject user = new JSONObject(userJson);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                user,
                (Response.Listener<JSONObject>) response -> {
                    Intent i = new Intent(Login.this, OwnerActivity.class);
                    startActivity(i);
                },
                (Response.ErrorListener) error -> {
                    System.out.println(error);
                    Toast.makeText(Login.this, "Some error occurred! Cannot update owner data", Toast.LENGTH_LONG).show();
                    Log.e("MainActivity", "Update owner error: ${error.localizedMessage}");
                }
        );
        volleyQueue.add(jsonObjectRequest);
    }
}