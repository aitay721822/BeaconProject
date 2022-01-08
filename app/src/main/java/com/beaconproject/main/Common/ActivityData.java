package com.beaconproject.main.Common;

import android.widget.Toast;

import java.nio.file.attribute.DosFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityData {
    private String activityId;
    private String activityName;
    private String activityLocation ;
    private String activityNote ;
    private String activityCreationDate ;
    private String activityStartDate ;
    private String activityEndDate ;
    private String BeaconUUID;

    public ActivityData() {
    }

    public ActivityData(String activityId, String activityName, String activityLocation, String activityNote,String activityCreationDate,String activityStartDate,String activityEndDate,String BeaconUUID) {
        this.activityId = activityId;
        this.activityName=activityName;
        this.activityLocation = activityLocation;
        this.activityNote = activityNote;
        this.activityCreationDate=activityCreationDate;
        this.activityStartDate=activityStartDate;
        this.activityEndDate=activityEndDate;
        this.BeaconUUID = BeaconUUID;
    }
    public String getActivityId() {
        return activityId;
    }
    public String getActivityName() {
        return activityName;
    }
    public String getActivityLocation() {
        return activityLocation;
    }
    public String getActivityNote() {
        return  activityNote;
    }
    public String getActivityCreationDate() {
        return activityCreationDate;
    }
    public String getActivityStartDate() {
        return activityStartDate;
    }
    public String getActivityEndDate() {
        return activityEndDate;
    }
    public String getBeaconUUID() {
        return  BeaconUUID;
    }

    public boolean validCheckInTime(){
        Date curr = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try{
            Date startDate = sdf.parse(activityStartDate);
            Date endDate = sdf.parse(activityEndDate);
            if(curr.after(startDate) && curr.before(endDate))
                return true;
            else
                return false;
        }
        catch(ParseException ex){
            ex.printStackTrace();
            return false;
        }
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }
    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
    public void setActivityLocation(String activityLocation) {
        this.activityLocation = activityLocation;
    }
    public void setActivityNote(String activityNote) {
        this.activityNote = activityNote;
    }
    public void setActivityCreationDate(String activityCreationDate) {
        this.activityCreationDate = activityCreationDate;
    }
    public void setActivityStartDate(String activityStartDate) {
        this.activityStartDate = activityStartDate;
    }
    public void setActivityEndDate(String activityEndDate) {
        this.activityEndDate = activityEndDate;
    }
    public void setBeaconUUID(String beaconUUID) {
        this.BeaconUUID = beaconUUID;
    }

    public boolean equals(ActivityData obj) {
        if (obj == null) return false;
        if (obj.BeaconUUID==this.getBeaconUUID() && obj.activityEndDate==this.activityEndDate && obj.activityStartDate == this.activityStartDate && obj.activityCreationDate==this.activityCreationDate && obj.activityNote==this.activityNote &&
                obj.activityLocation==this.getActivityLocation() &&obj.activityId== this.getActivityId() && obj.activityName==this.getActivityName()){
            return true;
        }
        else return false;
    }
}
