package com.zd.miko;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;

public class MyCustomSchema implements RealmMigration {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

        }
    }