package com.zd.miko.riji;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import io.realm.Realm;

public class MyApp extends Application {

    private static Context context;

    //返回
    public static Context getContextObject(){
        return context;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Realm.init(this);
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}