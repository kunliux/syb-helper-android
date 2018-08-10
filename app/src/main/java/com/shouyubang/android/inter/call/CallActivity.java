package com.shouyubang.android.inter.call;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shouyubang.android.inter.R;
import com.shouyubang.android.inter.model.MySelfInfo;
import com.shouyubang.android.inter.presenters.StaffServerHelper;
import com.shouyubang.android.inter.utils.DialogUtil;
import com.tencent.av.sdk.AVAudioCtrl;
import com.tencent.av.sdk.AVView;
import com.tencent.callsdk.ILVBCallMemberListener;
import com.tencent.callsdk.ILVCallConstants;
import com.tencent.callsdk.ILVCallListener;
import com.tencent.callsdk.ILVCallManager;
import com.tencent.callsdk.ILVCallOption;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.ilivesdk.view.AVVideoView;

import java.util.ArrayList;
import java.util.List;

/**
 * 通话界面
 */
public class CallActivity extends Activity implements ILVCallListener, ILVBCallMemberListener, View.OnClickListener {

    private static final String TAG = "CallActivity";
    private static final int REQUEST_PHONE_PERMISSIONS = 21;

    private static final int REQUEST_ACCEPT_CALL = 1;
    private static final int REQUEST_END_CALL = 2;

    private ImageButton btnEndCall, btnSwitch, btnCamera, btnMic, btnBeauty, btnSpeaker;
    private Button mEndBeauty;
    private AVRootView avRootView;
    private TextView tvTitle, teMic, teCamera, teBeauty, teSpeaker;
    private RelativeLayout rlControl;
    private LinearLayout llBeauty;
    private SeekBar sbBeauty;

    private String mHostId;
    private int mCallId;
    private int mCallType;
    private int mBeautyRate;
    private String callNum;

    private boolean bSwitchEnable = true;
    private boolean bCameraEnable = true;
    private boolean bMicEnable = true;
    private boolean bSpeaker = true;
    private int mCurCameraId = ILiveConstants.FRONT_CAMERA;

    private void initView() {
        avRootView = (AVRootView) findViewById(R.id.av_root_view);
        tvTitle = (TextView) findViewById(R.id.tv_call_title);
        teMic = (TextView) findViewById(R.id.te_mic);
        teCamera = (TextView) findViewById(R.id.te_camera);
        teBeauty = (TextView) findViewById(R.id.te_beauty);
        teSpeaker = (TextView) findViewById(R.id.te_speaker);

        btnSwitch = (ImageButton) findViewById(R.id.btn_switch_camera);
        btnCamera = (ImageButton) findViewById(R.id.btn_camera);
        btnMic = (ImageButton) findViewById(R.id.btn_mic);
        btnBeauty = (ImageButton) findViewById(R.id.btn_beauty);
        btnSpeaker = (ImageButton) findViewById(R.id.btn_speaker);
        btnEndCall = (ImageButton) findViewById(R.id.btn_end);

        llBeauty = (LinearLayout) findViewById(R.id.ll_beauty_setting);
        mEndBeauty = (Button) findViewById(R.id.btn_beauty_setting_finish);
        rlControl = (RelativeLayout) findViewById(R.id.rl_control);
        btnEndCall.setVisibility(View.VISIBLE);
    }

    private void changeCamera() {
        if (bCameraEnable) {
            ILVCallManager.getInstance().enableCamera(mCurCameraId, false);
            avRootView.closeUserView(ILiveLoginManager.getInstance().getMyUserId(), AVView.VIDEO_SRC_TYPE_CAMERA, true);
        } else {
            ILVCallManager.getInstance().enableCamera(mCurCameraId, true);
        }
        bCameraEnable = !bCameraEnable;
        btnCamera.setBackgroundResource(bCameraEnable ? R.mipmap.video_unpress : R.mipmap.video_press);
        teCamera.setTextColor(bCameraEnable ? getResources().getColor(R.color.material_grey_500) : getResources().getColor(R.color.material_white));
    }

    private void changeMic() {
        if (bMicEnable) {
            ILVCallManager.getInstance().enableMic(false);
        } else {
            ILVCallManager.getInstance().enableMic(true);
        }

        bMicEnable = !bMicEnable;
        btnMic.setBackgroundResource(bMicEnable? R.mipmap.mic_unpress : R.mipmap.mic_press);
        teMic.setTextColor(bMicEnable? getResources().getColor(R.color.material_grey_500) : getResources().getColor(R.color.material_white));
    }

