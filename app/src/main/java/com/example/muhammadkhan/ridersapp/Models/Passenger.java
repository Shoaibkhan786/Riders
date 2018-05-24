package com.example.muhammadkhan.ridersapp.Models;

import java.io.Serializable;

/**
 * Created by Muhammad Khan on 25/03/2018.
 */

public class Passenger implements Serializable{

    private String name;
    private String currentAddress;
    private String destinationAdress;
    private String contact;
    private String city;
    private String location;
    private String profileImage;
    public Passenger(){
        // Empty constructor for Firebase dataSnapShot
    }
    public Passenger(String pName,String pCurrentAddress,String pDestinationAdress,
                     String pContact,String pCity,String pLocation,String profileImage){
        this.name=pName;
        this.currentAddress=pCurrentAddress;
        this.destinationAdress=pDestinationAdress;
        this.contact=pContact;
        this.city=pCity;
        this.location=pLocation;
        this.profileImage=profileImage;
    }
    ///getters
    public String getName() {
        return name;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public String getDestinationAdress() {
        return destinationAdress;
    }

    public String getContact() {
        return contact;
    }

    public String getCity() {
        return city;
    }

    public String getLocation() {
        return location;
    }

    public String getProfileImage() {
        return profileImage;
    }

    //setters
    public void setName(String name) {
        this.name = name;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public void setDestinationAdress(String destinationAdress) {
        this.destinationAdress = destinationAdress;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

}
