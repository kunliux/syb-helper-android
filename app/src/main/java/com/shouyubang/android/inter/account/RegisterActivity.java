package com.shouyubang.android.inter.account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shouyubang.android.inter.MainActivity;
import com.shouyubang.android.inter.R;
import com.shouyubang.android.inter.presenters.AccountHelper;
import com.shouyubang.android.inter.presenters.view.LoginView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity implements LoginView {

    private static final String TAG = "RegisterActivity";

    @BindView(R.id.input_mobile)
    EditText mMobileText;
    @BindView(R.id.input_password)
    EditText mPasswordText;
    @BindView(R.id.btn_signup)
    Button mRegisterButton;
    @BindView(R.id.link_login)
    TextView mLoginLink;

    private ProgressDialog progressDialog;
    AccountHelper mLoginHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        mLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void register() {
        Log.d(TAG, "Register");

        if (!validate()) {
            onRegisterFailed();
            return;
        }

        mRegisterButton.setEnabled(false);

        progressDialog = new ProgressDialog(RegisterActivity.this, R.style.AppTheme_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("创建账号中...");
        progressDialog.show();

        String phone = mMobileText.getText().toString();
        String password = mPasswordText.getText().toString();

        //注册一个账号
        mLoginHelper.standardRegister(phone, password);
    }


    public void onRegisterSuccess() {
        mRegisterButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onRegisterFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        mRegisterButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String mobile = mMobileText.getText().toString();
        String password = mPasswordText.getText().toString();

        if (mobile.isEmpty() || !isPhoneValid(mobile)) {
            mMobileText.setError(getString(R.string.error_invalid_phone));
            valid = false;
        } else {
            mMobileText.setError(null);
        }

        if (password.isEmpty() || password.length() < 5 || password.length() > 16) {
            mPasswordText.setError(getString(R.string.error_invalid_password));
            valid = false;
        } else {
            mPasswordText.setError(null);
        }

        return valid;
    }

    private boolean isUsernameValid(String username) {
        //正则表达式判断由数字、26个英文字母或者下划线组成的字符串
        return username.matches("^\\w+$");
    }

    private boolean isPhoneValid(String phone_num) {
        //至少包含6个字符
        return phone_num.matches("^(13[0-9]|14[0-9]|15[0-9]|18[0-9])\\d{8}$");
    }

    @Override
    public void loginSuccess() {
        progressDialog.dismiss();
        jumpIntoMainActivity();
    }

    @Override
    public void loginFail(String module, int errCode, String errMsg) {
        progressDialog.dismiss();
        Toast.makeText(this, "code "+errCode+"     "+errMsg , Toast.LENGTH_SHORT).show();

    }

    /**
     * 直接跳转主界面
     */
    private void jumpIntoMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}