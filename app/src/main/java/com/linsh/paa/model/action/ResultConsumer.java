package com.linsh.paa.model.action;

import com.linsh.lshapp.common.base.BaseContract;
import com.linsh.lshutils.utils.Basic.LshToastUtils;
import com.linsh.paa.model.result.Result;

import io.reactivex.functions.Consumer;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/08
 *    desc   :
 * </pre>
 */
public class ResultConsumer implements Consumer<Result> {

    private BaseContract.BaseView mView;

    public ResultConsumer(BaseContract.BaseView view) {
        mView = view;
    }

    @Override
    public void accept(Result result) throws Exception {
        if (mView != null) {
            handleFailedWithDialog(mView, result);
        } else {
            handleFailedWithToast(result);
        }
    }

    public static boolean handleFailedWithDialog(BaseContract.BaseView view, Result result) {
        if (!result.isSuccess()) {
            view.showTextDialog(result.getMessage());
            return true;
        }
        return false;
    }

    public static boolean handleFailedWithToast(Result result) {
        if (!result.isSuccess()) {
            LshToastUtils.show(result.getMessage());
            return true;
        }
        return false;
    }
}
