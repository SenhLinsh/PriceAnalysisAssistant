package com.linsh.paa.task.db;

import com.linsh.paa.model.action.AsyncRealmConsumer;
import com.linsh.paa.model.action.AsyncTransaction;
import com.linsh.paa.model.bean.db.Item;
import com.linsh.paa.model.bean.db.ItemHistory;
import com.linsh.paa.model.bean.db.Tag;
import com.linsh.paa.model.result.Result;
import com.linsh.paa.tools.BeanHelper;
import com.linsh.paa.tools.LshRxUtils;

import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/03
 *    desc   : 处理数据库增删改查
 * </pre>
 */
public class PaaDbHelper {

    public static RealmResults<Item> getItems(Realm realm) {
        return realm.where(Item.class).findAllSortedAsync("sort", Sort.DESCENDING);
    }

    public static RealmResults<Tag> getTags(Realm realm) {
        return realm.where(Tag.class).findAllSortedAsync("sort");
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
                    Number number = realm.where(Item.class).max("sort");
                    item.setSort(number == null ? 0 : (number.intValue() + 1));
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

    public static Flowable<Result> createTag(Realm realm, Tag tag) {
        return LshRxUtils.getAsyncTransactionFlowable(realm, new AsyncTransaction<Result>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Result> emitter) {
                Number max = realm.where(Tag.class).max("sort");
                tag.setSort((max == null ? 0 : max.intValue()) + 1);
                realm.copyToRealmOrUpdate(tag);
                emitter.onNext(new Result());
            }
        });
    }

    public static Flowable<Result> updateItem(Realm realm, Item item, ItemHistory history) {
        return LshRxUtils.getAsyncTransactionFlowable(realm, new AsyncTransaction<Result>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Result> emitter) {
                realm.copyToRealmOrUpdate(item);
                if (history != null) {
                    RealmResults<ItemHistory> results = realm.where(ItemHistory.class)
                            .equalTo("id", history.getId()).findAllSorted("timestamp");
                    if (results.size() > 0) {
                        ItemHistory latestHistory = results.get(results.size() - 1);
                        if (BeanHelper.isSame(latestHistory, history)) {
                            if (history.getTimestamp() - latestHistory.getTimestamp() < 1000L * 60 * 60 * 12) {
                                emitter.onNext(new Result("短时间内宝贝没有变化的哦"));
                                return;
                            }
                        }
                    }
                    realm.copyToRealm(history);
                }
                emitter.onNext(new Result());
            }
        });
    }

    public static Flowable<Result> deleteItem(Realm realm, String itemId) {
        return LshRxUtils.getAsyncTransactionFlowable(realm, new AsyncTransaction<Result>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Result> emitter) {
                RealmResults<Item> items = realm.where(Item.class).equalTo("id", itemId).findAll();
                RealmResults<ItemHistory> histories = realm.where(ItemHistory.class).equalTo("id", itemId).findAll();
                items.deleteAllFromRealm();
                histories.deleteAllFromRealm();
                emitter.onNext(new Result());
            }
        });
    }

    public static Flowable<Boolean> hasItem(String itemId) {
        return LshRxUtils.getAsyncRealmFlowable(new AsyncRealmConsumer<Boolean>() {
            @Override
            public void call(Realm realm, FlowableEmitter<? super Boolean> emitter) {
                emitter.onNext(realm.where(Item.class).equalTo("id", itemId).findFirst() != null);
                emitter.onComplete();
            }
        });
    }
}
