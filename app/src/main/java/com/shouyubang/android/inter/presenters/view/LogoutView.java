package com.shouyubang.android.inter.presenters.view;


/**
 * 登出回调
 */
public interface LogoutView extends MVPView{

    void logoutSuccess();

    void logoutFail();
}
