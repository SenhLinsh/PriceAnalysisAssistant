package com.linsh.paa.mvp.main;

import com.linsh.lshapp.common.base.RealmPresenterImpl;
import com.linsh.lshutils.module.SimpleDate;
import com.linsh.lshutils.utils.Basic.LshApplicationUtils;
import com.linsh.lshutils.utils.Basic.LshLogUtils;
import com.linsh.lshutils.utils.LshListUtils;
import com.linsh.lshutils.utils.LshRandomUtils;
import com.linsh.paa.model.action.DefaultThrowableConsumer;
import com.linsh.paa.model.action.ResultConsumer;
import com.linsh.paa.model.bean.db.Item;
import com.linsh.paa.model.bean.db.ItemHistory;
import com.linsh.paa.model.bean.db.Platform;
import com.linsh.paa.model.bean.db.Tag;
import com.linsh.paa.model.result.Result;
import com.linsh.paa.model.throwable.CustomThrowable;
import com.linsh.paa.task.db.PaaDbHelper;
import com.linsh.paa.task.network.NetworkHelper;
import com.linsh.paa.tools.BeanHelper;
import com.linsh.paa.tools.LshRxUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
        }
    };
    private String mCurPlatformCode;
    private String mCurTag;
    private String mCurDisplay;
    private long mLastModify;
    private Disposable mUpdateAllDis;
    private boolean isShowingRemoved;

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
        final int[] failedNum = {0};
        final int[] doneNum = {0};
        getView().showLoadingDialog("正在更新", dialog -> {
            if (mUpdateAllDis != null && !mUpdateAllDis.isDisposed()) {
                mUpdateAllDis.dispose();
            }
        });
        mUpdateAllDis = LshRxUtils.getAsyncRealmFlowable()
                .flatMap(realm -> Flowable.just(realm.where(Item.class).findAll())
                        .flatMap(items -> {
                            size[0] = items.size();
                            LshLogUtils.i("查询所有宝贝: size = " + size[0]);
                            if (size[0] > 0) {
                                return Flowable.just(realm.copyFromRealm(items));
                            }
                            return Flowable.error(new CustomThrowable("请先添加宝贝吧"));
                        }))
                .flatMap(items -> Flowable.just(curIndex)
                        .map(indexArr -> items.get(indexArr[0]))
                        .flatMap(item -> {
                            curIndex[0]++;
                            if (item.shouldUpdateItem()) {
                                LshApplicationUtils.postRunnable(() ->
                                        getView().setLoadingDialogText(String.format(Locale.CHINA, "正在更新: %d/%d", curIndex[0], size[0])));
                                // 线程等待 1-2s 防止淘宝风控返回失败
                                return Flowable.timer(LshRandomUtils.getInt(300, 800), TimeUnit.MILLISECONDS)
                                        // 获取商品详情数据
                                        .flatMap(timer -> Flowable.just(item.getId())
                                                // 获取需要保存的 Item 和 ItemHistory
                                                .flatMap(NetworkHelper::getItemProvider)
                                                .flatMap(provider -> {
                                                    Object[] toSave = BeanHelper.getItemAndHistoryToSave(item, item, provider);
                                                    if (toSave == null) {
                                                        failedNum[0]++;
                                                        return Flowable.just(new Object[2]);
                                                    }
                                                    return Flowable.just(toSave);
                                                })
                                        )
                                        .map(toSave -> {
                                            if (toSave[0] != null || toSave[1] != null) {
                                                PaaDbHelper.updateItem((Item) toSave[0], (ItemHistory) toSave[1]);
                                                doneNum[0]++;
                                            }
                                            return new Result();
                                        });
                            } else {
                                return Flowable.just(new Result());
                            }
                        })
                        .repeatUntil(() -> curIndex[0] >= size[0])
                )

                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                }, thr -> {
                    mUpdateAllDis = null;
                    getView().dismissLoadingDialog();
                    DefaultThrowableConsumer.showThrowableMsg(thr);
                }, () -> {
                    mUpdateAllDis = null;
                    getView().dismissLoadingDialog();
                    String msg;
                    if (doneNum[0] > 0 && failedNum[0] > 0) {
                        msg = "更新了" + doneNum[0] + "件宝贝" + " (失败" + failedNum[0] + "件)";
                    } else if (doneNum[0] > 0 && failedNum[0] == 0) {
                        msg = "更新了" + doneNum[0] + "件宝贝";
                    } else if (doneNum[0] == 0 && failedNum[0] > 0) {
                        msg = failedNum[0] + "件宝贝更新失败了";
                    } else {
                        msg = "短时间内宝贝没有变化的哦";
                    }
                    getView().showToast(msg);
                });
        addDisposable(mUpdateAllDis);
    }

    @Override
    public void removeItem(String id) {
        Disposable disposable = PaaDbHelper.removeItem(getRealm(), id)
                .subscribe();
        addDisposable(disposable);
    }

    @Override
    public void cancelRemoveItem(String id) {
        Disposable disposable = PaaDbHelper.cancelRemoveItem(getRealm(), id)
                .subscribe();
        addDisposable(disposable);
    }

    @Override
    public boolean isShowingRemoved() {
        return isShowingRemoved;
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
    public void removeOrDeleteItems(List<String> ids) {
        Disposable disposable = Flowable.fromIterable(ids)
                .flatMap(id -> {
                    if (isShowingRemoved) {
                        return PaaDbHelper.deleteItem(getRealm(), id);
                    } else {
                        return PaaDbHelper.removeItem(getRealm(), id);
                    }
                })
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

    @Override
    public void onPlatformSelected(String platform) {
        if ("淘宝".equals(platform)) {
            mCurPlatformCode = Platform.Taobao.getCode();
        } else if ("京东".equals(platform)) {
            mCurPlatformCode = Platform.Jingdong.getCode();
        } else {
            mCurPlatformCode = null;
        }
        mItems.removeAllChangeListeners();
        mItems = queryItems();
        mItems.addChangeListener(mItemChangeListener);
    }

    @DebugLog
    @Override
    public void onTagSelected(String tag) {
        mCurTag = tag;
        mItems.removeAllChangeListeners();
        mItems = queryItems();
        mItems.addChangeListener(mItemChangeListener);
    }

    @DebugLog
    @Override
    public void onStatusSelected(String status) {
        if ("价格较低".equals(status)) {
            mCurDisplay = "价格低";
            mLastModify = 0;
            isShowingRemoved = false;
        } else if ("降价中".equals(status)) {
            mCurDisplay = "下降";
            mLastModify = 0;
            isShowingRemoved = false;
        } else if ("未更新".equals(status)) {
            mCurDisplay = null;
            long min1 = System.currentTimeMillis() - 12L * 60 * 60 * 1000;
            long min2 = new Date(new SimpleDate(new Date()).getDate().getTime()).getTime();
            mLastModify = Math.min(min1, min2);
            isShowingRemoved = false;
        } else if ("未关注".equals(status)) {
            mCurDisplay = null;
            mLastModify = 0;
            isShowingRemoved = true;
        } else {
            mCurDisplay = null;
            mLastModify = 0;
            isShowingRemoved = false;
        }
        mItems.removeAllChangeListeners();
        mItems = queryItems();
        mItems.addChangeListener(mItemChangeListener);
    }

    private RealmResults<Item> queryItems() {
        return PaaDbHelper.getItems(getRealm(), mCurPlatformCode, mCurTag, mCurDisplay, mLastModify, isShowingRemoved);
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
