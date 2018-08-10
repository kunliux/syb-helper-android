package com.shouyubang.android.inter.utils;

public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);

}