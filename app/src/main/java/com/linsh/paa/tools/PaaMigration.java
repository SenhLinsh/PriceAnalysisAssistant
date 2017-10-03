package com.linsh.paa.tools;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;

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

    }

}
