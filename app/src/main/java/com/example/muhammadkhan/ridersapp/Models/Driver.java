package com.example.muhammadkhan.ridersapp.Models;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Muhammad Khan on 29/03/2018.
 */

public class Driver implements Serializable{

    private String driverName;
    private String profileImage;
    private String space;
    private String vehicleType;
    private String pricePerKilometer;
    private String totalCapacity;
    private String modelName;
    private String driverCity;
    private String driverContact;
    private String currentLocation;
    public Driver(){
        // Empty constructor for Firebase dataSnapShot
    }
    public Driver(String driverName,String profileImage,String modelName,String totalCapacity,String space
            ,String pricePerKilometer,String vehicleType,String driverCity,String driverContact,String currentLocation){
        this.driverName=driverName;
        this.profileImage=profileImage;
        this.modelName=modelName;
        this.totalCapacity=totalCapacity;
        this.space=space;
        this.pricePerKilometer=pricePerKilometer;
        this.vehicleType=vehicleType;
        this.driverCity=driverCity;
        this.driverContact=driverContact;
        this.currentLocation=currentLocation;
    }
    //getters
    public String getVehicleType(){
        return vehicleType;
    }
    public String getDriverCity(){
        return driverCity;
    }
    public String getDriverName() {
        return driverName;
    }
    public String getCurrentLocation(){
        return currentLocation;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getSpace() {
        return space;
    }

    public String getPricePerKilometer() {
        return pricePerKilometer;
    }
    public String getDriverContact(){
        return driverContact;
    }
    public String getTotalCapacity() {
        return totalCapacity;
    }
    public String getModelName(){return modelName;}
    //setters
    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
    public void setSpace(String space) {
        this.space = space;
    }
    public void setTotalCapacity(String totalCapacity){
        this.totalCapacity=totalCapacity;
    }
    public void setPricePerKilometer(String pricePerKilometer) {
        this.pricePerKilometer = pricePerKilometer;
    }

    public void setCurrentLocation(String currentLocation){
        this.currentLocation=currentLocation;
    }
    public void setModelName(String modelName){this.modelName=modelName;}


}
