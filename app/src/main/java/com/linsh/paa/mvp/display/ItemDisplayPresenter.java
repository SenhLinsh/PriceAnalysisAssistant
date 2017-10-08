package com.linsh.paa.mvp.display;

import android.util.Log;

import com.linsh.lshapp.common.base.RealmPresenterImpl;
import com.linsh.paa.model.action.HttpThrowableConsumer;
import com.linsh.paa.model.action.ResultConsumer;
import com.linsh.paa.model.bean.db.Item;
import com.linsh.paa.model.bean.db.ItemHistory;
import com.linsh.paa.model.bean.json.TaobaoDetail;
import com.linsh.paa.task.db.PaaDbHelper;
import com.linsh.paa.task.network.ApiCreator;
import com.linsh.paa.task.network.Url;
import com.linsh.paa.tools.BeanHelper;
import com.linsh.paa.tools.TaobaoDataParser;

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

    @Override
    public void addCurItem(String url) {
        String itemId = null;
        if (url.matches(".+a\\.m\\.taobao\\.com/i\\d+\\.htm.+")) {
            // 我的收藏 -> .
            itemId = url.replaceAll(".+/i(\\d+).*", "$1");
        } else if (url.matches(".+/(item|detail).htm\\?.*id=.+")) {
            // 宝贝详情
            itemId = url.replaceAll(".+[?&]id=(\\d+).*", "$1");
        }
        if (itemId != null) {
            getItem(itemId);
        } else {
            getView().showToast("该界面不是宝贝界面");
        }
    }

    private void getItem(String itemId) {
        ApiCreator.getTaobaoApi()
                .getDetail(Url.getTaobaoDetailUrl(itemId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new HttpThrowableConsumer())
                .subscribe(data -> {
                    Log.i("LshLog", "getItem: data = " + data);
                    TaobaoDetail detail = TaobaoDataParser.parseGetDetailData(data);
                    Object[] toSave = BeanHelper.getItemAndHistiryToSave(null, detail);
                    if (toSave != null) {
                        addItem((Item) toSave[0], (ItemHistory) toSave[1]);
                    }
                });
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
