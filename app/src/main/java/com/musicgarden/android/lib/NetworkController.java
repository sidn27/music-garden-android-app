package com.musicgarden.android.lib;

import android.content.Context;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Necra on 14-09-2017.
 */


public interface NetworkController {

    @GET
    Observable<Response<ResponseBody>> get(@Url String url,
                                           @HeaderMap Map<String, String> headerMap,
                                           @QueryMap Map<String, String> queryMap);

    @POST
    Observable<retrofit2.Response<ResponseBody>> post(@Url String url,
                                                      @HeaderMap Map<String, String> headerMap,
                                                      @QueryMap Map<String, String> queryMap,
                                                      @Body Object body);
}
