package com.shouyubang.android.inter.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.shouyubang.android.inter.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by KunLiu on 2017/3/21.
 */

public class FeedbackActivity extends AppCompatActivity {

    @BindView(R.id.feedback_contact)
    EditText mFeedbackContact;
    @BindView(R.id.feedback_content)
    EditText mFeedbackContent;
    @BindView(R.id.feedback_submit_btn)
    Button mFeedbackSubmitBtn;

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, FeedbackActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
        mFeedbackSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
