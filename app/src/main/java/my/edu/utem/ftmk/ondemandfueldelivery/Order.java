package my.edu.utem.ftmk.ondemandfueldelivery;


import com.google.firebase.Timestamp;

import java.util.Date;

public class Order {
    String Address;
    String Email;
    String FullName;
    String Password;
    String PhoneNum;
    String PictureURL;
    String addresslatlng;
    /* String currentWorkerLatitude;
     String currentWorkerLongitude;*/
    Timestamp date;
    String orderNumber;
    String orderStatus;

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhoneNum() {
        return PhoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        PhoneNum = phoneNum;
    }

    public String getPictureURL() {
        return PictureURL;
    }

    public void setPictureURL(String pictureURL) {
        PictureURL = pictureURL;
    }

    public String getAddresslatlng() {
        return addresslatlng;
    }

    public void setAddresslatlng(String addresslatlng) {
        this.addresslatlng = addresslatlng;
    }
/*
    public String getCurrentWorkerLatitude() {
        return currentWorkerLatitude;
    }

    public void setCurrentWorkerLatitude(String currentWorkerLatitude) {
        this.currentWorkerLatitude = currentWorkerLatitude;
    }

    public String getCurrentWorkerLongitude() {
        return currentWorkerLongitude;
    }

    public void setCurrentWorkerLongitude(String currentWorkerLongitude) {
        this.currentWorkerLongitude = currentWorkerLongitude;
    }*/

    public Timestamp getDate() {

        return date;
    }

    public void setDate(Timestamp date) {

        this.date = date;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPetrolstation() {
        return petrolstation;
    }

    public void setPetrolstation(String petrolstation) {
        this.petrolstation = petrolstation;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPriceunit() {
        return priceunit;
    }

    public void setPriceunit(String priceunit) {
        this.priceunit = priceunit;
    }

    public String getTypeoffuel() {
        return typeoffuel;
    }

    public void setTypeoffuel(String typeoffuel) {
        this.typeoffuel = typeoffuel;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getWaypointid() {
        return waypointid;
    }

    public void setWaypointid(String waypointid) {
        this.waypointid = waypointid;
    }

    public String getWorkerid() {
        return workerid;
    }

    public void setWorkerid(String workerid) {
        this.workerid = workerid;
    }

    public String getWorkername() {
        return workername;
    }

    public void setWorkername(String workername) {
        this.workername = workername;
    }

    String petrolstation;
    String plateNumber;
    String price;
    String priceunit;
    String typeoffuel;
    String userType;
    String userid;
    String waypointid;
    String workerid;
    String workername;

}

