package com.shouyubang.android.inter.account;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.shouyubang.android.inter.BuildConfig;
import com.shouyubang.android.inter.R;

public class AboutActivity extends AppCompatActivity {
	
	private TextView mVersionView;

    public static Intent newIntent(Context packageContext) {
        Intent i = new Intent(packageContext, AboutActivity.class);
        return i;
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		findViews();
		initViewData();
	}

    private void findViews() {
		mVersionView = (TextView) findViewById(R.id.version_detail);
	}

	private void initViewData() {
        mVersionView.setText("手语帮服务版 " + BuildConfig.VERSION_NAME +" 测试版");
	}
}
