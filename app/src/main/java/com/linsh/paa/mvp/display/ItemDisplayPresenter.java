package com.linsh.paa.mvp.display;

import com.linsh.lshapp.common.base.RealmPresenterImpl;
import com.linsh.paa.model.action.DefaultThrowableConsumer;
import com.linsh.paa.model.action.HttpThrowableConsumer;
import com.linsh.paa.model.action.ResultConsumer;
import com.linsh.paa.model.bean.db.Item;
import com.linsh.paa.model.bean.db.ItemHistory;
import com.linsh.paa.task.db.PaaDbHelper;
import com.linsh.paa.task.network.NetworkHelper;
import com.linsh.paa.tools.BeanHelper;

import hugo.weaving.DebugLog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/04
 *    desc   :
 * </pre>
 */
class ItemDisplayPresenter extends RealmPresenterImpl<ItemDisplayContract.View>
        implements ItemDisplayContract.Presenter {

    @Override
    protected void attachView() {
    }

    @DebugLog
    @Override
    public void addCurItem(String url) {
        String id = BeanHelper.getIdOrUrlFromText(url);
        if (id != null) {
            addItem(id);
        } else {
            getView().showToast("该界面不是宝贝界面");
        }
    }

    private void addItem(String id) {
        NetworkHelper.getItemProvider(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(detail -> {
                    Object[] toSave = BeanHelper.getItemAndHistoryToSave(detail);
                    if (toSave[0] != null && toSave[1] != null) {
                        addItem((Item) toSave[0], (ItemHistory) toSave[1]);
                    }
                }, new DefaultThrowableConsumer());
    }

    private void addItem(Item item, ItemHistory history) {
        PaaDbHelper.createItem(getRealm(), item, history)
                .doOnError(new HttpThrowableConsumer())
                .subscribe(result -> {
                    if (!ResultConsumer.handleFailedWithToast(result)) {
                        getView().showToast("保存成功");
                    }
                });
    }
}
