package com.example.fuelqueue;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyStationSingleton {

    private RequestQueue requestQueue;
    private static VolleyStationSingleton mInstance;

    private VolleyStationSingleton(Context context){
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized VolleyStationSingleton getmInstance(Context context){
        if(mInstance == null){
            mInstance = new VolleyStationSingleton(context);
        }
        return  mInstance;
    }

    public RequestQueue getRequestQueue(){
        return requestQueue;
    }
}
