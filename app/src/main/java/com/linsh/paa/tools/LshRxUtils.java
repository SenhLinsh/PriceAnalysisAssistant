package com.linsh.paa.tools;


import com.linsh.paa.model.action.AsyncRealmConsumer;
import com.linsh.paa.model.action.AsyncTransaction;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Emitter;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public class LshRxUtils {

    public static <T> Flowable<T> create(FlowableOnSubscribe<T> source) {
        return Flowable.create(source, BackpressureStrategy.ERROR);
    }

    public static <T> Flowable<T> getDoNothingFlowable() {
        return Flowable.create(Emitter::onComplete, BackpressureStrategy.ERROR);
    }

    public static <T> Flowable<T> getDoNothingFlowable(T onNext) {
        return Flowable.create(e -> {
            e.onNext(onNext);
            e.onComplete();
        }, BackpressureStrategy.ERROR);
    }

    public static <T> Flowable<T> getAsyncFlowable(final FlowableOnSubscribe<T> action) {
        return Flowable.create(action, BackpressureStrategy.ERROR).subscribeOn(Schedulers.io());
    }

    public static <T> Flowable<T> getAsyncTransactionFlowable(final Realm realm, final AsyncTransaction<T> transaction) {
        return Flowable.create(new FlowableOnSubscribe<T>() {
            @Override
            public void subscribe(FlowableEmitter<T> e) throws Exception {
                transaction.setSubscriber(e);
                realm.executeTransactionAsync(transaction,
                        new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                e.onComplete();
                            }
                        }, new Realm.Transaction.OnError() {
                            @Override
                            public void onError(Throwable error) {
                                e.onError(error);
                            }
                        });
            }
        }, BackpressureStrategy.ERROR).observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Flowable<T> getAsyncRealmFlowable(final AsyncRealmConsumer<T> action1) {
        return Flowable.create(new FlowableOnSubscribe<T>() {
            @Override
            public void subscribe(FlowableEmitter<T> emitter) throws Exception {
                Realm bgRealm = Realm.getDefaultInstance();
                try {
                    action1.call(bgRealm, emitter);
                } catch (final Throwable e) {
                    e.printStackTrace();
                    emitter.onError(e);
                } finally {
                    bgRealm.close();
                }
                emitter.onComplete();
            }
        }, BackpressureStrategy.ERROR).subscribeOn(Schedulers.io());
    }

    public static Flowable<Realm> getAsyncRealmFlowable() {
        return Flowable.create(new FlowableOnSubscribe<Realm>() {
            @Override
            public void subscribe(FlowableEmitter<Realm> emitter) throws Exception {
                Realm bgRealm = Realm.getDefaultInstance();
                try {
                    emitter.onNext(bgRealm);
                } catch (final Throwable e) {
                    e.printStackTrace();
                    emitter.onError(e);
                } finally {
                    bgRealm.close();
                }
                emitter.onComplete();
            }
        }, BackpressureStrategy.ERROR).subscribeOn(Schedulers.io());
    }
}
