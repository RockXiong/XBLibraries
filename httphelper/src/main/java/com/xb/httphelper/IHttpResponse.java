package com.xb.httphelper;

/**
 * Author: xiongda
 * Created on 2017/7/16.
 * Introduction:
 * 请求相应接口，所有的网络请求响应类都实现改接口
 */

public interface IHttpResponse {
    int SUCCESS_CODE = 1000;//成功
    int FAULT_CODE = 9999;//系统异常或故障
    int NETWORK_TIMER_OUT_CODE = 1005;//网络连接超时

    /**
     * 默认实现IHttpResponse的响应类,用于返回原始的response
     */
    final class BaseResponse implements IHttpResponse {

        BaseResponse(byte[] responseBytes) {
            mResponseBytes = responseBytes;
        }

        private byte[] mResponseBytes;

        /**
         * 获取响应字节数组
         *
         * @return
         */
        public byte[] getResponseBytes() {
            return mResponseBytes;
        }

        /**
         * 设置响应内容
         *
         * @param responseBytes
         */
        public void setResponseBytes(byte[] responseBytes) {
            mResponseBytes = responseBytes;
        }
    }
}
