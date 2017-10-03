package com.linsh.paa.task.network;

import com.linsh.lshapp.common.tools.CommonApiCreator;
import com.linsh.lshapp.common.tools.RetrofitHelper;
import com.linsh.paa.task.network.api.TaobaoApi;

/**
 * Created by Senh Linsh on 17/6/2.
 */

public class ApiCreator extends CommonApiCreator {

    public static TaobaoApi getTaobaoApi() {
        return RetrofitHelper.createStringApi(TaobaoApi.class, BASE_URL_GITHUB);
    }
}
