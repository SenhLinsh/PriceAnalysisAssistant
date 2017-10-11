package com.linsh.paa.mvp.main;

import android.util.Log;

import com.linsh.lshapp.common.base.RealmPresenterImpl;
import com.linsh.lshutils.utils.LshListUtils;
import com.linsh.paa.model.action.DefaultThrowableConsumer;
import com.linsh.paa.model.action.ResultConsumer;
import com.linsh.paa.model.bean.db.Item;
import com.linsh.paa.model.bean.db.ItemHistory;
import com.linsh.paa.model.bean.db.Tag;
import com.linsh.paa.model.bean.json.TaobaoDetail;
import com.linsh.paa.model.result.Result;
import com.linsh.paa.task.db.PaaDbHelper;
import com.linsh.paa.task.network.ApiCreator;
import com.linsh.paa.task.network.Url;
import com.linsh.paa.tools.BeanHelper;
import com.linsh.paa.tools.TaobaoDataParser;

import java.util.Locale;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
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
    private RealmResults<Tag> mTags;

    @Override
    protected void attachView() {
        mItems = PaaDbHelper.getItems(getRealm());
        mItems.addChangeListener(element -> {
            if (mItems.isValid()) {
                getView().setData(mItems);
            }
        });
        mTags = PaaDbHelper.getTags(getRealm());
        mTags.addChangeListener(element -> {
            if (mTags.isValid()) {
                getView().setTags(LshListUtils.getStringList(mTags, Tag::getName));
            }
        });
    }

    @Override
    public String checkItem(String text) {
        return BeanHelper.checkItem(text);
    }

    @Override
    public void addItem(String itemId) {
        Disposable disposable = ApiCreator.getTaobaoApi()
                .getDetail(Url.getTaobaoDetailUrl(itemId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    Log.i("LshLog", "getItem: data = " + data);
                    TaobaoDetail detail = TaobaoDataParser.parseGetDetailData(data);
                    Object[] toSave = BeanHelper.getItemAndHistoryToSave(null, detail);
                    if (toSave[0] != null) {
                        addItem((Item) toSave[0], (ItemHistory) toSave[1]);
                    }
                }, new DefaultThrowableConsumer());
        addDisposable(disposable);
    }

    @Override
    public void updateAll() {
        final Item[] curItem = {null};
        final int[] index = {0};
        int size = mItems.size();
        Disposable disposable = Flowable.fromIterable(mItems)
                .doOnSubscribe(onSub -> {
                    getView().showLoadingDialog(String.format(Locale.CHINA, "正在更新: 0/%d", size));
                })
                .map(item -> {
                    curItem[0] = item;
                    return item.getId();
                })
                .observeOn(Schedulers.io())
                .flatMap(itemId -> ApiCreator.getTaobaoApi()
                        .getDetail(Url.getTaobaoDetailUrl(itemId)))
                .map(TaobaoDataParser::parseGetDetailData)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(detail -> {
                    Object[] toSave = BeanHelper.getItemAndHistoryToSave(curItem[0], detail);
                    if (toSave[0] != null) {
                        return PaaDbHelper.updateItem(getRealm(), (Item) toSave[0], (ItemHistory) toSave[1]);
                    }
                    return Flowable.just(new Result("数据解析失败"));
                })
                .doOnTerminate(() -> getView().dismissLoadingDialog())
                .doOnError(DefaultThrowableConsumer::showThrowableMsg)
                .map(result -> {
                    getView().setLoadingDialogText(String.format(Locale.CHINA, "正在更新: %d/%d", ++index[0], size));
                    return result;
                })
                .collect(() -> new Result(mItems.size() > 0 ? "短时间内宝贝不会有更新的哦" : "请先添加宝贝吧"),
                        (success, result) -> success.setSuccess(result.isSuccess() || success.isSuccess()))
                .subscribe(result -> {
                    if (!ResultConsumer.handleFailedWithToast(result)) {
                        getView().showToast("更新完成");
                    }
                }, new DefaultThrowableConsumer());
        addDisposable(disposable);
    }

    @Override
    public void deleteItem(String id) {
        Disposable disposable = PaaDbHelper.deleteItem(getRealm(), id)
                .subscribe(result -> getView().showToast("删除成功"));
        addDisposable(disposable);
    }

    @Override
    public void addTag(String tag) {
        Disposable disposable = PaaDbHelper.createTag(getRealm(), new Tag(tag))
                .doOnComplete(() -> getView().showToast("添加成功"))
                .subscribe();
        addDisposable(disposable);
    }

    public void addItem(Item item, ItemHistory history) {
        Disposable disposable = PaaDbHelper.createItem(getRealm(), item, history)
                .subscribe(result -> {
                    if (!ResultConsumer.handleFailedWithToast(result)) {
                        getView().showToast("保存成功");
                    }
                });
        addDisposable(disposable);
    }
}
