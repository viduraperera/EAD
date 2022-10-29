package com.example.fuelqueue;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

import java.util.UUID;

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
        ProgressDialog dialog=new ProgressDialog(Login.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();
        RequestQueue volleyQueue = Volley.newRequestQueue(Login.this);
        String url = "https://fuel-management-api.herokuapp.com/customers/login";

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
                    try {
                        dialog.hide();
                        System.out.println(response);
                        JSONObject jsonObject = response.getJSONObject("owner");
                        String id = (String) jsonObject.get("id");
                        String name = (String) jsonObject.get("name");
                        SQLiteDatabase db = openOrCreateDatabase("FuelManagement",MODE_PRIVATE,null);
                        db.execSQL("DROP TABLE IF EXISTS "+"User");
                        String query = String.format("INSERT INTO User VALUES(\'%s\',\'%s\');", id, name);
                        db.execSQL("CREATE TABLE IF NOT EXISTS User(id VARCHAR,Name VARCHAR);");
                        db.execSQL(query);
                        Intent i = new Intent(Login.this, StationList.class);
                        startActivity(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                (Response.ErrorListener) error -> {
                    dialog.hide();
                    Toast.makeText(Login.this, "Some error occurred! Cannot login", Toast.LENGTH_LONG).show();
                    Log.e("MainActivity", "error.getLocalizedMessage()");
                }
        );
        volleyQueue.add(jsonObjectRequest);
    }
}