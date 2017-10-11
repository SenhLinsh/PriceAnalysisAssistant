package com.linsh.paa.tools;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/03
 *    desc   : Realm 数据库迁移/升级时需要声明的变化
 * </pre>
 */
public class PaaMigration implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        Log.i("LshLog", "Shiyi 数据库更新 --- oldVersion = " + oldVersion + ",  newVersion = " + newVersion);
        RealmSchema schema = realm.getSchema();
        switch ((int) oldVersion) {
            case 0:
                schema.create("Tag")
                        .addField("name", String.class)
                        .addPrimaryKey("name")
                        .addField("sort", int.class);
                break;
        }
    }

}
