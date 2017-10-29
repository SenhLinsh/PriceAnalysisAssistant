package com.linsh.paa.tools;

import android.util.Log;

import com.linsh.paa.model.bean.db.Platform;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmMigration;
import io.realm.RealmResults;
import io.realm.RealmSchema;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/03
 *    desc   : Realm 数据库迁移/升级时需要声明的变化
 * </pre>
 */
public class PaaMigration implements RealmMigration {

    public static final int VERSION = 3;

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
                schema.get("Item")
                        .addField("tag", String.class);
            case 1:
                schema.get("Item")
                        .addField("display", String.class)
                        .addField("initialPrice", int.class)
                        .transform(item -> {
                            String itemId = item.get("id");
                            RealmResults<DynamicRealmObject> histories = realm.where("ItemHistory").equalTo("id", itemId).findAllSorted("timestamp");
                            if (histories.size() > 0) {
                                String price = histories.get(0).get("price");
                                int[] prices = TaobaoDataParser.parsePrice(price);
                                item.set("initialPrice", prices[0]);
                            }
                            Long initialPrice = item.get("initialPrice");
                            if (initialPrice == 0) {
                                item.set("initialPrice", TaobaoDataParser.parsePrice(item.get("price"))[0]);
                            }
                        });
                break;
            case 2:
                schema.get("Item")
                        .removePrimaryKey()
                        .transform(item -> {
                            String itemId = item.get("id");
                            String id = BeanHelper.getId(Platform.Taobao, itemId);
                            item.set("id", id);
                        })
                        .addPrimaryKey("id");
                schema.get("ItemHistory")
                        .transform(history -> {
                            String itemId = history.get("id");
                            String id = BeanHelper.getId(Platform.Taobao, itemId);
                            history.set("id", id);
                        });
                break;
        }
    }

}
