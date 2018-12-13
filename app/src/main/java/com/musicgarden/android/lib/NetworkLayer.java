package com.musicgarden.android.lib;

import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicgarden.android.utils.Globals;

import java.io.IOException;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by Necra on 14-09-2017.
 */

public class NetworkLayer<T>  {

    private ViewController view;
    private NetworkService service;
    private Subscription subscription;

    public NetworkLayer(T view) {
        this.view = (ViewController) view;
        this.service = Globals.getNetworkService();
    }

    public void get(String apiEndPoint, Map<String, String> headers, Map<String, String> query) {
        StringBuilder id = new StringBuilder(apiEndPoint);
        id.append("?");
        for(String key:query.keySet()) {
            id.append(key);
            id.append("=");
            id.append(query.get(key));
            id.append("&");
        }
        final String ID = id.substring(0, id.length() - 1);
        Observable<Response<ResponseBody>> friendResponseObservable = (Observable<Response<ResponseBody>>)
                service.getPreparedObservable(service.getAPI().get(apiEndPoint,
                        headers, query), ID, true, true);


        friendResponseObservable.subscribe(new Subscriber<Response<ResponseBody>>() {
            @Override
            public void onCompleted() {
                this.unsubscribe();
            }

            @Override
            public void onError(Throwable e) {
                view.onError(e);
            }

            @Override
            public void onNext(Response<ResponseBody> response) {
                String result = null;
                try {
                    result = response.body().string();
                    service.getApiObservables().put(ID, result);

                } catch (Exception e) {
                    view.onError(e);
                }
                view.onResponse(result);
            }
        });
        /*subscription = friendResponseObservable.subscribe(new Observer<Response<ResponseBody>>() {
            @Override
            public void onCompleted() {
                subscription.unsubscribe();
                subscription = null;
            }

            @Override
            public void onError(Throwable e) {
                view.onError(e);
            }

            @Override
            public void onNext(Response<ResponseBody> response) {
                String result = null;
                try {
                    result = response.body().string();
                    service.getApiObservables().put(ID, result);

                } catch (Exception e) {
                    view.onError(e);
                }
                view.onResponse(result);
            }
        });*/
    }



    public void get(String apiEndPoint, Map<String, String> headers, Map<String, String> query, final Callback callback) {
        StringBuilder id = new StringBuilder(apiEndPoint);
        id.append("?");
        for(String key:query.keySet()) {
            id.append(key);
            id.append("=");
            id.append(query.get(key));
            id.append("&");
        }
        final String ID = id.substring(0, id.length() - 1);
        Observable<Response<ResponseBody>> friendResponseObservable = (Observable<Response<ResponseBody>>)
                service.getPreparedObservable(service.getAPI().get(apiEndPoint,
                        headers, query), ID, true, true);


        friendResponseObservable.subscribe(new Subscriber<Response<ResponseBody>>() {
            @Override
            public void onCompleted() {
                this.unsubscribe();
            }

            @Override
            public void onError(Throwable e) {
                view.onError(e);
            }

            @Override
            public void onNext(Response<ResponseBody> response) {
                String result = null;
                try {
                    result = response.body().string();
                    service.getApiObservables().put(ID, result);

                } catch (Exception e) {
                    view.onError(e);
                }
                ObjectMapper om = new ObjectMapper();
                try {

                    Object obj = om.readValue(result, callback.getTypeParameterClass());

                    callback.call(obj);
                }
                catch(Exception e) {
                    System.out.println("IOEXCEPTION!!!!!!!!!!");
                }
                view.onResponse(result);
            }
        });
        /*subscription = friendResponseObservable.subscribe(new Observer<Response<ResponseBody>>() {
            @Override
            public void onCompleted() {
                subscription.unsubscribe();
                subscription = null;
            }

            @Override
            public void onError(Throwable e) {
                view.onError(e);
            }

            @Override
            public void onNext(Response<ResponseBody> response) {
                String result = null;
                try {
                    result = response.body().string();
                    service.getApiObservables().put(ID, result);

                } catch (Exception e) {
                    view.onError(e);
                }
                view.onResponse(result);
            }
        });*/
    }





    public void post(String apiEndPoint, Map<String, String> headers, Map<String, String> query, Object object) {
        Observable<Response<ResponseBody>> friendResponseObservable = (Observable<Response<ResponseBody>>)
                service.getPreparedObservable(service.getAPI().post(apiEndPoint, headers, query, object), "",
                        true, true);
        subscription = friendResponseObservable.subscribe(new Observer<Response<ResponseBody>>() {
            @Override
            public void onCompleted() {
                subscription.unsubscribe();
                subscription = null;
            }

            @Override
            public void onError(Throwable e) {
                    view.onError(e);
            }

            @Override
            public void onNext(Response<ResponseBody> response) {
                String result = null;
                try {
                    result = response.body().string();
                } catch (Exception e) {
                    view.onError(e);
                }
                view.onResponse(result);
            }
        });
    }

    public void unSubscribe() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}