package com.vincent.psm.broadcast_helper.data;

import java.io.Serializable;

public class Device implements Serializable {
    private static final long serialVersionUID = 1L;
    private String device;
    private String token;

    public Device() {

    }

    public Device(String token) {
        this.device = "android";
        this.token = token;
    }

    public String getDevice() {
        return device;
    }

    public String getToken() {
        return token;
    }
}
