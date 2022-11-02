package com.example.fuelqueue;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
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

        SQLiteDatabase db = openOrCreateDatabase("FuelManagement",MODE_PRIVATE,null);
        Cursor tableExistsResultSet = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='User'",null);
        tableExistsResultSet.moveToFirst();
        Integer tableCount = tableExistsResultSet.getCount();
        if (tableCount == 1) {
            Cursor resultSet = db.rawQuery("Select * from User",null);
            resultSet.moveToFirst();
            String userType = resultSet.getString(1);
            System.out.println(userType);
            Intent i;
            if (userType == "customers") {
                i = new Intent(Login.this, StationList.class);
            } else {
                i = new Intent(Login.this, OwnerActivity.class);
            }
            startActivity(i);
        } else {
            db.execSQL("CREATE TABLE IF NOT EXISTS User(id VARCHAR,Type VARCHAR);");
        }

        TextView btnLogin = findViewById(R.id.btnLogin);
        TextView DoNotHaveAccountTextView = findViewById(R.id.DoNotHaveAccountTextView);

        DoNotHaveAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, RegisterActivity.class);
                startActivity(i);
            }
        });
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
        CheckBox checkBox = findViewById(R.id.checkbox);

        String userType = checkBox.isChecked() ? "owners" : "customers";
        String url = String.format("https://fuel-management-api.herokuapp.com/%s/login", userType);

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
                        JSONObject jsonObject = response.getJSONObject("user");
                        String id = (String) jsonObject.get("id");
                        SQLiteDatabase db = openOrCreateDatabase("FuelManagement",MODE_PRIVATE,null);
                        db.execSQL("DROP TABLE IF EXISTS "+"User");
                        db.execSQL("CREATE TABLE IF NOT EXISTS User(id VARCHAR,Type VARCHAR);");
                        String query = String.format("INSERT INTO User VALUES(\'%s\',\'%s\');", id, userType);
                        db.execSQL(query);
                        Intent i;
                        if (checkBox.isChecked()) {
                            i = new Intent(Login.this, OwnerActivity.class);
                        } else {
                            i = new Intent(Login.this, StationList.class);
                        }

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