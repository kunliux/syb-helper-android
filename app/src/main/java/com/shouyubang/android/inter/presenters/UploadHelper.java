package com.shouyubang.android.inter.presenters;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.shouyubang.android.inter.model.MySelfInfo;
import com.shouyubang.android.inter.presenters.view.UploadImageView;
import com.shouyubang.android.inter.utils.Constants;
import com.tencent.cos.COSClient;
import com.tencent.cos.COSConfig;
import com.tencent.cos.common.COSEndPoint;
import com.tencent.cos.model.COSRequest;
import com.tencent.cos.model.COSResult;
import com.tencent.cos.model.PutObjectRequest;
import com.tencent.cos.model.PutObjectResult;
import com.tencent.cos.task.listener.IUploadTaskListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Cos人图片上传类
 */
public class UploadHelper extends Presenter {

    private static final String TAG = "UploadHelper";

    private final String coverBucket = Constants.COVER_BUCKET;
    private final String avatarBucket = Constants.AVATAR_BUCKET;
    private final String appid = Constants.COS_APP_ID;

    private final static int THREAD_GET_SIG = 1;
    private final static int THREAD_UPLOAD_COVER = 2;
    private final static int THREAD_GETSIG_COVER = 3;
    private final static int THREAD_UPLOAD_AVATAR = 4;
    private final static int THREAD_GETSIG_AVATAR = 5;

    private final static int MAIN_CALL_BACK = 1;
    private final static int MAIN_PROCESS = 2;

    private Context mContext;
    private UploadImageView mView;
    private HandlerThread mThread;
    private Handler mHandler;
    private Handler mMainHandler;

