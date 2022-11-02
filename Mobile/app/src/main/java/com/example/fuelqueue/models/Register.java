package com.example.fuelqueue.models;

public class Register {
    String Name;
    String Email;
    String Password;
    String VehicleType;

    public Register(String Name, String Email, String Password, String VehicleType) {
        this.Name = Name;
        this.Email = Email;
        this.Password = Password;
        this.VehicleType = VehicleType;
    }
}
