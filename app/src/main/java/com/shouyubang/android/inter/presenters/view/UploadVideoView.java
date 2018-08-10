package com.shouyubang.android.inter.presenters.view;

/**
 * 登录回调
 */
public interface UploadVideoView extends MVPView {

    void uploadSuccess();

    void uploadFail(String module, int errCode, String errMsg);
}
