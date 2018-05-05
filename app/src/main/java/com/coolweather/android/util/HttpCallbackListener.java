package com.coolweather.android.util;

/**
 * Created by qr on 2018/5/5.
 */

public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
