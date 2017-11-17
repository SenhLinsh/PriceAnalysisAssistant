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

        void showInputItemUrlDialog();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getItem(String text, boolean isCheck);

        void updateAll();

        void deleteItem(String id);

        void addTag(String tag, List<String> ids);

        List<String> getTags();

        void removeOrDeleteItems(List<String> ids);

        void moveItemsToOtherTag(String tag, ArrayList<String> ids);

        void onTagSelected(String tag);

        void onStatusSelected(String status);

        void setNotifiedPrice(String id, String price);

        void setNormalPrice(String id, String price);

        void saveItem(Object[] toSave);

        void onPlatformSelected(String platform);

        void removeItem(String id);

        boolean isShowingRemoved();

        void cancelRemoveItem(String id);
    }
}
