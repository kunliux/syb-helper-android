package com.shouyubang.android.inter.utils;

/**
 * 静态函数
 */
public class Constants {

    private static final String DOMAIN = "https://www.yunzhewentong.com:8443/guanli";

    public static final String BASE = DOMAIN;
    public static final String STAFF_URL = BASE+"/api/staff";
    public static final String AUTH_URL = BASE+"/api/auth";

    public static final int IM_APP_ID = 10000;
    public static final int ACCOUNT_TYPE = 10000;

    public static final String COS_APP_ID = "helloworld";
    public static final String VIDEO_BUCKET = "video";
    public static final String COVER_BUCKET = "cover";
    public static final String AVATAR_BUCKET = "avatar";

    public static final String USER_INFO = "user_info";

    public static final String USER_ID = "user_id";

    public static final String USER_SIG = "user_sig";

    public static final String USER_TOKEN = "user_token";

    public static final String USER_NICK = "user_nick";

    public static final String USER_SIGN = "user_sign";

    public static final String USER_AVATAR = "user_avatar";

    public static final int CALL_OFFLINE = 0;
    public static final int CALL_ONLINE = 1;
    public static final int CALL_AWAY = 2;
    public static final int CALL_BUSY = 3;

    public static final int WRITE_PERMISSION_REQ_CODE = 101;

}
