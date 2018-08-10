package com.shouyubang.android.inter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.shouyubang.android.inter.record.RecordActivity;
import com.shouyubang.android.inter.utils.CallUtil;
import com.tencent.callsdk.ILVCallListener;
import com.tencent.callsdk.ILVCallManager;
import com.tencent.callsdk.ILVIncomingListener;
import com.tencent.callsdk.ILVIncomingNotification;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends SingleFragmentActivity implements ILVIncomingListener, ILVCallListener {

    private static final String TAG = "MainActivity";

    private CoordinatorLayout layoutRoot;

    private AlertDialog mIncomingDlg;
    private int mCurIncomingId;

    private static final int REQUEST_PHONE_PERMISSIONS = 20;

    @Override
    protected Fragment createFragment() {
        return MainFragment.newInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置通话回调
        ILVCallManager.getInstance().addIncomingListener(this);
        ILVCallManager.getInstance().addCallListener(this);
        checkPermission();
    }

    /**
     * Permission Check
     */
    void checkPermission() {
        final List<String> permissionsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.READ_PHONE_STATE);
            if ((checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if ((checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.CAMERA);
            if ((checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.RECORD_AUDIO);
            if (permissionsList.size() != 0) {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_PHONE_PERMISSIONS);
            }
        }
    }

    @Override
    public void onCallEstablish(int callId) {
        Log.i(TAG, "Call Establish :"+callId);
    }

    @Override
    public void onCallEnd(int callId, int endResult, String endInfo) {
        if (mCurIncomingId == callId){
            mIncomingDlg.dismiss();
        }
        Log.i(TAG, "End Call:"+endResult+"-"+endInfo+"/"+callId);
        Log.e("XDBG_END", "onCallEnd->id: "+callId+"|"+endResult+"|"+endInfo);
    }

    @Override
    public void onException(int iExceptionId, int errCode, String errMsg) {
        Log.i(TAG, "Exception id:"+iExceptionId+", "+errCode+"-"+errMsg);
    }

    @Override
    public void onNewIncomingCall(final int callId, final int callType, final ILVIncomingNotification notification) {
        Log.i(TAG, "New Call from:"+notification.getSender()+"/"+callId+"-"+notification);
        if (null != mIncomingDlg){  // 关闭遗留来电对话框
            mIncomingDlg.dismiss();
        }
        mCurIncomingId = callId;
        mIncomingDlg = new AlertDialog.Builder(this)
                .setTitle("新来电  "+ encryptPhoneNumber(notification.getSender()))
                .setMessage(notification.getNotifDesc())
                .setPositiveButton("接听", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CallUtil.acceptCall(MainActivity.this, callId, notification.getSponsorId(), callType);
                        Log.i(TAG, "Accept Call :"+mCurIncomingId);
                    }
                })
                .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int ret = ILVCallManager.getInstance().rejectCall(mCurIncomingId);
                        Log.i(TAG, "Reject Call:"+ret+"/"+mCurIncomingId);
                    }
                })
                .create();
        mIncomingDlg.setCanceledOnTouchOutside(false);
        mIncomingDlg.show();
    }

    @Override
    protected void onDestroy() {
        ILVCallManager.getInstance().removeIncomingListener(this);
        ILVCallManager.getInstance().removeCallListener(this);
        super.onDestroy();
    }

    private String encryptPhoneNumber(String phone) {
        return phone.substring(0,6)+"****"+phone.substring(10,phone.length());
    }
}
