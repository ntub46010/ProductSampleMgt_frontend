package com.vincent.psm.broadcast_helper.data;

import java.io.Serializable;

public class Device implements Serializable {
    private static final long serialVersionUID = 1L;
    private String platform;
    private String token;

    public Device() {

    }

    public Device(String token) {
        this.platform = "android";
        this.token = token;
    }

    public String getPlatform() {
        return platform;
    }

    public String getToken() {
        return token;
    }
}
