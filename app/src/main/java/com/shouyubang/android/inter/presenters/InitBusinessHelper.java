package com.shouyubang.android.inter.presenters;

import android.content.Context;
import android.util.Log;

import com.shouyubang.android.inter.utils.Constants;
import com.shouyubang.android.inter.utils.CrashHandler;
import com.tencent.TIMManager;
import com.tencent.callsdk.ILVCallConfig;
import com.tencent.callsdk.ILVCallManager;
import com.tencent.callsdk.ILVCallNotification;
import com.tencent.callsdk.ILVCallNotificationListener;
import com.tencent.ilivesdk.ILiveSDK;


/**
 * 初始化
 * 包括imsdk等
 */
public class InitBusinessHelper {
    private static String TAG = "InitBusinessHelper";

    private InitBusinessHelper() {
    }
    private static String appVer = "1.0";


    /**
     * 初始化App
     */
    public static void initApp(final Context context) {
        //初始化avsdk imsdk
        TIMManager.getInstance().disableBeaconReport();

        // 初始化ILiveSDK
        ILiveSDK.getInstance().initSdk(context, Constants.IM_APP_ID, Constants.ACCOUNT_TYPE);

        ILVCallManager.getInstance().init(new ILVCallConfig()
                .setNotificationListener(new ILVCallNotificationListener() {
                    @Override
                    public void onRecvNotification(int callid, ILVCallNotification notification) {
                        Log.i(TAG, "onRecvNotification->notify id:"+notification.getNotifId()+"|"+notification.getUserInfo()+"/"+notification.getSender());
                    }
                })
                .setTimeOut(3000) // 超时时间为30秒
                .setAutoBusy(true)); // 忙时自动拒接模式

        Log.i(TAG, "Init CallSDK...");

        //初始化CrashReport系统
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(context);
    }
}
