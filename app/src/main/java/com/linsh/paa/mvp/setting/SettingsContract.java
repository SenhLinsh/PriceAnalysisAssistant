package com.linsh.paa.mvp.setting;


import com.linsh.lshapp.common.base.BaseContract;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public interface SettingsContract {

    interface View extends BaseContract.BaseView {

    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void checkUpdate();

        void importItems();

        void exportRealm();

        void importRealm();
    }
}
