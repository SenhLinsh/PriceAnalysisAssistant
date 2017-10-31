package com.linsh.paa.mvp.main;

import com.linsh.lshapp.common.base.RealmPresenterImpl;
import com.linsh.lshutils.utils.Basic.LshApplicationUtils;
import com.linsh.lshutils.utils.Basic.LshLogUtils;
import com.linsh.lshutils.utils.LshListUtils;
import com.linsh.paa.model.action.DefaultThrowableConsumer;
import com.linsh.paa.model.action.ResultConsumer;
import com.linsh.paa.model.bean.db.Item;
import com.linsh.paa.model.bean.db.ItemHistory;
import com.linsh.paa.model.bean.db.Tag;
import com.linsh.paa.model.throwable.CustomThrowable;
import com.linsh.paa.task.db.PaaDbHelper;
import com.linsh.paa.task.network.NetworkHelper;
import com.linsh.paa.tools.BeanHelper;
import com.linsh.paa.tools.LshRxUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import hugo.weaving.DebugLog;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
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
    private RealmResults<Tag> mTags;
    private RealmChangeListener<RealmResults<Item>> mItemChangeListener = element -> {
        if (mItems.isValid()) {
            getView().setData(mItems);
            for (Item item : mItems) {
                LshLogUtils.i(item.getTitle(), item.getPrice(), item.getInitialPrice());
            }
        }
    };
    private String mCurTag;
    private String mCurDisplay;

    @DebugLog
    @Override
    protected void attachView() {
        mItems = PaaDbHelper.getItems(getRealm());
        mItems.addChangeListener(mItemChangeListener);
        mTags = PaaDbHelper.getTags(getRealm());
        mTags.addChangeListener(element -> {
            if (mTags.isValid()) {
                getView().setTags(LshListUtils.getStringList(mTags, Tag::getName));
            }
        });
    }

    /**
     * @param isConfirm 为 true 时表示 ItemId 为手动输入的; 为 false 时表示 ItemId 为剪贴板的
     */
    @DebugLog
    @Override
    public void getItem(String text, boolean isConfirm) {
        String idOrUrl = BeanHelper.getIdOrUrlFromText(text);
        if (idOrUrl == null) {
            if (isConfirm) {
                getView().showToast("无法解析该宝贝, 请传入正确格式");
            } else {
                getView().showInputItemUrlDialog();
            }
            return;
        }
        getView().showLoadingDialog();
        Disposable disposable = Flowable.just(idOrUrl)
                .subscribeOn(Schedulers.io())
                .flatMap(s -> {
                    if (idOrUrl.startsWith("http")) {
                        return NetworkHelper.get(idOrUrl) // Url
                                .map(BeanHelper::getIdFromTKL);
                    } else {
                        return Flowable.just(idOrUrl); // Id
                    }
                })
                .flatMap(NetworkHelper::getItemProvider)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(detail -> {
                    getView().dismissLoadingDialog();
                    Object[] toSave = BeanHelper.getItemAndHistoryToSave(detail);
                    if (toSave[0] != null && toSave[1] != null) {
                        getView().showItem(toSave, isConfirm);
                    } else if (isConfirm) {
                        getView().showToast("数据解析失败");
                    } else {
                        getView().showInputItemUrlDialog();
                    }
                }, thr -> {
                    getView().dismissLoadingDialog();
                    DefaultThrowableConsumer.showThrowableMsg(thr);
                });
        addDisposable(disposable);
    }

    @DebugLog
    @Override
    public void updateAll() {
        final int[] size = new int[1];
        final int[] curIndex = {0};
        getView().showLoadingDialog("正在更新");
        Disposable disposable = LshRxUtils.getAsyncRealmFlowable()
                .flatMap(realm -> Flowable.just(realm.where(Item.class).findAll())
                        .flatMap(items -> {
                            size[0] = items.size();
                            if (size[0] > 0) {
                                return Flowable.just(items);
                            }
                            return Flowable.error(new CustomThrowable("请先添加宝贝吧"));
                        })
                        .flatMap(Flowable::fromIterable)
                        .map(item -> {
                            LshApplicationUtils.postRunnable(() ->
                                    getView().setLoadingDialogText(String.format(Locale.CHINA, "正在更新: %d/%d", ++curIndex[0], size[0])));
                            return item;
                        })
                        // 获取商品详情数据
                        .flatMap(item -> Flowable.just(item.getId())
                                // 获取需要保存的 Item 和 ItemHistory
                                .flatMap(NetworkHelper::getItemProvider)
                                .flatMap(provider -> {
                                    Object[] toSave = BeanHelper.getItemAndHistoryToSave(item, realm.copyFromRealm(item), provider);
                                    if (toSave[1] != null) {
                                        return Flowable.just(toSave);
                                    }
                                    return Flowable.error(new CustomThrowable("数据解析失败"));
                                })
                                .filter(toSave -> toSave[0] != null)
                        )
                )
                .collect((Callable<List<Object[]>>) ArrayList::new, List::add)
                .map(PaaDbHelper::updateItems)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    getView().dismissLoadingDialog();
                    if (!ResultConsumer.handleFailedWithToast(result)) {
                        getView().showToast("更新完成");
                    }
                }, thr -> {
                    getView().dismissLoadingDialog();
                    DefaultThrowableConsumer.showThrowableMsg(thr);
                });
        addDisposable(disposable);
    }

    @DebugLog
    @Override
    public void deleteItem(String id) {
        Disposable disposable = PaaDbHelper.deleteItem(getRealm(), id)
                .subscribe(result -> getView().showToast("删除成功"));
        addDisposable(disposable);
    }

    @DebugLog
    @Override
    public void addTag(String tag, List<String> ids) {
        Disposable disposable = PaaDbHelper.createTag(getRealm(), new Tag(tag))
                .flatMap(result -> Flowable.fromIterable(ids))
                .flatMap(id -> PaaDbHelper.updateItem(getRealm(), id, item -> item.setTag(tag)))
                .subscribe();
        addDisposable(disposable);
    }

    @Override
    public List<String> getTags() {
        return LshListUtils.getStringList(mTags, Tag::getName);
    }

    @DebugLog
    @Override
    public void deleteItems(List<String> ids) {
        Disposable disposable = Flowable.fromIterable(ids)
                .flatMap(id -> PaaDbHelper.deleteItem(getRealm(), id))
                .subscribe();
        addDisposable(disposable);
    }

    @DebugLog
    @Override
    public void moveItemsToOtherTag(String tag, ArrayList<String> ids) {
        Disposable disposable = Flowable.fromIterable(ids)
                .flatMap(id -> PaaDbHelper.updateItem(getRealm(), id, item -> item.setTag(tag)))
                .subscribe();
        addDisposable(disposable);
    }

    @DebugLog
    @Override
    public void onTagSelected(String tag) {
        mCurTag = tag;
        mItems.removeAllChangeListeners();
        mItems = PaaDbHelper.getItems(getRealm(), mCurTag, mCurDisplay);
        mItems.addChangeListener(mItemChangeListener);
    }

    @DebugLog
    @Override
    public void onStatusSelected(String status) {
        if ("价格较低".equals(status)) {
            mCurDisplay = "价格低";
        } else if ("降价中".equals(status)) {
            mCurDisplay = "下降";
        } else {
            mCurDisplay = null;
        }
        mItems.removeAllChangeListeners();
        mItems = PaaDbHelper.getItems(getRealm(), mCurTag, mCurDisplay);
        mItems.addChangeListener(mItemChangeListener);
    }

    @Override
    public void setNotifiedPrice(String id, String price) {
        PaaDbHelper.updateItem(getRealm(), id,
                item -> {
                    item.setNotifiedPrice((int) (Float.parseFloat(price) * 100));
                    BeanHelper.updateItemDisplay(item);
                })
                .subscribe();

    }

    @Override
    public void setNormalPrice(String id, String price) {
        PaaDbHelper.updateItem(getRealm(), id,
                item -> {
                    item.setNormalPrice((int) (Float.parseFloat(price) * 100));
                    BeanHelper.updateItemDisplay(item);
                })
                .subscribe();
    }

    @DebugLog
    @Override
    public void saveItem(Object[] toSave) {
        Disposable disposable = PaaDbHelper.createItem(getRealm(), (Item) toSave[0], (ItemHistory) toSave[1])
                .subscribe(result -> {
                    if (!ResultConsumer.handleFailedWithToast(result)) {
                        getView().showToast("保存成功");
                    }
                });
        addDisposable(disposable);
    }
}
