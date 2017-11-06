package com.linsh.paa.mvp.analysis;

import com.github.mikephil.charting.data.Entry;
import com.linsh.lshapp.common.base.RealmPresenterImpl;
import com.linsh.paa.model.action.DefaultThrowableConsumer;
import com.linsh.paa.model.bean.db.Item;
import com.linsh.paa.model.bean.db.ItemHistory;
import com.linsh.paa.model.throwable.CustomThrowable;
import com.linsh.paa.task.db.PaaDbHelper;
import com.linsh.paa.task.network.NetworkHelper;
import com.linsh.paa.tools.BeanHelper;

import java.util.ArrayList;

import hugo.weaving.DebugLog;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmResults;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/05
 *    desc   :
 * </pre>
 */
class AnalysisPresenter extends RealmPresenterImpl<AnalysisContract.View>
        implements AnalysisContract.Presenter {

    private RealmResults<ItemHistory> mHistories;
    private Item mItem;

    @DebugLog
    @Override
    protected void attachView() {
        mItem = PaaDbHelper.getItem(getRealm(), getView().getId());
        mItem.addChangeListener(element -> {
            if (mItem.isValid()) {
                getView().setData(mItem);
            }
        });

        mHistories = PaaDbHelper.getItemHistories(getRealm(), getView().getId());
        mHistories.addChangeListener(element -> {
            if (element.isValid()) {
                ArrayList<Entry> values1 = new ArrayList<>();
                ArrayList<Entry> values2 = new ArrayList<>();
                boolean hasHighPrices = false;
                for (ItemHistory history : mHistories) {
                    String price = history.getPrice();
                    if (price.contains("-")) {
                        hasHighPrices = true;
                        String[] split = price.split("-");
                        values1.add(new Entry(history.getTimestamp(), Float.parseFloat(split[0]), history.getId()));
                        values2.add(new Entry(history.getTimestamp(), Float.parseFloat(split[1]), history.getId()));
                    } else {
                        values1.add(new Entry(history.getTimestamp(), Float.parseFloat(price), history.getId()));
                        values2.add(new Entry(history.getTimestamp(), Float.parseFloat(price), history.getId()));
                    }
                }
                getView().setData(values1, hasHighPrices ? values2 : null);
            }
        });
    }

    @Override
    public void updateItem() {
        Item item = getRealm().copyFromRealm(mItem);
        getView().showLoadingDialog();
        Flowable.just(item.getId())
                .subscribeOn(Schedulers.io())
                // 获取需要保存的 Item 和 ItemHistory
                .flatMap(NetworkHelper::getItemProvider)
                .flatMap(provider -> {
                    Object[] toSave = BeanHelper.getItemAndHistoryToSave(item, item, provider);
                    if (toSave == null) {
                        return Flowable.error(new CustomThrowable("宝贝信息获取失败"));
                    }
                    ArrayList<Object[]> list = new ArrayList<>();
                    list.add(toSave);
                    return Flowable.just(list);
                })
                .map(PaaDbHelper::updateItems)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    getView().dismissLoadingDialog();
                    if (result.isSuccess()) {
                        getView().showToast("更新成功");
                    } else {
                        getView().showToast(result.getMessage());
                    }
                }, thr -> {
                    getView().dismissLoadingDialog();
                    DefaultThrowableConsumer.showThrowableMsg(thr);
                });
    }
}
