package com.beaconproject.main.Common;

import android.provider.Contacts;

import java.io.Serializable;

public class PeopleData implements Serializable {
    private String Name;
    private String Notice;
    private String CheckInTime ;
    private String userID ;

    public PeopleData(){

    }


    public PeopleData(String name, String notice, String checkInTime, String userid) {
        Name = name;
        Notice = notice;
        CheckInTime = checkInTime;
        userID = userid;
    }

    public PeopleData(String name, String notice) {
        Name = name;
        Notice = notice;
        CheckInTime = null;
        userID = null;
    }

    public String getName() {
        return Name;
    }
    public String getNotice() {
        return Notice;
    }
    public String getCheckInTime() {
        return CheckInTime;
    }
    public String getUserID() {
        return userID;
    }

    public void setName(String name) {
        Name = name;
    }
    public void setNotice(String notice) {
        Notice = notice;
    }
    public void setCheckInTime(String checkInTime) {
        CheckInTime = checkInTime;
    }
    public void setUserID(String userid) {
        userID = userid;
    }

    public boolean equals(PeopleData obj) {
        if (obj == null) return false;
        if (obj.getNotice()==this.getNotice() && obj.getName()==this.getName() && obj.getUserID() == this.getUserID() && obj.getCheckInTime()==this.getCheckInTime()){
            return true;
        }
        else return false;
    }
}
