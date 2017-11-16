package com.linsh.paa.task.db;

import com.linsh.paa.model.action.AsyncRealmConsumer;
import com.linsh.paa.model.action.AsyncTransaction;
import com.linsh.paa.model.bean.db.Item;
import com.linsh.paa.model.bean.db.ItemHistory;
import com.linsh.paa.model.bean.db.Tag;
import com.linsh.paa.model.result.Result;
import com.linsh.paa.tools.LshRxUtils;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.functions.Consumer;
import io.realm.Realm;
import io.realm.RealmQuery;
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

    public static Item getItem(Realm realm, String id) {
        return realm.where(Item.class).equalTo("id", id).findFirstAsync();
    }

    public static RealmResults<Item> getItems(Realm realm) {
        return realm.where(Item.class).findAllSortedAsync("sort", Sort.DESCENDING);
    }

    public static RealmResults<Item> getItems(Realm realm, String platformCode, String tag, String display, long lastModify) {
        RealmQuery<Item> where = realm.where(Item.class);
        if (platformCode != null) where.beginsWith("id", platformCode);
        if (tag != null) where.equalTo("tag", tag.equals("无标签") ? null : tag);
        if (display != null) where.contains("display", display);
        if (lastModify > 0) where.lessThan("lastModified", lastModify);
        return where.findAllSortedAsync("sort", Sort.DESCENDING);
    }

    public static RealmResults<Tag> getTags(Realm realm) {
        return realm.where(Tag.class).findAllSortedAsync("sort");
    }

    public static RealmResults<ItemHistory> getItemHistories(Realm realm, String id) {
        return realm.where(ItemHistory.class).equalTo("id", id).findAllSortedAsync("timestamp");
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

    public static void updateItem(Item item, ItemHistory history) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        if (item != null) {
            realm.copyToRealmOrUpdate(item);
        }
        if (history != null) {
            realm.copyToRealm(history);
        }
        realm.commitTransaction();
    }

    public static Result updateItems(List<Object[]> toSaves) {
        boolean changed = false;
        int count = 0;
        if (toSaves != null && toSaves.size() > 0) {
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            for (Object[] toSave : toSaves) {
                Item item = (Item) toSave[0];
                if (item != null) {
                    realm.copyToRealmOrUpdate(item);
                }
                ItemHistory history = (ItemHistory) toSave[1];
                if (history != null) {
                    realm.copyToRealm(history);
                }
                if (item != null || history != null) {
                    changed = true;
                    count++;
                }
            }
            realm.commitTransaction();
        }
        return changed ? new Result(true, "更新了" + count + "件宝贝") : new Result("短时间内宝贝没有变化的哦");
    }

    public static Flowable<Result> updateItem(Realm realm, String id, Consumer<Item> consumer) {
        return LshRxUtils.getAsyncTransactionFlowable(realm, new AsyncTransaction<Result>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Result> emitter) {
                Item item = realm.where(Item.class).equalTo("id", id).findFirst();
                if (item != null) {
                    try {
                        consumer.accept(item);
                        emitter.onNext(new Result());
                    } catch (Exception e) {
                        emitter.onError(e);
                    }
                } else {
                    emitter.onNext(new Result("没有找到该宝贝"));
                }
            }
        });
    }

    public static Flowable<Result> deleteItem(Realm realm, String id) {
        return LshRxUtils.getAsyncTransactionFlowable(realm, new AsyncTransaction<Result>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Result> emitter) {
                RealmResults<Item> items = realm.where(Item.class).equalTo("id", id).findAll();
                RealmResults<ItemHistory> histories = realm.where(ItemHistory.class).equalTo("id", id).findAll();
                items.deleteAllFromRealm();
                histories.deleteAllFromRealm();
                emitter.onNext(new Result());
            }
        });
    }

    public static Flowable<Boolean> hasItem(String id) {
        return LshRxUtils.getAsyncRealmFlowable(new AsyncRealmConsumer<Boolean>() {
            @Override
            public void call(Realm realm, FlowableEmitter<? super Boolean> emitter) {
                Item item = realm.where(Item.class).equalTo("id", id).findFirst();
                emitter.onNext(item != null);
                emitter.onComplete();
            }
        });
    }
}
