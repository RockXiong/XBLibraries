package com.xb.httphelper;

import android.support.annotation.NonNull;

import java.util.HashMap;

/**
 * Author: xiongda
 * Created on 2017/7/16.
 * Introduction:
 * 抽象的请求类,封装了网络请求参数，所有的网络请求类都需要继承该类
 */

public abstract class AHttpRequest {

    /**
     * 请求参数集合
     */
    private final HashMap<String, Object> mParams = new HashMap<>();

    /**
     * json请求字串
     */
    private String mJsonEntity;

    protected String mContentType;

    public final static String CONTENT_TYPE_APPLICATION_JSON = "application/json;charset:utf-8";

    /*
    * 获取请求的url
    * */
    public abstract String getUrl();

    /**
     * 当需要将请求结果用Gson等工具解析成java 对象时可重写此方法，并返回实际的java bean对象
     * 程序会自动将请求结果根据返回的been进行解析
     * 当不需要返回java对象时可直接返回null
     *
     * @return
     */
    public Class<?> getResponseClass() {
        return null;
    }

    /**
     * 设置请求参数
     *
     * @param key   参数名称
     * @param value 参数值
     */
    protected void put(@NonNull String key, @NonNull Object value) {
        mParams.put(key, value);
    }

    /**
     * 设置请求参数
     *
     * @param params 请求参数的集合
     */
    protected void put(@NonNull HashMap<String, String> params) {
        mParams.putAll(params);
    }

    /**
     * 提交raw形式的json数据
     * content-type为 application/json 时使用
     *
     * @param jsonValue
     */
    protected void put(@NonNull String jsonValue) {
        mJsonEntity = jsonValue;
    }

    public HashMap<String, Object> getParams() {
        return mParams;
    }

    public String getJsonEntity() {
        return mJsonEntity;
    }
}