    private void changeSpeaker() {
        if (bSpeaker) {
            ILiveSDK.getInstance().getAvAudioCtrl().setAudioOutputMode(AVAudioCtrl.OUTPUT_MODE_HEADSET);
        } else {
            ILiveSDK.getInstance().getAvAudioCtrl().setAudioOutputMode(AVAudioCtrl.OUTPUT_MODE_SPEAKER);
        }
        bSpeaker = !bSpeaker;
        btnSpeaker.setBackgroundResource(bSpeaker ? R.mipmap.speaker_unpress : R.mipmap.speaker_press);
        teSpeaker.setTextColor(bSpeaker ? getResources().getColor(R.color.material_grey_500) : getResources().getColor(R.color.material_white));
    }

    private void switchCamera() {
        mCurCameraId = (ILiveConstants.FRONT_CAMERA==mCurCameraId) ? ILiveConstants.BACK_CAMERA : ILiveConstants.FRONT_CAMERA;
        ILVCallManager.getInstance().switchCamera(mCurCameraId);
        bSwitchEnable = !bSwitchEnable;
        btnSwitch.setBackgroundResource(bSwitchEnable ? R.mipmap.switch_camera_unpress : R.mipmap.switch_camera_press);
    }

    private void setBeauty() {
        if (null == sbBeauty) {
            sbBeauty = (SeekBar) findViewById(R.id.sb_beauty_progress);
            sbBeauty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub
                    Toast.makeText(CallActivity.this, "beauty " + mBeautyRate + "%", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    // TODO Auto-generated method stub
                    mBeautyRate = progress;
                    ILiveSDK.getInstance().getAvVideoCtrl().inputBeautyParam(9.0f * progress / 100.0f);
                }
            });
        }
        llBeauty.setVisibility(View.VISIBLE);
        rlControl.setVisibility(View.INVISIBLE);
    }

    private void showInviteDlg(){
        final EditText etInput = new EditText(this);
        new AlertDialog.Builder(this).setTitle(R.string.invite_tip)
                .setView(etInput)
                .setPositiveButton(R.string.invite, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!TextUtils.isEmpty(etInput.getText().toString())) {
                            List<String> nums = new ArrayList<String>();
                            nums.add(etInput.getText().toString());
                            ILVCallManager.getInstance().inviteUser(mCallId, nums);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        initView();
        askPermissions();

        // 添加通话回调
        ILVCallManager.getInstance().addCallListener(this);

        Intent intent = getIntent();
        mHostId = intent.getStringExtra("HostId");
        mCallType = intent.getIntExtra("CallType", ILVCallConstants.CALL_TYPE_VIDEO);
        mCallId = intent.getIntExtra("CallId", 0);
        ILVCallOption option = new ILVCallOption(mHostId)
                .callTips("邀请你进行视频聊天")
                .setMemberListener(this)
                .setCallType(mCallType);
        if (0 == mCallId) { // 发起呼叫
            callNum = intent.getStringExtra("CallNumber");
            mCallId = ILVCallManager.getInstance().makeCall(callNum, option);

        }else{  // 接听呼叫
            ILVCallManager.getInstance().acceptCall(mCallId, option);
        }

        ILiveLoginManager.getInstance().setUserStatusListener(new ILiveLoginManager.TILVBStatusListener() {
            @Override
            public void onForceOffline(int error, String message) {
                finish();
            }
        });

        tvTitle.setText("新来电\n" + mHostId);

        avRootView.setAutoOrientation(false);
        ILVCallManager.getInstance().initAvView(avRootView);
//        ILVCallManager.getInstance().enableMic(false);

        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
            }});

        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMic();
            }});

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCamera();
            }});

        btnBeauty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBeauty();
            }});

        btnSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSpeaker();
            }});

        btnEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ILVCallManager.getInstance().endCall(mCallId);
            }});

        mEndBeauty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llBeauty.setVisibility(View.GONE);
                rlControl.setVisibility(View.VISIBLE);
            }});
    }

    @Override
    protected void onResume() {
        ILVCallManager.getInstance().onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        ILVCallManager.getInstance().onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        ILVCallManager.getInstance().removeCallListener(this);
        ILVCallManager.getInstance().onDestory();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        ILVCallManager.getInstance().endCall(mCallId);
    }

    @Override
    public void onClick(View v) {
        // library中不能使用switch索引资源id
        if (v.getId() == R.id.btn_end){
            ILVCallManager.getInstance().endCall(mCallId);
        }else if (v.getId() == R.id.btn_camera){
            changeCamera();
        }else if(v.getId() == R.id.btn_mic){
            changeMic();
        }else if(v.getId() == R.id.btn_switch_camera){
            switchCamera();
        }else if(v.getId() == R.id.btn_speaker){
            changeSpeaker();
        }else if(v.getId() == R.id.btn_beauty){
            setBeauty();
        }else if(v.getId() == R.id.btn_beauty_setting_finish){
            llBeauty.setVisibility(View.GONE);
            rlControl.setVisibility(View.VISIBLE);
        }else if(v.getId() == R.id.btn_invite){
            showInviteDlg();
        }
    }


    /**
     * 会话建立回调
     * @param callId
     */
    @Override
    public void onCallEstablish(int callId) {
        btnEndCall.setVisibility(View.VISIBLE);

        Log.d("ILVB-DBG", "onCallEstablish->0:"+avRootView.getViewByIndex(0).getIdentifier()+"/"+avRootView.getViewByIndex(1).getIdentifier());
        new CallRequestTask(mCallId, mHostId, MySelfInfo.getInstance().getId(), REQUEST_ACCEPT_CALL).execute();

        avRootView.swapVideoView(0, 1);
        // 设置点击小屏切换及可拖动
        for (int i=1; i<ILiveConstants.MAX_AV_VIDEO_NUM; i++){
            final int index = i;
            AVVideoView minorView = avRootView.getViewByIndex(i);
            if (ILiveLoginManager.getInstance().getMyUserId().equals(minorView.getIdentifier())){
                minorView.setMirror(true);      // 本地镜像
            }
            minorView.setDragable(true);    // 小屏可拖动
            minorView.setGestureListener(new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    avRootView.swapVideoView(0, index);     // 与大屏交换
                    return false;
                }
            });
        }
    }

    /**
     *  会话结束回调
     * @param callId
     * @param endResult 结束原因
     * @param endInfo   结束描述
     */
    @Override
    public void onCallEnd(int callId, int endResult, String endInfo) {
        Log.e(TAG, "onCallEnd->id: "+callId+"|"+endResult+"|"+endInfo);
        new CallRequestTask(mCallId, mHostId, MySelfInfo.getInstance().getId(), REQUEST_END_CALL).execute();
        finish();
    }

    @Override
    public void onException(int iExceptionId, int errCode, String errMsg) {

    }

    @Override
    public void onCameraEvent(String id, boolean bEnable) {
        Log.i(TAG, "["+id+"] "+(bEnable?"open":"close")+" camera");
    }

    @Override
    public void onMicEvent(String id, boolean bEnable) {
        Log.i(TAG, "["+id+"] "+(bEnable?"open":"close")+" mic");
    }
    /*
    @Override
    public void onMemberEvent(String id, boolean bEnter) {
        Log.i(TAG, "["+id+"] "+(bEnter?"join":"exit")+" call");
    }
*/
    private void askPermissions() {
        final List<String> permissionsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ActivityCompat.checkSelfPermission(CallActivity.this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.CAMERA);
            if ((ActivityCompat.checkSelfPermission(CallActivity.this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.RECORD_AUDIO);
            if ((ActivityCompat.checkSelfPermission(CallActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionsList.size() != 0) {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_PHONE_PERMISSIONS);
            }
        } else {
            DialogUtil.showToast(CallActivity.this, getString(R.string.permissions_not_granted));
        }
    }

    private class CallRequestTask extends AsyncTask<Void, Void, Boolean> {

        private String mCallerId, mAnswerId;
        private int mCallId, mRequestId;

        CallRequestTask(Integer callId, String callerId, String answerId, Integer requestId) {
            mCallId = callId;
            mCallerId = callerId;
            mAnswerId = answerId;
            mRequestId = requestId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            switch (mRequestId) {
                case REQUEST_ACCEPT_CALL:
                    return StaffServerHelper.acceptCall(mCallId, mCallerId, mAnswerId);
                case REQUEST_END_CALL:
                    return StaffServerHelper.endCall(mCallId, mCallerId, mAnswerId);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            switch (mRequestId) {
                case REQUEST_ACCEPT_CALL:
                    if(success) {
                        DialogUtil.showToast(CallActivity.this, "通话开始");
                    }
                    break;
                case REQUEST_END_CALL:
                    if (success) {
                        DialogUtil.showToast(CallActivity.this, "通话结束");
                    }
                    break;
            }
        }
    }

}
