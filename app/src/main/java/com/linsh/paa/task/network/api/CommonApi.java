package com.linsh.paa.task.network.api;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Senh Linsh on 17/6/12.
 */

public interface CommonApi {

    @GET()
    Flowable<String> get(@Url String url);
}
