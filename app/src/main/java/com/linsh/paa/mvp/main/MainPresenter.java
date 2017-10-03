package com.linsh.paa.mvp.main;

import android.widget.Toast;

import com.linsh.lshapp.common.base.RealmPresenterImpl;
import com.linsh.paa.model.bean.db.Item;
import com.linsh.paa.model.bean.db.ItemHistory;
import com.linsh.paa.task.db.PaaDbHelper;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/03
 *    desc   :
 * </pre>
 */
class MainPresenter extends RealmPresenterImpl<MainContract.View>
        implements MainContract.Presenter {

    private RealmResults<Item> mItems;

    @Override
    protected void attachView() {
        mItems = PaaDbHelper.getItems(getRealm());
        mItems.addChangeListener(new RealmChangeListener<RealmResults<Item>>() {
            @Override
            public void onChange(RealmResults<Item> element) {
                if (mItems.isValid()) {
                    getView().setData(mItems);
                }
            }
        });
    }

    @Override
    public void saveItem(Item item, ItemHistory history) {
        PaaDbHelper.saveItemAndHistory(getRealm(), item, history)
                .subscribe(success -> {
                    Toast.makeText(getView().getContext(), "保存成功", Toast.LENGTH_SHORT).show();
                });
    }
}
