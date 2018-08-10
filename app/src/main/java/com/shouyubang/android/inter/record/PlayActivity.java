package com.shouyubang.android.inter.record;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.shouyubang.android.inter.R;
import com.shouyubang.android.inter.manage.VideoLabActivity;
import com.shouyubang.android.inter.presenters.AccountHelper;
import com.shouyubang.android.inter.presenters.view.UploadVideoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlayActivity extends AppCompatActivity implements UploadVideoView {
    private static final String TAG = "PlayActivity";

    public final static String DATA = "URL";

    private static final String VIDEO_ID = "video_id";

    @BindView(R.id.playView)
    PlayView playView;
    @BindView(R.id.btn_play)
    Button playBtn;
    @BindView(R.id.btn_retake)
    Button retakeBtn;
    @BindView(R.id.btn_upload)
    Button uploadBtn;

    private long playPosition = -1;
    private long duration = -1;
    String currentPath;
    int videoId;
    BizService bizService;
    AccountHelper mUploadHelper;

    public static Intent newIntent(Context packageContext, String uri, Integer videoId) {
        Intent intent = new Intent(packageContext, PlayActivity.class);
        intent.putExtra(DATA, uri);
        intent.putExtra(VIDEO_ID, videoId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        ButterKnife.bind(this);
        mUploadHelper = new AccountHelper(getApplicationContext(), this);

        //初始化 cosClient
        bizService = BizService.instance();
        bizService.init(getApplicationContext());

        currentPath = getIntent().getStringExtra(DATA);
        videoId = getIntent().getIntExtra(VIDEO_ID, 0);

        if (currentPath == null)
            finish();
        Log.i(TAG, "uri:  " + currentPath);

        playView.setVideoURI(Uri.parse(currentPath));

        playView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playView.seekTo(1);
                startVideo();
            }
        });

        playView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //获取视频资源的宽度
                int videoWidth = mp.getVideoWidth();
                //获取视频资源的高度
                int videoHeight = mp.getVideoHeight();
                playView.setSizeH(videoHeight);
                playView.setSizeW(videoWidth);
                playView.requestLayout();
                duration = mp.getDuration();
                play();
            }
        });

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();//如果为true，则表示屏幕“亮”了，否则屏幕“暗”了。
        if (!isScreenOn) {
            pauseVideo();
        }
    }

    @OnClick({R.id.btn_play, R.id.btn_retake, R.id.btn_upload})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_play:
                play();
                break;
            case R.id.btn_retake:
                retake();
                break;
            case R.id.btn_upload:
                upload();
                uploadBtn.setClickable(false);
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (playPosition > 0) {
            pauseVideo();
        }
        playView.seekTo((int) ((playPosition > 0 && playPosition < duration) ? playPosition : 1));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playView.stopPlayback();
    }

    @Override
    protected void onPause() {
        super.onPause();
        playView.pause();
        playPosition = playView.getCurrentPosition();
        pauseVideo();

    }

    @Override
    public void onBackPressed() {
//        FileUtils.deleteFile(currentPath);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    private void pauseVideo() {
        playView.pause();
        playBtn.setText("回看");
    }

    private void startVideo() {
        playView.start();
        playBtn.setText("暂停");
    }

    /**
     * 播放
     */
    private void play() {
        if (playView.isPlaying()) {
            pauseVideo();
        } else {
            if (playView.getCurrentPosition() == playView.getDuration()) {
                playView.seekTo(0);
            }
            startVideo();
        }
    }

    /**
     * 重录
     */
    private void retake() {
        PlayActivity.this.finish();
    }

    /**
     * 上传
     */
    private void upload() {
        mUploadHelper.upload(bizService, currentPath ,videoId);
    }


    @Override
    public void uploadSuccess() {
        Toast.makeText(PlayActivity.this, "上传视频成功", Toast.LENGTH_SHORT).show();
        jumpIntoVideoLabActivity();
    }

    @Override
    public void uploadFail(String module, int errCode, String errMsg) {
        Toast.makeText(PlayActivity.this, "上传视频失败", Toast.LENGTH_SHORT).show();
        uploadBtn.setClickable(true);
    }

    /**
     * 直接跳转主界面
     */
    private void jumpIntoVideoLabActivity() {
        Intent intent = new Intent(PlayActivity.this, VideoLabActivity.class);
        startActivity(intent);
        finish();
    }
}
