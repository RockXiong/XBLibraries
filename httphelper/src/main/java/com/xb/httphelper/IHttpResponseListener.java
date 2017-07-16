package com.xb.httphelper;

/**
 * 处理请求的响应结果
 * 所有的接口方法都会在主线程中执行
 * 参见{@link HttpHandler}
 */
public interface IHttpResponseListener {


    /**
     * 响应成功
     * @param response
     * @param requestTag
     */
    void onResponseSuccess(IHttpResponse response, String requestTag);

    /**
     * 响应失败
     * @param requestTag
     * @param errorCode
     */
    void onResponseFailure(String requestTag, int errorCode);

    /**
     * 处理进度
     *
     * @param progress 值区间:[0,100],0开始处理,100处理结束,无论响应成功与否最终都会是100
     */
    void onProgress(int progress);

}
