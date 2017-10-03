package com.linsh.paa.task.network.api;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/10/03
 *    desc   : 淘宝相关 API
 * </pre>
 */
public interface TaobaoApi {

    @GET()
    Flowable<String> getDetail(@Url String url);
}
