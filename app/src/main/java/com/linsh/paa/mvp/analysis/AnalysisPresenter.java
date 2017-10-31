package com.linsh.paa.mvp.analysis;

import com.github.mikephil.charting.data.Entry;
import com.linsh.lshapp.common.base.RealmPresenterImpl;
import com.linsh.paa.model.bean.db.Item;
import com.linsh.paa.model.bean.db.ItemHistory;
import com.linsh.paa.task.db.PaaDbHelper;

import java.util.ArrayList;

import hugo.weaving.DebugLog;
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
                        values1.add(new Entry(history.getTimestamp(), Float.parseFloat(split[0])));
                        values2.add(new Entry(history.getTimestamp(), Float.parseFloat(split[1])));
                    } else {
                        values1.add(new Entry(history.getTimestamp(), Float.parseFloat(price)));
                        values2.add(new Entry(history.getTimestamp(), Float.parseFloat(price)));
                    }
                }
                getView().setData(values1, hasHighPrices ? values2 : null);
            }
        });
    }
}
