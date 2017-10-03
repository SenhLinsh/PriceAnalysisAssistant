package com.linsh.paa.mvp.display;


import com.linsh.lshapp.common.base.BaseContract;

/**
 * Created by Senh Linsh on 17/4/25.
 */

interface DisplayContract {

    interface View extends BaseContract.BaseView {

    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void addCurItem(String url);
    }
}
