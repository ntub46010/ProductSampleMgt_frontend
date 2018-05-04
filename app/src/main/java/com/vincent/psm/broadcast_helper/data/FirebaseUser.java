package com.vincent.psm.broadcast_helper.data;

import java.util.ArrayList;

public class FirebaseUser {
    public static final String DATABASE_USERS = "users";
    private ArrayList<Device> deviceList;

    public FirebaseUser() {
        this.deviceList = new ArrayList<>();
    }

    public void addDevice(Device device) {
        deviceList.add(device);
    }

    public ArrayList<Device> getDeviceList() {
        return deviceList;
    }
}
