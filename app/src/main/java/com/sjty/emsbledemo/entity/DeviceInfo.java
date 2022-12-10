package com.sjty.emsbledemo.entity;

/**
 * @Author
 * @Time
 * @Description
 */
public class DeviceInfo {
    //device name
    private String name;
    //device mac address
    private String mac;
    //device connection status
    private boolean isConn;

    public DeviceInfo(String name, String mac) {
        this.name = name;
        this.mac = mac;
        this.isConn = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public boolean isConn() {
        return isConn;
    }

    public void setConn(boolean conn) {
        isConn = conn;
    }
}
