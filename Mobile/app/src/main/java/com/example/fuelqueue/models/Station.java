package com.example.fuelqueue.models;

import java.util.Date;

public class Station {

    private String name, arrivalTime, finishTime, location, fuelType, status;

    public Station(String name, String arrivalTime, String finishTime, String location, String fuelType, String status){
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.finishTime = finishTime;
        this.location = location;
        this.fuelType = fuelType;
        this.status = status;
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

    public String getFuelType() {
        return fuelType;
    }

    public String getStatus() {
        return status;
    }
}
