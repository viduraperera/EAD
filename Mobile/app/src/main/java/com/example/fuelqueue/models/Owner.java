package com.example.fuelqueue.models;

import java.util.Date;

public class Owner {
    String id;
    String name;
    String email;
    String status;
    String fuelType;
    String location;
    String arrivalTime;
    String finishTime;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Owner(String id, String name, String email, String status, String fuelType, String location, String arrivalTime, String finishTime) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = status;
        this.fuelType = fuelType;
        this.arrivalTime = arrivalTime;
        this.finishTime = finishTime;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public Owner() {
    }
}