    public UploadHelper(Context context, UploadImageView view) {
        Log.i(TAG, "UploadHelper Create.");
        mContext = context;
        mView = view;
        mThread = new HandlerThread("upload");
        mThread.start();
        mHandler = new Handler(mThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case THREAD_GET_SIG:
                        doUpdateSig();
                        break;
                    case THREAD_UPLOAD_COVER:
                        doUploadCover((String) msg.obj, true);
                        break;
                    case THREAD_GETSIG_COVER:
                        doUpdateSig();
                        doUploadCover((String) msg.obj, false);
                        break;
                    case THREAD_UPLOAD_AVATAR:
                        doUploadAvatar((String) msg.obj, true);
                        break;
                    case THREAD_GETSIG_AVATAR:
                        doUpdateSig();
                        doUploadAvatar((String) msg.obj, false);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        mMainHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Log.d(TAG, "handleMessage id:" + msg.what);
                switch (msg.what) {
                    case MAIN_CALL_BACK:
                        if (null != mView)
                            mView.onUploadResult(msg.arg1, (String) msg.obj);
                        break;
                    case MAIN_PROCESS:
                        if (null != mView)
                            mView.onUploadProcess(msg.arg1);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    private String createNetUrl() {
        return "/" + MySelfInfo.getInstance().getId() + "_" + System.currentTimeMillis() + ".jpg";
    }

    private void doUpdateSig() {
        String sig = StaffServerHelper.getInstance().getCosSig();
        MySelfInfo.getInstance().setCosSig(sig);
        Log.d(TAG, "doUpdateSig->get sig: " + sig);
    }

    /**
     * 复制单个文件
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public boolean copyFile(String oldPath, String newPath) {
        try {
            int byteSum = 0;
            int byteRead = 0;
            File oldFile = new File(oldPath);
            if (oldFile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteRead = inStream.read(buffer)) != -1) {
                    byteSum += byteRead; //字节数 文件大小
                    System.out.println(byteSum);
                    fs.write(buffer, 0, byteRead);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            Log.e(TAG, "copy file failed!");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void doUploadCover(final String path, boolean bRetry) {
        Log.i(TAG, "Start upload cover.");
        String sig = MySelfInfo.getInstance().getCosSig();
        Log.i(TAG, "Sig: " + sig);
        if (TextUtils.isEmpty(sig)) {
            if (bRetry) {
                Message msg = new Message();
                msg.what = THREAD_GETSIG_COVER;
                msg.obj = path;

                mHandler.sendMessage(msg);
            }
            return;
        }

        String tmpPath = path;
        if ("Xiaomi".equals(android.os.Build.MANUFACTURER)) { // 复制到tmp文件再上传(小米5机器上无法占用文件)
            tmpPath = path + "_tmp";
            copyFile(path, tmpPath);
        }

        //创建COSClientConfig对象，根据需要修改默认的配置参数
        final COSConfig config = new COSConfig();
        //设置园区
        config.setEndPoint(COSEndPoint.COS_SH);

        //创建COClient对象，实现对象存储的操作
        COSClient cos = new COSClient(mContext, appid, config, "kliu");

        Log.d(TAG, "upload cover: " + tmpPath);

        //上传文件
        PutObjectRequest putObjectRequest = new PutObjectRequest();
        putObjectRequest.setBucket(coverBucket);
        putObjectRequest.setCosPath(createNetUrl());
        putObjectRequest.setSrcPath(tmpPath);
        putObjectRequest.setSign(sig);
        putObjectRequest.setListener(new IUploadTaskListener(){
            @Override
            public void onSuccess(COSRequest cosRequest, COSResult cosResult) {
                PutObjectResult result = (PutObjectResult) cosResult;
                if(result != null){
                    Log.i(TAG, "upload succeed: " + result.url);
                    Message msg = new Message();
                    msg.what = MAIN_CALL_BACK;
                    msg.arg1 = 0;
                    msg.obj = result.source_url;

                    mMainHandler.sendMessage(msg);
                }
            }

            @Override
            public void onFailed(COSRequest COSRequest, final COSResult cosResult) {
                Log.w(TAG, "upload error code: " + cosResult.code + " msg:" + cosResult.msg);
                if (-96 == cosResult.code) {  // 签名过期重试
                    Message msg = new Message();
                    msg.what = THREAD_GETSIG_COVER;
                    msg.obj = path;

                    mHandler.sendMessage(msg);
                } else {
                    Message msg = new Message();
                    msg.what = MAIN_CALL_BACK;
                    msg.arg1 = cosResult.code;
                    msg.obj = cosResult.msg;

                    mMainHandler.sendMessage(msg);
                }
            }

            @Override
            public void onProgress(COSRequest cosRequest, final long currentSize, final long totalSize) {
                Log.d(TAG, "onUploadProgress: " + currentSize + "/" + totalSize);
                Message msg = new Message();
                msg.what = MAIN_PROCESS;
                msg.arg1 = (int) (currentSize * 100 / totalSize);

                mMainHandler.sendMessage(msg);
            }

            @Override
            public void onCancel(COSRequest cosRequest, COSResult cosResult) {

            }
        });

        PutObjectResult putObjectResult = cos.putObject(putObjectRequest);

        if (0 != putObjectResult.code){
            Message msg = new Message();
            msg.what = MAIN_CALL_BACK;
            msg.arg1 = -1;
            msg.obj = "upload failed";

            mMainHandler.sendMessage(msg);
        }
    }

    private void doUploadAvatar(final String path, boolean bRetry) {
        Log.i(TAG, "Start upload avatar.");
        String sig = MySelfInfo.getInstance().getCosSig();
        Log.i(TAG, "Sig: " + sig);
        if (TextUtils.isEmpty(sig)) {
            if (bRetry) {
                Message msg = new Message();
                msg.what = THREAD_GETSIG_AVATAR;
                msg.obj = path;

                mHandler.sendMessage(msg);
            }
            return;
        }

        String tmpPath = path;
        if ("Xiaomi".equals(android.os.Build.MANUFACTURER)) { // 复制到tmp文件再上传(小米5机器上无法占用文件)
            tmpPath = path + "_tmp";
            copyFile(path, tmpPath);
        }

        //创建COSClientConfig对象，根据需要修改默认的配置参数
        final COSConfig config = new COSConfig();
        //设置园区
        config.setEndPoint(COSEndPoint.COS_SH);

        //创建COClient对象，实现对象存储的操作
        COSClient cos = new COSClient(mContext, appid, config, "kliu");

        Log.d(TAG, "upload avatar: " + tmpPath);

        //上传文件
        PutObjectRequest putObjectRequest = new PutObjectRequest();
        putObjectRequest.setBucket(avatarBucket);
        putObjectRequest.setCosPath(createNetUrl());
        putObjectRequest.setSrcPath(tmpPath);
        putObjectRequest.setSign(sig);
        putObjectRequest.setListener(new IUploadTaskListener(){
            @Override
            public void onSuccess(COSRequest cosRequest, COSResult cosResult) {
                PutObjectResult result = (PutObjectResult) cosResult;
                if(result != null){
                    Log.i(TAG, "upload succeed: " + result.url);
                    Message msg = new Message();
                    msg.what = MAIN_CALL_BACK;
                    msg.arg1 = 0;
                    msg.obj = result.source_url;

                    mMainHandler.sendMessage(msg);
                }
            }

            @Override
            public void onFailed(COSRequest COSRequest, final COSResult cosResult) {
                Log.w(TAG, "upload error code: " + cosResult.code + " msg:" + cosResult.msg);
                if (-96 == cosResult.code) {  // 签名过期重试
                    Message msg = new Message();
                    msg.what = THREAD_GETSIG_COVER;
                    msg.obj = path;

                    mHandler.sendMessage(msg);
                } else {
                    Message msg = new Message();
                    msg.what = MAIN_CALL_BACK;
                    msg.arg1 = cosResult.code;
                    msg.obj = cosResult.msg;

                    mMainHandler.sendMessage(msg);
                }
            }

            @Override
            public void onProgress(COSRequest cosRequest, final long currentSize, final long totalSize) {
                Log.d(TAG, "onUploadProgress: " + currentSize + "/" + totalSize);
                Message msg = new Message();
                msg.what = MAIN_PROCESS;
                msg.arg1 = (int) (currentSize * 100 / totalSize);

                mMainHandler.sendMessage(msg);
            }

            @Override
            public void onCancel(COSRequest cosRequest, COSResult cosResult) {

            }
        });

        PutObjectResult putObjectResult = cos.putObject(putObjectRequest);

        if (0 != putObjectResult.code){
            Message msg = new Message();
            msg.what = MAIN_CALL_BACK;
            msg.arg1 = -1;
            msg.obj = "upload failed";

            mMainHandler.sendMessage(msg);
        }
    }

    public void updateSig() {
        mHandler.sendEmptyMessage(THREAD_GET_SIG);
    }

    public void uploadCover(String path) {
        Message msg = new Message();
        msg.what = THREAD_UPLOAD_COVER;
        msg.obj = path;

        mHandler.sendMessage(msg);
    }

    public void uploadAvatar(String path) {
        Message msg = new Message();
        msg.what = THREAD_UPLOAD_AVATAR;
        msg.obj = path;

        mHandler.sendMessage(msg);
    }

    @Override
    public void onDestroy() {
        mView = null;
        mContext = null;
    }
}
