package com.beaconproject.main.Common;

public class BeaconData {

    private String UUID;
    private String Minor ;
    private String Major ;
    private String MacAddress ;
    public BeaconData() {
    }
    public BeaconData(String uuid, String minor, String major, String tx) {
        UUID = uuid;
        Minor = minor;
        Major = major;
        MacAddress = tx;
    }
    public String getUUID() {
        return UUID;
    }
    public String getMinor() {
        return Minor;
    }
    public String getMajor() {
        return Major;
    }
    public String getMacAddress() {
        return MacAddress;
    }
    public void setUUID(String uuid) {
        UUID = uuid;
    }
    public void setMinor(String minor) {
        Minor = minor;
    }
    public void setMajor(String major) {
        Major = major;
    }
    public void setMacAddress(String mac) {
        MacAddress = mac;
    }
}
