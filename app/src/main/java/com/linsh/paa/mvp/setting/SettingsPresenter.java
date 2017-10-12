package com.linsh.paa.mvp.setting;


import com.linsh.lshapp.common.base.RealmPresenterImpl;
import com.linsh.lshutils.utils.Basic.LshFileUtils;
import com.linsh.paa.model.action.DefaultThrowableConsumer;
import com.linsh.paa.model.bean.db.Item;
import com.linsh.paa.model.bean.db.ItemHistory;
import com.linsh.paa.model.result.Result;
import com.linsh.paa.task.db.PaaDbHelper;
import com.linsh.paa.task.network.ApiCreator;
import com.linsh.paa.task.network.Url;
import com.linsh.paa.tools.BeanHelper;
import com.linsh.paa.tools.PaaFileFactory;
import com.linsh.paa.tools.PaaSpTools;
import com.linsh.paa.tools.TaobaoDataParser;

import java.io.File;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Senh Linsh on 17/5/2.
 */

public class SettingsPresenter extends RealmPresenterImpl<SettingsContract.View> implements SettingsContract.Presenter {

    @Override
    protected void attachView() {

    }

    @Override
    public void checkUpdate() {
        // TODO: 17/10/10
        getView().showToast("该功能尚未开发");
    }

    @Override
    public void importItems() {
        File file = new File(PaaFileFactory.getAppDir(), "import/items.txt");
        if (!file.exists()) {
            getView().showToast("检查不到可读文件");
            return;
        }
        if (PaaSpTools.getLastImportItemsTime() > file.lastModified()) {
            getView().showToast("这些宝贝都已经添加过了哦");
            return;
        }
        StringBuilder builder = LshFileUtils.readFile(file);
        if (builder != null) {
            String[] split = builder.toString().split("\\r|\\n");
            final int[] index = {0};
            getView().showLoadingDialog("正在添加: ...");
            Disposable disposable = Flowable.fromArray(split)
                    .map(line -> {
                        String item = BeanHelper.checkItem(line);
                        if (item == null) item = "";
                        return item;
                    })
                    .filter(item -> item.length() > 0)
                    .flatMap(item -> PaaDbHelper.hasItem(item)
                            .filter(has -> !has)
                            .map(has -> item))
                    .toList()
                    .flatMap(items -> Flowable.fromIterable(items)
                            .observeOn(Schedulers.io())
                            .flatMap(item -> ApiCreator.getTaobaoApi().getDetail(Url.getTaobaoDetailUrl(item)))
                            .map(TaobaoDataParser::parseGetDetailData)
                            .map(BeanHelper::getItemAndHistoryToSave)
                            .filter(toSave -> toSave[0] != null && toSave[1] != null)
                            .observeOn(AndroidSchedulers.mainThread())
                            .flatMap(toSave -> PaaDbHelper.createItem(getRealm(), (Item) toSave[0], (ItemHistory) toSave[1]))
                            .filter(Result::isSuccess)
                            .map(result -> {
                                index[0]++;
                                getView().setLoadingDialogText("正在添加: " + index[0] + "/" + items.size());
                                return result;
                            })
                            .count())
                    .subscribe(count -> {
                        getView().dismissLoadingDialog();
                        getView().showToast("成功添加了 " + count + " 件宝贝~");
                        PaaSpTools.refreshLastImportItemsTime();
                    }, new DefaultThrowableConsumer());
            addDisposable(disposable);
        } else {
            getView().showToast("读取失败");
        }
    }

    @Override
    public void exportRealm() {
        Flowable.fromCallable(new Callable<Result>() {
            @Override
            public Result call() throws Exception {
                File realmFile = new File(getRealm().getPath());
                File destFile = PaaFileFactory.getRealmFile();
                LshFileUtils.copy(realmFile, destFile);
                return new Result();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> getView().showToast("导出成功"))
                .subscribe();
    }

}
