package com.linsh.paa.mvp.main;


import com.linsh.lshapp.common.base.BaseContract;
import com.linsh.paa.model.bean.db.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Senh Linsh on 17/4/25.
 */

interface MainContract {

    interface View extends BaseContract.BaseView {

        void setData(List<Item> items);

        void setTags(List<String> tags);

        void showItem(Object[] toSave, boolean isConfirm);

        void showInputItemIdDialog();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getItem(String text, boolean isCheck);

        void updateAll();

        void deleteItem(String id);

        void addTag(String tag, List<String> itemIds);

        List<String> getTags();

        void deleteItems(List<String> itemIds);

        void moveItemsToOtherTag(String tag, ArrayList<String> itemIds);

        void onTagSelected(String tag);

        void onStatusSelected(String status);

        void setNotifiedPrice(String itemId, String price);

        void setNormalPrice(String itemId, String price);

        void saveItem(Object[] toSave);
    }
}
