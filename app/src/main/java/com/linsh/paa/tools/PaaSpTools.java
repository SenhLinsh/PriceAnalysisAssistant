package com.linsh.paa.tools;

import com.linsh.lshapp.common.tools.CommonSpTools;
import com.linsh.lshutils.utils.Basic.LshSharedPreferenceUtils;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/10
 *    desc   :
 * </pre>
 */
public class PaaSpTools extends CommonSpTools {

    private static final String LAST_IMPORT_ITEMS_TIME = "key_last_import_Items_time";

    public static void refreshLastImportItemsTime() {
        LshSharedPreferenceUtils.putLong(LAST_IMPORT_ITEMS_TIME, System.currentTimeMillis());
    }

    public static long getLastImportItemsTime() {
        return LshSharedPreferenceUtils.getLong(LAST_IMPORT_ITEMS_TIME, 0);
    }

}
