package com.shouyubang.android.inter.model;

/**
 * Created by KunLiu on 2017/8/2.
 */

public class RequestBackInfo {

    private int errorCode;
    private String errorInfo;

    public RequestBackInfo(int code, String bad) {
        errorCode = code;
        errorInfo = bad;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

}
