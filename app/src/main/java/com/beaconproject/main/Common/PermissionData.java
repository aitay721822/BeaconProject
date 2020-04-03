package com.beaconproject.main.Common;

import java.io.Serializable;

public class PermissionData implements Serializable {
    private String userId;
    private String ActivityId;
    private String EditPermission ;
    private String DeletePermission ;

    public PermissionData(String userId, String ActivityId, String EditPermission, String DeletePermission) {
        this.ActivityId = ActivityId;
        this.userId = userId;
        this.EditPermission = EditPermission;
        this.DeletePermission = DeletePermission;
    }

    public String getUserId() {
        return userId;
    }
    public String getActivityId() {
        return ActivityId;
    }
    public String getEditPermission() {
        return EditPermission;
    }
    public String getDeletePermission() {
        return DeletePermission;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setActivityId(String activityId) {
        this.ActivityId = activityId;
    }
    public void setEditPermission(String editPermission) {
        this.EditPermission = editPermission;
    }
    public void setDeletePermission(String deletePermission) {
        this.DeletePermission = deletePermission;
    }
}
