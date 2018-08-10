package com.shouyubang.android.inter.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.shouyubang.android.inter.MainActivity;
import com.shouyubang.android.inter.R;
import com.shouyubang.android.inter.model.MySelfInfo;
import com.shouyubang.android.inter.presenters.AccountHelper;
import com.shouyubang.android.inter.presenters.view.LogoutView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity implements LogoutView {

    @BindView(R.id.feedback_btn)
    TextView mFeedbackBtn;
    @BindView(R.id.about_btn)
    TextView mAboutUsBtn;
    @BindView(R.id.logout_btn)
    TextView mLogoutBtn;

    private AccountHelper mLoginHelper;

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, SettingsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mLoginHelper = new AccountHelper(getApplicationContext(), this);
        ButterKnife.bind(this);
        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mLoginHelper)
                    mLoginHelper.standardLogout(MySelfInfo.getInstance().getId());
            }
        });
        mFeedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = FeedbackActivity.newIntent(SettingsActivity.this);
                startActivity(i);
            }
        });
        mAboutUsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = AboutActivity.newIntent(SettingsActivity.this);
                startActivity(i);
            }
        });
    }

    /**
     * 直接跳转主界面
     */
    private void jumpIntoMainActivity() {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void logoutSuccess() {
        SharedPreferences.Editor editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        editor.putBoolean("living", false);
        editor.apply();
        Toast.makeText(SettingsActivity.this, "退出当前账号", Toast.LENGTH_SHORT).show();
        jumpIntoMainActivity();
    }

    @Override
    public void logoutFail() {

    }
}
