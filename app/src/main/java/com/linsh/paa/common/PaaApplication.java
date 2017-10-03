package com.linsh.paa.common;

import com.linsh.lshapp.common.common.BaseApplication;
import com.linsh.lshapp.common.common.Config;
import com.linsh.lshapp.common.tools.RealmTool;
import com.linsh.paa.tools.PaaMigration;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/03
 *    desc   :
 * </pre>
 */
public class PaaApplication extends BaseApplication {


    @Override
    public void onCreate() {
        super.onCreate();
        RealmTool.init(this, "paa.realm", 0, new PaaMigration());
    }

    @Override
    protected Config getConfig() {
        return new PaaConfig();
    }
}
