package com.shouyubang.android.inter.presenters.view;

/**
 * 图片上传页
 */
public interface UploadImageView {

    void onUploadProcess(int percent);

    void onUploadResult(int code, String url);
}
