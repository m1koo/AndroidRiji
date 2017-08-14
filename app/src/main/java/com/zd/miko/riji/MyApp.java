package com.zd.miko.riji;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import java.util.ArrayList;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
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
                .schemaVersion(1)
                .migration(new RealmMigration() {
                    @Override
                    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
                        RealmSchema schema = realm.getSchema();
                        if (oldVersion != 1 && newVersion == 1) {
                            schema.get("RealmDiaryDetailBean").removeField("location")
                                    .addField("longLatitude", String.class)
                                    .addField("locationStr", String.class);
//                            schema.create("RealmArticleWorld").addField()
                            oldVersion = 1;
                        }

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