package com.shouyubang.android.inter.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.shouyubang.android.inter.utils.Constants;

/**
 * 自己的状态数据
 */
public class MySelfInfo {

    private static final String TAG = "MySelfInfo";

    private String id;
    private String userSig;     //用于登录
    private String nickName;    // 姓名
    private String avatar;      // 头像
    private String sign;      // 个人签名
    private String CosSig;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String token;

    private int id_status;

    private static MySelfInfo ourInstance = new MySelfInfo();

    public static MySelfInfo getInstance() {

        return ourInstance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserSig() {
        return userSig;
    }

    public void setUserSig(String userSig) {
        this.userSig = userSig;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCosSig() {
        return CosSig;
    }

    public void setCosSig(String cosSig) {
        CosSig = cosSig;
    }

    public void writeToCache(Context context) {
        if (null != context) {
            Log.i(TAG, "writeToCache");
            SharedPreferences settings = context.getSharedPreferences(Constants.USER_INFO, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(Constants.USER_ID, id);
            editor.putString(Constants.USER_SIG, userSig);
            editor.putString(Constants.USER_TOKEN, token);
            editor.putString(Constants.USER_NICK, nickName);
            editor.putString(Constants.USER_AVATAR, avatar);
            editor.putString(Constants.USER_SIGN, sign);
            editor.apply();
        }
    }

    public void clearCache(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.USER_INFO, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.apply();
    }

    public void getCache(Context context) {
        SharedPreferences shareData = context.getSharedPreferences(Constants.USER_INFO, 0);
        id = shareData.getString(Constants.USER_ID, null);
        userSig = shareData.getString(Constants.USER_SIG, null);
        token = shareData.getString(Constants.USER_TOKEN,null);
        nickName = shareData.getString(Constants.USER_NICK, null);
        avatar = shareData.getString(Constants.USER_AVATAR, null);
        sign = shareData.getString(Constants.USER_SIGN, null);
    }

    public int getIdStatus() {
        return id_status;
    }

    public void setIdStatus(int id_status) {
        this.id_status = id_status;
    }

}