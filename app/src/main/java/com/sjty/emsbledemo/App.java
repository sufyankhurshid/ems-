package com.sjty.emsbledemo;


import android.app.Application;

/**
 * Author by SJTY, Email xx@xx.com, Date on 2020/10/30.
 * PS: Not easy to write code, please indicate.
 */
public class App extends Application {

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static App getInstance() {
        if (instance == null) {
            instance = new App();
        }
        return instance;
    }
}
