package com.linsh.paa.task.db;

import com.linsh.paa.model.action.AsyncTransaction;
import com.linsh.paa.model.bean.db.Item;
import com.linsh.paa.model.bean.db.ItemHistory;
import com.linsh.paa.model.result.Result;
import com.linsh.paa.tools.BeanHelper;
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

    public static Flowable<Result> createItem(Realm realm, Item item, ItemHistory history) {
        return LshRxUtils.getAsyncTransactionFlowable(realm, new AsyncTransaction<Result>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Result> emitter) {
                Item result = realm.where(Item.class).equalTo("id", item.getId()).findFirst();
                if (result == null) {
                    realm.copyToRealm(item);
                    if (history != null) {
                        realm.copyToRealm(history);
                    }
                    emitter.onNext(new Result());
                } else {
                    emitter.onNext(new Result("该宝贝已存在"));
                }
            }
        });
    }

    public static Flowable<Boolean> updateItem(Realm realm, Item item, ItemHistory history) {
        return LshRxUtils.getAsyncTransactionFlowable(realm, new AsyncTransaction<Boolean>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Boolean> emitter) {
                realm.copyToRealmOrUpdate(item);
                if (history != null) {
                    RealmResults<ItemHistory> results = realm.where(ItemHistory.class)
                            .equalTo("id", history.getId()).findAllSorted("timestamp");
                    if (results.size() > 0) {
                        ItemHistory latestHistory = results.get(results.size() - 1);
                        if (BeanHelper.isSame(latestHistory, history)) {
                            if (history.getTimestamp() - latestHistory.getTimestamp() > 1000L * 60 * 60 * 12) {
                                emitter.onNext(false);
                                return;
                            }
                        }
                    }
                    realm.copyToRealm(history);
                }
                emitter.onNext(true);
            }
        });
    }
}
