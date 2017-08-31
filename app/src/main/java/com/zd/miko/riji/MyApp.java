package com.zd.miko.riji;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmSchema;

public class MyApp extends Application {

    public static Context context;

    private static ArrayList<Activity> activities = new ArrayList<>();

    //返回
    public static Context getContextObject() {
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
        RealmConfiguration myConfig = new RealmConfiguration.Builder()
                .schemaVersion(3)
                .migration((realm, oldVersion, newVersion) -> {
                    RealmSchema schema = realm.getSchema();
                    if (oldVersion != 3 && newVersion == 3) {
                        schema.get("RealmArticleWorld")
                                .addField("readUserId",String.class)
                                .addField("hadRead",boolean.class);
                        oldVersion = 3;
                    }

                })
                .build();
        Realm.setDefaultConfiguration(myConfig);
    }

    public static void push(Activity activity) {
        activities.add(activity);
    }

    public static void pop() {
        Activity activity = activities.get(activities.size() - 1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.finishAfterTransition();
        } else {
            activity.finish();
        }
        activities.remove(activities.size() - 1);
        activity = null;
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