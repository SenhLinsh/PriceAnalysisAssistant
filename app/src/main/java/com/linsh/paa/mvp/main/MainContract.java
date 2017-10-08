package com.linsh.paa.mvp.main;


import com.linsh.lshapp.common.base.BaseContract;
import com.linsh.paa.model.bean.db.Item;

import java.util.List;

/**
 * Created by Senh Linsh on 17/4/25.
 */

interface MainContract {

    interface View extends BaseContract.BaseView {

        void setData(List<Item> items);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        String checkItem(String text);

        void addItem(String itemId);

        void updateAll();
    }
}
