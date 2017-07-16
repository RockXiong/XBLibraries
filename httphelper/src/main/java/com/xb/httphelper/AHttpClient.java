package com.xb.httphelper;

/**
 * Author: xiongda
 * Created on 2017/7/16.
 * Introduction:
 */

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 抽象的HttpClient，可根据实际的需求来实现
 */
public abstract class AHttpClient {
    private static AHttpClient mHttpClient;

    public static void init(@NonNull AHttpClient httpClient) {
        mHttpClient = httpClient;
    }

    public static AHttpClient getHttpClient() {
        if (mHttpClient == null) {
            throw new NullPointerException("mHttpClient can not be null! Please call initHttpClient(AbstractHttpClient) to initial the mHttpClient field firstly.");
        }
        return mHttpClient;
    }

    /**
     * 同步的get请求
     *
     * @param aHttpRequest
     * @param responseListener
     */
    public abstract void getSync(AHttpRequest aHttpRequest, IHttpResponseListener responseListener);

    /**
     * 异步get请求
     *
     * @param aHttpRequest
     * @param responseListener
     */
    public abstract void getASync(AHttpRequest aHttpRequest, IHttpResponseListener responseListener);

    /**
     * 同步post请求
     *
     * @param aHttpRequest
     * @param responseListener
     * @param isJson
     */
    public abstract void postSync(AHttpRequest aHttpRequest, IHttpResponseListener responseListener, boolean isJson);

    /**
     * 异步post请求
     *
     * @param aHttpRequest
     * @param responseListener
     * @param isJson
     */
    public abstract void postASync(AHttpRequest aHttpRequest, IHttpResponseListener responseListener, boolean isJson);

    /**
     * @param aHttpRequest
     * @param responseListener
     */
    public abstract void uploadFile(AHttpRequest aHttpRequest, IHttpResponseListener responseListener);

    /**
     * 获取带参数的URL
     *
     * @param shouldEncoderUrl 是否需要进行URL编码
     * @param aHttpRequest     请求参数的对象
     * @return
     */
    protected String getAbsoluteUrlWithQueryString(boolean shouldEncoderUrl, @NonNull AHttpRequest aHttpRequest) {
        HashMap<String, Object> params = aHttpRequest.getParams();
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(aHttpRequest.getUrl());
        if (!params.isEmpty()) {
            if (urlBuilder.indexOf("?") == -1) {
                urlBuilder.append("?");
            }
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                urlBuilder.append(entry.getKey())
                          .append("=")
                          .append(entry.getValue())
                          .append("&");
            }
            if (urlBuilder.lastIndexOf("&") == urlBuilder.length() - 1) {
                urlBuilder.deleteCharAt(urlBuilder.lastIndexOf("&"));
            }
        }
        return shouldEncoderUrl ? urlBuilder.toString()
                                            .replace(" ", "%20") : urlBuilder.toString();
    }
}
