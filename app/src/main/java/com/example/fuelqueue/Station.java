package com.example.fuelqueue;

import java.util.Date;

public class Station {

    private String name, arrivalTime, finishTime, location;

    public Station(String name, String arrivalTime, String finishTime, String location){
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.finishTime = finishTime;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public String getLocation() {
        return location;
    }
}
