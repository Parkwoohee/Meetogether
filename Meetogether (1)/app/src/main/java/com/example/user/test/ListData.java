package com.example.user.test;

public class ListData {

    public String room;
    public String email;
    public String photo;
    public String key;
    public String peopleNum;
    public String gender;
    public String engLv;
    public String mainPlace;
    public String cost;

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getPeopleNum() {
        return peopleNum;
    }

    public void setPeopleNum(String peopleNum) {
        this.peopleNum = peopleNum;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEngLv() {
        return engLv;
    }

    public void setEngLv(String engLv) {
        this.engLv = engLv;
    }

    public String getMainPlace() {
        return mainPlace;
    }

    public void setMainPlace(String mainPlace) {
        this.mainPlace = mainPlace;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ListData() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}