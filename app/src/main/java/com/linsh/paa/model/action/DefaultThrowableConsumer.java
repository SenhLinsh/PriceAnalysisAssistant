package com.linsh.paa.model.action;

import com.linsh.lshapp.common.tools.HttpErrorCatcher;
import com.linsh.lshutils.utils.Basic.LshToastUtils;
import com.linsh.paa.model.throwable.CustomThrowable;

import io.reactivex.functions.Consumer;


/**
 * Created by Senh Linsh on 17/4/28.
 */

public class DefaultThrowableConsumer implements Consumer<Throwable> {

    @Override
    public void accept(Throwable throwable) {
        showThrowableMsg(throwable);
    }

    public static void showThrowableMsg(Throwable throwable) {
        throwable.printStackTrace();
        if (HttpErrorCatcher.isHttpError(throwable)) {
            LshToastUtils.show(HttpErrorCatcher.dispatchError(throwable));
        } else if (throwable instanceof CustomThrowable) {
            LshToastUtils.show(throwable.getMessage());
        } else {
            LshToastUtils.show(throwable.getMessage());
        }
    }
}
