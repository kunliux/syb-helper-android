package com.shouyubang.android.inter.utils;

import android.content.Context;
import android.content.Intent;

import com.shouyubang.android.inter.call.CallActivity;
import com.tencent.ilivesdk.core.ILiveLoginManager;

/**
 * Created by KunLiu on 2017/7/21.
 */

public class CallUtil {

    /**
     * 发起呼叫
     */
    public static void makeCall(final Context ctx, final int callType, final String callNum){
        Intent intent = new Intent();
        intent.setClass(ctx, CallActivity.class);
        intent.putExtra("HostId", ILiveLoginManager.getInstance().getMyUserId());
        intent.putExtra("CallId", 0);
        intent.putExtra("CallType", callType);
        intent.putExtra("CallNumber", callNum);
        ctx.startActivity(intent);
    }

    public static void acceptCall(final Context ctx, final int incomingCallId, final String hostId, final int callType){
        Intent intent = new Intent();
        intent.setClass(ctx, CallActivity.class);
        intent.putExtra("HostId", hostId);
        intent.putExtra("CallId", incomingCallId);
        intent.putExtra("CallType", callType);
        ctx.startActivity(intent);
    }
}
