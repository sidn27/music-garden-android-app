package com.musicgarden.android.lib;

import android.util.LruCache;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NetworkService {

    private NetworkController networkAPI;

    private LruCache<String, String> apiObservables;

    public LruCache<String, String> getApiObservables() {
        return apiObservables;
    }


    public NetworkService(String baseUrl){
        apiObservables = new LruCache<>(100);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        networkAPI = retrofit.create(NetworkController.class);

    }

    public NetworkController getAPI(){
        return  networkAPI;
    }


    public Observable<?> getPreparedObservable(Observable<?> unPreparedObservable, String id, boolean cacheObservable, boolean useCache){

        Observable<?> preparedObservable = null;

        if(useCache && apiObservables.get(id) != null)//this way we don't reset anything in the cache if this is the only instance of us not wanting to use it.
        {
            Response<ResponseBody> resp = Response.success(ResponseBody.create(MediaType.parse("application/json"), apiObservables.get(id)));
            preparedObservable = Observable.just(resp);
        }

        if(preparedObservable!=null) {
            preparedObservable.observeOn(AndroidSchedulers.mainThread());
            return preparedObservable;
        }



        //we are here because we have never created this observable before or we didn't want to use the cache...

        preparedObservable = unPreparedObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        /*if(cacheObservable){
            preparedObservable = preparedObservable.cache();
            apiObservables.put(id, preparedObservable);
        }*/


        return preparedObservable;
    }


}
