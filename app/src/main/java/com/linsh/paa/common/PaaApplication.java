package com.linsh.paa.common;

import com.linsh.lshapp.common.common.BaseApplication;
import com.linsh.lshapp.common.common.Config;
import com.linsh.lshapp.common.tools.RealmTool;
import com.linsh.lshutils.utils.Basic.LshApplicationUtils;
import com.linsh.lshutils.utils.Basic.LshToastUtils;
import com.linsh.paa.tools.PaaMigration;

import hugo.weaving.DebugLog;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/03
 *    desc   :
 * </pre>
 */
public class PaaApplication extends BaseApplication {

    @DebugLog
    @Override
    public void onCreate() {
        super.onCreate();
        RealmTool.init(this, "paa.realm", PaaMigration.VERSION, new PaaMigration());
        RxJavaPlugins.setErrorHandler(thr -> {
            thr.printStackTrace();
            LshApplicationUtils.postRunnable(() -> LshToastUtils.show(thr.getMessage()));
        });
    }

    @Override
    protected Config getConfig() {
        return new PaaConfig();
    }
}
