package com.xb.httphelper;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.HashMap;


/**
 * Author: xiongda
 * Created on 2017/7/16.
 * Introduction:
 * 用于处理Http请求结果的类
 * 如果HttpHandler不能满足您的需求，可以自己继承它并重写onSuccess和onFailure来实现自己的处理
 */

public class HttpHandler {
    private final String TAG = getClass().getSimpleName();
    private IHttpResponseListener mIHttpResponseListener;
    private Class<?> mClazz;
    private String mRequestTag;
    private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
    private Gson mGson = new Gson();

    public HttpHandler(@NonNull IHttpResponseListener iHttpResponseListener, @NonNull String requestTag, @NonNull Class<?> clazz) {
        mIHttpResponseListener = iHttpResponseListener;
        mClazz = clazz;
        mRequestTag = requestTag;
    }

    public void onSuccess(HashMap<String, String> headers, final byte[] responseBody) {
        String result = new String(responseBody);
        final IHttpResponse iHttpResponse;
        if (null != mClazz) {
            try {
                iHttpResponse = (IHttpResponse) mGson.fromJson(result, mClazz);
            } catch (Exception e) {
                mIHttpResponseListener.onResponseFailure(mRequestTag, IHttpResponse.FAULT_CODE);
                return;
            }
        } else {
            iHttpResponse = new IHttpResponse.BaseResponse(responseBody);
        }
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                mIHttpResponseListener.onResponseSuccess(iHttpResponse, mRequestTag);
            }
        });
    }

    public void onFailure(final int errorCode, HashMap<String, String> headers, String errorMsg) {
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                mIHttpResponseListener.onResponseFailure(mRequestTag, errorCode);
            }
        });
    }

    public void onProgress(final int progress) {
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                mIHttpResponseListener.onProgress(progress);
            }
        });
    }

}
