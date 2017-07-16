package com.xb.httphelpersample.impl;

import com.xb.httphelper.AHttpClient;
import com.xb.httphelper.AHttpRequest;
import com.xb.httphelper.HttpHandler;
import com.xb.httphelper.IHttpResponse;
import com.xb.httphelper.IHttpResponseListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Author: xiongda
 * Created on 2017/7/16.
 * Introduction:
 */

public class HttpClientWithOkHttp extends AHttpClient {

    private final OkHttpClient mClient = new OkHttpClient.Builder().cookieJar(new MemoryCookieStore())
                                                                   .build();

    public void getSync(AHttpRequest aHttpRequest, IHttpResponseListener responseListener) {
        HttpHandler httpHandler = new HttpHandler(responseListener, aHttpRequest.getClass()
                                                                                .getSimpleName(), aHttpRequest.getResponseClass());
        httpHandler.onProgress(30);
        String url = getAbsoluteUrlWithQueryString(true, aHttpRequest);
        Request request = new Request.Builder().url(url)
                                               .build();
        try {
            Response response = mClient.newCall(request)
                                       .execute();

            if (!response.isSuccessful()) {
                httpHandler.onFailure(response.code(), null, response.body()
                                                                     .string());
            } else {
                httpHandler.onSuccess(null, response.body()
                                                    .bytes());
            }
        } catch (IOException e) {
            httpHandler.onFailure(IHttpResponse.FAULT_CODE, null, e.getMessage());
        } finally {
            httpHandler.onProgress(100);
        }
    }

    @Override
    public void getASync(AHttpRequest aHttpRequest, IHttpResponseListener responseListener) {
        final HttpHandler httpHandler = new HttpHandler(responseListener, aHttpRequest.getClass()
                                                                                      .getSimpleName(), aHttpRequest.getResponseClass());
        httpHandler.onProgress(30);
        String url = getAbsoluteUrlWithQueryString(true, aHttpRequest);
        String tag = aHttpRequest.getClass()
                                 .getSimpleName();
        Request request = new Request.Builder().url(url)
                                               .tag(tag)
                                               .build();
        cancelTag(tag);//取消队列中相同的请求,确保同一个请求只执行一次
        mClient.newCall(request)
               .enqueue(new Callback() {
                   @Override
                   public void onFailure(Call call, IOException e) {
                       httpHandler.onFailure(IHttpResponse.FAULT_CODE, null, e.getMessage());
                   }

                   @Override
                   public void onResponse(Call call, Response response) throws IOException {
                       httpHandler.onSuccess(null, response.body()
                                                           .bytes());
                   }
               });
        httpHandler.onProgress(100);
    }

    @Override
    public void postSync(AHttpRequest aHttpRequest, IHttpResponseListener responseListener, boolean isJson) {
        HttpHandler httpHandler = new HttpHandler(responseListener, aHttpRequest.getClass()
                                                                                .getSimpleName(), aHttpRequest.getResponseClass());
        httpHandler.onProgress(30);
        try {
            Response response = mClient.newCall(getRequest(aHttpRequest, isJson))
                                       .execute();
            if (!response.isSuccessful()) {
                httpHandler.onFailure(IHttpResponse.FAULT_CODE, null, response.body()
                                                                              .string());
            } else {
                httpHandler.onSuccess(null, response.body()
                                                    .bytes());
            }
        } catch (IOException e) {
            httpHandler.onFailure(IHttpResponse.FAULT_CODE, null, e.getMessage());
        } finally {
            httpHandler.onProgress(100);
        }
    }

    @Override
    public void postASync(AHttpRequest aHttpRequest, IHttpResponseListener responseListener, boolean isJson) {
        final HttpHandler httpHandler = new HttpHandler(responseListener, aHttpRequest.getClass()
                                                                                      .getSimpleName(), aHttpRequest.getResponseClass());
        httpHandler.onProgress(30);

        mClient.newCall(getRequest(aHttpRequest, isJson))
               .enqueue(new Callback() {
                   @Override
                   public void onFailure(Call call, IOException e) {
                       httpHandler.onFailure(IHttpResponse.FAULT_CODE, null, e.getMessage());
                   }

                   @Override
                   public void onResponse(Call call, Response response) throws IOException {
                       httpHandler.onSuccess(null, response.body()
                                                           .bytes());
                   }
               });
        httpHandler.onProgress(100);
    }

    @Override
    public void uploadFile(AHttpRequest aHttpRequest, IHttpResponseListener responseListener) {

    }

    public void cancelTag(Object tag) {
        for (Call call : mClient.dispatcher()
                                .queuedCalls()) {
            if (tag.equals(call.request()
                               .tag())) {
                call.cancel();
            }
        }
        for (Call call : mClient.dispatcher()
                                .runningCalls()) {
            if (tag.equals(call.request()
                               .tag())) {
                call.cancel();
            }
        }
    }

    private Request getRequest(AHttpRequest aHttpRequest, boolean isJson) {
        //        String url = getAbsoluteUrl(aHttpRequest);
        String url = aHttpRequest.getUrl();
        String tag = aHttpRequest.getClass()
                                 .getSimpleName();
        //        Logger.t(tag + "--接口")
        //              .d(tag + "请求接口URL:" + super.getAbsoluteUrl(aHttpRequest));
        RequestBody formBody = getRequestFormBody(aHttpRequest);

        Request.Builder builder = new Request.Builder().url(url)
                                                       .tag(tag)
                                                       .post(isJson ? RequestBody.create(MediaType.parse("application/json; charset=utf-8"), aHttpRequest.getJsonEntity()) : formBody);
        if (isJson) {
            builder = builder.addHeader("Content-Type", AHttpRequest.CONTENT_TYPE_APPLICATION_JSON);
            //            Logger.t(aHttpRequest.getClass()
            //                                        .getSimpleName() + "--接口参数")
            //                  .d(aHttpRequest.getJsonEntity());
        }

        return builder.build();
    }

    private RequestBody getRequestFormBody(AHttpRequest aHttpRequest) {
        FormBody.Builder builder = new FormBody.Builder();
        if (!aHttpRequest.getParams()
                         .isEmpty()) {

            for (HashMap.Entry<String, Object> entry : aHttpRequest.getParams()
                                                                   .entrySet()) {
                builder.add(entry.getKey(), String.valueOf(entry.getValue()));
                //                Logger.t(aHttpRequest.getClass()
                //                                            .getSimpleName() + "--接口参数")
                //                      .d(entry.getKey() + "=" + entry.getValue());
            }
        }
        return builder.build();
    }

    /**
     * 实现Cookie缓存
     */
    private class MemoryCookieStore implements CookieJar {

        private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            cookieStore.put(url.host(), cookies);
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            List<Cookie> cookies = cookieStore.get(url.host());
            return null != cookies ? cookies : new ArrayList<Cookie>();
        }
    }

}
