package com.linsh.paa.model.action;

import io.reactivex.FlowableEmitter;
import io.realm.Realm;

/**
 * Created by Senh Linsh on 17/5/5.
 */

public abstract class AsyncTransaction<T> implements Realm.Transaction {

    private FlowableEmitter<? super T> mEmitter;

    @Override
    public void execute(Realm realm) {
        execute(realm, mEmitter);
        mEmitter.onComplete();
    }

    protected abstract void execute(Realm realm, FlowableEmitter<? super T> emitter);

    public void setSubscriber(FlowableEmitter<? super T> emitter) {
        mEmitter = emitter;
    }
}
