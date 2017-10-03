package com.linsh.paa.model.action;

import com.linsh.lshapp.common.tools.HttpErrorCatcher;
import com.linsh.lshutils.utils.Basic.LshToastUtils;

import io.reactivex.functions.Consumer;


/**
 * Created by Senh Linsh on 17/4/28.
 */

public class HttpThrowableConsumer implements Consumer<Throwable> {

    @Override
    public void accept(Throwable throwable) {
        showThrowableMsg(throwable);
    }

    public static void showThrowableMsg(Throwable throwable) {
        throwable.printStackTrace();
        LshToastUtils.show(HttpErrorCatcher.dispatchError(throwable));
    }
}
