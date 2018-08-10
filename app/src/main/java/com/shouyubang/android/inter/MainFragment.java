package com.shouyubang.android.inter;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.shouyubang.android.inter.account.LoginActivity;
import com.shouyubang.android.inter.account.ProfileInfoActivity;
import com.shouyubang.android.inter.account.SettingsActivity;
import com.shouyubang.android.inter.manage.CommentLabActivity;
import com.shouyubang.android.inter.model.MySelfInfo;
import com.shouyubang.android.inter.model.StaffProfile;
import com.shouyubang.android.inter.presenters.ProfileInfoHelper;
import com.shouyubang.android.inter.presenters.StaffServerHelper;
import com.shouyubang.android.inter.presenters.view.ProfileView;
import com.shouyubang.android.inter.record.VideoListActivity;
import com.shouyubang.android.inter.manage.VideoLabActivity;
import com.shouyubang.android.inter.utils.Constants;
import com.shouyubang.android.inter.utils.DialogUtil;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainFragment extends Fragment implements ProfileView {

    private static final String TAG = "MainFragment";

    private static final int REQUEST_SWITCH_ONLINE = 1;
    private static final int REQUEST_SWITCH_AWAY = 2;

    @BindView(R.id.user_avatar)
    ImageView mAvatarImage;
    @BindView(R.id.name)
    TextView mNameText;
    @BindView(R.id.staff_panel)
    LinearLayout mStaffPanel;
    @BindView(R.id.credits_value)
    TextView mCreditsValue;
    @BindView(R.id.rating_value)
    TextView mRatingValue;
    @BindView(R.id.times_value)
    TextView mTimesValue;
    @BindView(R.id.online_service_btn)
    LinearLayout mOnlineServiceBtn;
    @BindView(R.id.offline_service_btn)
    LinearLayout mOfflineServiceBtn;
    @BindView(R.id.video_manage_btn)
    RelativeLayout mVideoManageBtn;
    @BindView(R.id.comment_manage_btn)
    RelativeLayout mCommentManageBtn;
    @BindView(R.id.notification_btn)
    RelativeLayout mNotificationBtn;
    @BindView(R.id.setting_btn)
    RelativeLayout mSettingBtn;
    @BindView(R.id.status_switch)
    SwitchCompat mStatusSwitch;
    Unbinder unbinder;

    private ProfileInfoHelper mProfileHelper;


    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProfileHelper = new ProfileInfoHelper(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        mStaffPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(needLogin()) {
                    Intent i = LoginActivity.newIntent(getActivity());
                    startActivity(i);
                } else {
                    Intent i = ProfileInfoActivity.newIntent(getActivity());
                    startActivity(i);
                }
            }
        });
        mOnlineServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.showDialog(getActivity(), "该功能即将上线", false);
            }
        });
        mOfflineServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!needLogin()) {
                    Intent i = VideoListActivity.newIntent(getActivity());
                    startActivity(i);
                } else {
                    Intent i = LoginActivity.newIntent(getActivity());
                    startActivity(i);
                }
            }
        });
        mCommentManageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!needLogin()) {
                    Intent i = CommentLabActivity.newIntent(getActivity());
                    startActivity(i);
                } else {
                    Intent i = LoginActivity.newIntent(getActivity());
                    startActivity(i);
                }
            }
        });
        mVideoManageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!needLogin()) {
                    Intent i = VideoLabActivity.newIntent(getActivity());
                    startActivity(i);
                } else {
                    Intent i = LoginActivity.newIntent(getActivity());
                    startActivity(i);
                }
            }
        });
        mNotificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.showToast(getActivity(), "该功能未上线");
            }
        });
        mSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = SettingsActivity.newIntent(getActivity());
                startActivity(i);
            }
        });
        mStatusSwitch.setChecked(false);
        mStatusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    new StatusRequestTask(REQUEST_SWITCH_ONLINE).execute();
                    mStatusSwitch.setText("在线");
                    mStatusSwitch.setChecked(true);
                } else {
                    new StatusRequestTask(REQUEST_SWITCH_AWAY).execute();
                    mStatusSwitch.setText("隐身");
                    mStatusSwitch.setChecked(false);
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(needLogin()) {
            mNameText.setText("请登录");
            mStatusSwitch.setVisibility(View.GONE);
        } else if (null != mProfileHelper) {
            mProfileHelper.getMyProfile();
            updateStaffInfo(ILiveLoginManager.getInstance().getMyUserId(),
                    ILiveLoginManager.getInstance().getMyUserId(), null);
            mStatusSwitch.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 判断是否需要登录
     *
     * @return true 代表需要重新登录
     */
    public boolean needLogin() {
        if (MySelfInfo.getInstance().getId() != null) {
            return false;//有账号不需要登录
        } else {
            return true;//需要登录
        }
    }

    public void updateStaffInfo(String id, String name, String url) {
        if(null != name && !TextUtils.isEmpty(name))
            mNameText.setText(name);
        if (!TextUtils.isEmpty(url)) {
            Log.d(TAG, "profile avatar: " + url);
            Glide.with(getActivity()).load(url)
                    .apply(RequestOptions.circleCropTransform()).into(mAvatarImage);
        }

    }

    @Override
    public void updateStaffProfile(StaffProfile profile) {
        if (null == getContext()){
            return;
        }
        if (TextUtils.isEmpty(profile.getNickname())) {
            MySelfInfo.getInstance().setNickName(profile.getPhone());
        } else {
            MySelfInfo.getInstance().setNickName(profile.getNickname());
        }
        if (!TextUtils.isEmpty(profile.getAvatarUrl())) {
            MySelfInfo.getInstance().setAvatar(profile.getAvatarUrl());
        }
        MySelfInfo.getInstance().writeToCache(getContext());
        updateStaffInfo(ILiveLoginManager.getInstance().getMyUserId(), MySelfInfo.getInstance().getNickName(),
                MySelfInfo.getInstance().getAvatar());
    }

    private class StatusRequestTask extends AsyncTask<Void, Void, Boolean> {
        private int mRequestId;

        StatusRequestTask(Integer requestId) {
            mRequestId = requestId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            switch (mRequestId) {
                case REQUEST_SWITCH_ONLINE:
                    return StaffServerHelper.switchOnline(MySelfInfo.getInstance().getId(), Constants.CALL_ONLINE);
                case REQUEST_SWITCH_AWAY:
                    return StaffServerHelper.switchAway(MySelfInfo.getInstance().getId(), Constants.CALL_AWAY);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            switch (mRequestId) {
                case REQUEST_SWITCH_ONLINE:
                    if (success) {
                        DialogUtil.showToast(getActivity(), "上线");
                    } else {
                        DialogUtil.showToast(getActivity(), "请重试");
                    }
                    break;
                case REQUEST_SWITCH_AWAY:
                    if (success) {
                        DialogUtil.showToast(getActivity(), "隐身");
                    } else {
                        DialogUtil.showToast(getActivity(), "请重试");
                    }
                    break;
            }
        }
    }
}
