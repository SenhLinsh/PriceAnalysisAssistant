package com.linsh.paa.model.action;

import io.reactivex.FlowableEmitter;
import io.realm.Realm;

/**
 * Created by Senh Linsh on 17/5/5.
 */

public interface AsyncRealmConsumer<T> {


    void call(Realm realm, FlowableEmitter<? super T> emitter);

}
