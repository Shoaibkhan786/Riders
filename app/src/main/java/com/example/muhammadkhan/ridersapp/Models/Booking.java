package com.example.muhammadkhan.ridersapp.Models;

import java.io.Serializable;
import java.net.PortUnreachableException;

/**
 * Created by Muhammad Khan on 21/04/2018.
 */

public class Booking implements Serializable{
    private String name;
    private String profileImage;
    private String timeToPick;
    private String timeToReceive;
    private String startingDate;
    private String endingDate;
    private String vehicleType;
    private String modelName;
    private String currentLocation;
    private String remainingDays;
    private String contact;
    private String pricePerKilometer;
    private String status;
    private String empty;
    private String destinationAddress;
    private String currentAddress;

    public Booking(){
        //Empty constructor for dataSnapshot
    }
    //Driver will see this information
    public Booking(String name,String passengerImage,String passengerLocation, String currentAddress,
                   String destinationAddress,String remainingDays,String timeToPick,String timeToReceive,
                   String contact,String status,String startingDate,String endingDate,String pricePerKilometer,String empty){
        this.name=name;
        this.profileImage=passengerImage;
        this.currentLocation=passengerLocation;
        this.currentAddress=currentAddress;
        this.destinationAddress=destinationAddress;
        this.remainingDays=remainingDays;
        this.timeToPick=timeToPick;
        this.timeToReceive=timeToReceive;
        this.contact=contact;
        this.status=status;
        this.startingDate=startingDate;
        this.endingDate=endingDate;
        this.pricePerKilometer=pricePerKilometer;
        this.empty=empty;
    }
    //Passenger will see this information
    public Booking(String name,String driverImage,String driverLocation,String remainingDays,
                   String timeToPick,String timeToReceive,String contact, String modelName,String vehicleType,
                   String pricePerKilometer,String status,String startingDate,String endingDate){
        this.name=name;
        this.profileImage=driverImage;
        this.currentLocation=driverLocation;
        this.remainingDays=remainingDays;
        this.timeToPick=timeToPick;
        this.timeToReceive=timeToReceive;
        this.contact=contact;
        this.modelName=modelName;
        this.vehicleType=vehicleType;
        this.pricePerKilometer=pricePerKilometer;
        this.status=status;
        this.startingDate=startingDate;
        this.endingDate=endingDate;
    }
    //setters
    public void setName(String name) {
        this.name = name;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void setTimeToPick(String timeToPick) {
        this.timeToPick = timeToPick;
    }

    public void setTimeToReceive(String timeToReceive) {
        this.timeToReceive = timeToReceive;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public void setEndingDate(String endingDate) {
        this.endingDate = endingDate;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public void setRemainingDays(String remainingDays) {
        this.remainingDays = remainingDays;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setPricePerKilometer(String pricePerKilometer) {
        this.pricePerKilometer = pricePerKilometer;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getName() {
        return name;
    }
    //getters
    public String getEmpty(){
        return empty;
    }
    public String getProfileImage() {
        return profileImage;
    }

    public String getTimeToPick() {
        return timeToPick;
    }

    public String getTimeToReceive() {
        return timeToReceive;
    }

    public String getStartingDate() {
        return startingDate;
    }

    public String getEndingDate() {
        return endingDate;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public String getModelName() {
        return modelName;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public String getRemainingDays() {
        return remainingDays;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public String getContact() {
        return contact;
    }

    public String getPricePerKilometer() {
        return pricePerKilometer;
    }

    public String getStatus() {
        return status;
    }
}
