package com.beaconproject.main.Common;

import android.widget.Toast;

import java.nio.file.attribute.DosFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordData  {
    private String activityId;
    private String userId;
    private String isSignIn ;
    private String username ;
    private String attendDateTime ;
    private String notice ;

    public RecordData() {
    }

    public RecordData(String activityId, String userId, String isSignIn, String username,String attendDateTime,String notice) {
        this.activityId = activityId;
        this.userId=userId;
        this.isSignIn = isSignIn;
        this.username = username;
        this.attendDateTime=attendDateTime;
        this.notice=notice;
    }

    public String getActivityId() {
        return activityId;
    }
    public String getUserId() {
        return userId;
    }
    public String getIsSignIn() {
        return isSignIn;
    }
    public String getUsername() {
        return  username;
    }
    public String getAttendDateTime() {
        return attendDateTime;
    }
    public String getNotice() {
        return notice;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setIsSignIn(String isSignIn) {
        this.isSignIn = isSignIn;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setAttendDateTime(String AttendDateTime) {
        this.attendDateTime = AttendDateTime;
    }
    public void setNotice(String notice) {
        this.notice = notice;
    }

    public boolean equals(RecordData obj) {
        if (obj == null) return false;
        if (obj.getUserId()==this.getUserId() && obj.getIsSignIn()==this.getIsSignIn() && obj.getActivityId() == this.getActivityId() && obj.getNotice()==this.getNotice() && obj.getAttendDateTime()==this.getAttendDateTime() && obj.getUsername()==this.getUsername()){
            return true;
        }
        else return false;
    }
}
