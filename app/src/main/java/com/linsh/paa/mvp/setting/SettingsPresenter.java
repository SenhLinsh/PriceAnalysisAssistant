package com.linsh.paa.mvp.setting;


import com.linsh.lshapp.common.base.RealmPresenterImpl;
import com.linsh.lshutils.utils.Basic.LshFileUtils;
import com.linsh.lshutils.utils.Basic.LshStringUtils;
import com.linsh.lshutils.utils.LshAppUtils;
import com.linsh.paa.model.action.DefaultThrowableConsumer;
import com.linsh.paa.model.bean.db.Item;
import com.linsh.paa.model.bean.db.ItemHistory;
import com.linsh.paa.model.result.Result;
import com.linsh.paa.task.db.PaaDbHelper;
import com.linsh.paa.task.network.NetworkHelper;
import com.linsh.paa.tools.BeanHelper;
import com.linsh.paa.tools.PaaFileFactory;
import com.linsh.paa.tools.PaaSpTools;

import java.io.File;
import java.util.concurrent.Callable;

import hugo.weaving.DebugLog;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

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

    @DebugLog
    @Override
    public void importItems() {
        File file = new File(PaaFileFactory.getAppDir(), "import/items.txt");
        if (!file.exists()) {
            LshFileUtils.makeParentDirs(file);
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
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .map(line -> LshStringUtils.nullStrToEmpty(BeanHelper.getIdOrUrlFromText(line)))
                    .filter(id -> id.length() > 0)
                    .flatMap(id -> PaaDbHelper.hasItem(id)
                            .filter(has -> !has)
                            .map(has -> id))
                    .toList()
                    .flatMap(ids -> Flowable.fromIterable(ids)
                            .observeOn(Schedulers.io())
                            .flatMap(NetworkHelper::getItemProvider)
                            .map(BeanHelper::getItemAndHistoryToSave)
                            .filter(toSave -> toSave[0] != null && toSave[1] != null)
                            .observeOn(AndroidSchedulers.mainThread())
                            .flatMap(toSave -> PaaDbHelper.createItem(getRealm(), (Item) toSave[0], (ItemHistory) toSave[1]))
                            .filter(Result::isSuccess)
                            .map(result -> {
                                index[0]++;
                                getView().setLoadingDialogText("正在添加: " + index[0] + "/" + ids.size());
                                return result;
                            })
                            .count())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doAfterTerminate(() -> getView().dismissLoadingDialog())
                    .subscribe(count -> {
                        getView().showToast("成功添加了 " + count + " 件宝贝~");
                        PaaSpTools.refreshLastImportItemsTime();
                    }, new DefaultThrowableConsumer());
            addDisposable(disposable);
        } else {
            getView().showToast("读取失败");
        }
    }

    @DebugLog
    @Override
    public void importRealm() {
        File file = new File(PaaFileFactory.getAppDir(), "import/paa.realm");
        if (!file.exists()) {
            LshFileUtils.makeParentDirs(file);
            getView().showToast("检查不到文件");
            return;
        }
        Realm realm = getRealm();
        File realmFile = new File(getRealm().getPath());
        while (!realm.isClosed()) {
            realm.close();
        }
        if (realmFile.delete()) {
            if (LshFileUtils.copy(file, realmFile)) {
                getView().showTextDialog("导入成功, 需要重启应用才能生效", "重启", lshColorDialog -> {
                    lshColorDialog.dismiss();
                    LshAppUtils.killCurrentProcess();
                });
            } else {
                getView().showToast("Realm 文件导入失败");
            }
        } else {
            getView().showToast("Realm 文件删除失败");
        }
    }

    @DebugLog
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
