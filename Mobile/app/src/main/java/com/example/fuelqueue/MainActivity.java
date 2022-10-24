package com.example.fuelqueue;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ListView listView;

    String stationName, location;

    private static String STATION_LIST_URL = "https://run.mocky.io/v3/e7a54a90-973e-485a-91ce-5d7da9905827";

    ArrayList<HashMap<String, String>> stationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stationList = new ArrayList<>();
        listView= findViewById(R.id.listview);
    }

    public class GetData extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            String current = "";

            try {
                URL url;
                HttpURLConnection urlConnection = null;

                try {
                    url = new URL(STATION_LIST_URL);
                    urlConnection = (HttpURLConnection) url.openConnection();

                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(in);

                    int data = isr.read();
                    while (data != -1) {

                        current += (char) data;
                        data = isr.read();
                    }

                    return current;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
                return  current;
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("Users");

                for (int i = 0; i< jsonArray.length(); i++){
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    stationName = jsonObject1.getString("name");
                    location = jsonObject1.getString("username");

                    HashMap<String, String > fuelStations = new HashMap<>();
                    fuelStations.put("name", stationName);
                    fuelStations.put("username", location);

                    stationList.add(fuelStations);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Display results
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this,
                    stationList,
                    R.layout.station_list,
                    new String[] {"name", "username"},
                    new int[]{R.id.textView, R.id.textView2});

            listView.setAdapter(adapter);
        }
    }
}