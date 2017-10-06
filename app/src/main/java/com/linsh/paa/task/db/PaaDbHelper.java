package com.linsh.paa.task.db;

import com.linsh.paa.model.action.AsyncTransaction;
import com.linsh.paa.model.bean.db.Item;
import com.linsh.paa.model.bean.db.ItemHistory;
import com.linsh.paa.tools.LshRxUtils;

import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/03
 *    desc   : 处理数据库增删改查
 * </pre>
 */
public class PaaDbHelper {

    public static RealmResults<Item> getItems(Realm realm) {
        return realm.where(Item.class).findAllAsync();
    }

    public static RealmResults<ItemHistory> getItemHistories(Realm realm, String itemId) {
        return realm.where(ItemHistory.class).equalTo("id", itemId).findAllSortedAsync("timestamp");
    }

    public static Flowable<Boolean> saveItemAndHistory(Realm realm, Item item, ItemHistory history) {
        return LshRxUtils.getAsyncTransactionFlowable(realm, new AsyncTransaction<Boolean>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Boolean> emitter) {
                if (item != null) {
                    realm.copyToRealmOrUpdate(item);
                }
                if (history != null) {
                    realm.copyToRealm(history);
                }
                emitter.onNext(true);
            }
        });
    }
}
