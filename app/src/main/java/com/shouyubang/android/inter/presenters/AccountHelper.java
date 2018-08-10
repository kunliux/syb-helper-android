package com.shouyubang.android.inter.presenters;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.shouyubang.android.inter.model.MySelfInfo;
import com.shouyubang.android.inter.model.RequestBackInfo;
import com.shouyubang.android.inter.presenters.view.LoginView;
import com.shouyubang.android.inter.presenters.view.LogoutView;
import com.shouyubang.android.inter.presenters.view.UploadVideoView;
import com.shouyubang.android.inter.record.BizService;
import com.shouyubang.android.inter.record.PutObjectTask;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;

/**
 * 登录的数据处理类
 */
public class AccountHelper {
    private static final String TAG = "AccountHelper";

    private Context mContext;
    private LoginView mLoginView;
    private LogoutView mLogoutView;
    public UploadVideoView mUploadView;

    public AccountHelper(Context context) {
        mContext = context;
    }

    public AccountHelper(Context context, LoginView loginView) {
        mContext = context;
        mLoginView = loginView;
    }

    public AccountHelper(Context context, LogoutView logoutView) {
        mContext = context;
        mLogoutView = logoutView;
    }

    public AccountHelper(Context context, UploadVideoView uploadView) {
        mContext = context;
        mUploadView = uploadView;
    }


    //独立模式登录
    private StandardLoginTask loginTask;

    private class StandardLoginTask extends AsyncTask<String, Integer, RequestBackInfo> {

        String id;
        String password;

        StandardLoginTask(String id, String password) {
            this.id  = id;
            this.password = password;
        }

        @Override
        protected RequestBackInfo doInBackground(String... strings) {

            return StaffServerHelper.getInstance().loginId(id, password);
        }

        @Override
        protected void onPostExecute(RequestBackInfo result) {

            if (result != null) {
                if (result.getErrorCode() >= 0) {
                    MySelfInfo.getInstance().writeToCache(mContext);
                    Log.e(TAG, "Step 1 Pass" + MySelfInfo.getInstance().getId() +" : "+ MySelfInfo.getInstance().getUserSig());
                    //登录
                    iLiveLogin(MySelfInfo.getInstance().getId(), MySelfInfo.getInstance().getUserSig());
                } else {
                    mLoginView.loginFail("Module_TLSSDK", result.getErrorCode(), result.getErrorInfo());
                }
            }

        }
    }


    public void iLiveLogin(String id, String sig) {
        //登录
        ILiveLoginManager.getInstance().iLiveLogin(id, sig, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                if (mLoginView != null)
                    mLoginView.loginSuccess();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                if (mLoginView != null)
                    mLoginView.loginFail(module, errCode, errMsg);
            }
        });
    }


    /**
     * 退出imsdk <p> 退出成功会调用退出AVSDK
     */
    public void iLiveLogout() {
        //TODO 新方式登出ILiveSDK
        ILiveLoginManager.getInstance().iLiveLogout(new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                Log.i(TAG, "IMLogout SUCCESS !");
                //清除本地缓存
                MySelfInfo.getInstance().clearCache(mContext);
                MySelfInfo.getInstance().setId(null);
                mLogoutView.logoutSuccess();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Log.e(TAG, "IMLogout fail ：" + module + "|" + errCode + " msg " + errMsg);
            }
        });
    }

    /**
     * 独立模式 登录
     */
    public void standardLogin(String id, String password) {
        loginTask = new StandardLoginTask(id, password);
        loginTask.execute(id, password);

    }


    /**
     * 独立模式 注册
     */
    public void standardRegister(final String id, final String psw) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final RequestBackInfo result = StaffServerHelper.getInstance().registerId(id, psw);
                if (null != mContext) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        public void run() {
                            if (result != null && result.getErrorCode() >= 0) {
                                standardLogin(id, psw);
                            } else if (result != null) {
                                Toast.makeText(mContext, "  " + result.getErrorCode() + " : " + result.getErrorInfo(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }


    /**
     * 独立模式 登出
     */
    public void standardLogout(final String id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBackInfo result = StaffServerHelper.getInstance().logoutId(id);
                if (result != null && (result.getErrorCode() >= 0 || result.getErrorCode() == 10008)) {
                }
            }
        }).start();
        iLiveLogout();
    }

    /**
     * 上传
     */
    public void upload(final BizService bizService, final String currentPath, final Integer videoId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String filename = com.tencent.cos.utils.FileUtils.getFileName(currentPath);
                String cosPath = "/" + filename; //cos 上的路径
                PutObjectTask.putObjectForLargeFile(AccountHelper.this, bizService, cosPath, currentPath, videoId);
            }
        }).start();
    }


}
