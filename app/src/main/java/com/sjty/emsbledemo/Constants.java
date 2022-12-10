package com.sjty.emsbledemo;

import static com.hjq.permissions.Permission.ACCESS_COARSE_LOCATION;
import static com.hjq.permissions.Permission.ACCESS_FINE_LOCATION;
import static com.hjq.permissions.Permission.BLUETOOTH_CONNECT;
import static com.hjq.permissions.Permission.BLUETOOTH_SCAN;

/**
 * @Author
 * @Time
 * @Description
 */
public class Constants {
    public static final String[] PERMISSIONS = {
            BLUETOOTH_SCAN,
            BLUETOOTH_CONNECT,
            ACCESS_COARSE_LOCATION,
            ACCESS_FINE_LOCATION
    };
}
