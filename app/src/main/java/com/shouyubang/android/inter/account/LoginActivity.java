package com.shouyubang.android.inter.account;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.shouyubang.android.inter.R;
import com.shouyubang.android.inter.model.MySelfInfo;
import com.shouyubang.android.inter.presenters.AccountHelper;
import com.shouyubang.android.inter.presenters.view.LoginView;


/**
 * Android login screen Activity
 */
public class LoginActivity extends AppCompatActivity implements LoginView {

    private static final String TAG = "LoginActivity";

    private View loginFormView;
    private View progressView;
    private EditText phoneTextView;
    private EditText passwordTextView;
    private TextView signUpTextView;
    private AccountHelper mLoginHelper;

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginHelper = new AccountHelper(this, this);
        //获取个人数据本地缓存
        MySelfInfo.getInstance().getCache(getApplicationContext());

        if (needLogin()) {//本地没有账户需要登录
            initView(true);
        } else {
            //有账户登录直接IM登录
            Log.i(TAG, "LoginActivity onCreate");
            initView(false);
            showProgress(true);
            mLoginHelper.iLiveLogin(MySelfInfo.getInstance().getId(), MySelfInfo.getInstance().getUserSig());
        }
    }

    private void initView(boolean needLogin) {
        setContentView(R.layout.activity_login);

        phoneTextView = (EditText) findViewById(R.id.input_phone);
        passwordTextView = (EditText) findViewById(R.id.input_password);
        passwordTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    initLogin();
                    return true;
                }
                return false;
            }
        });

        ImageButton exitButton = (ImageButton) findViewById(R.id.btn_login_exit);
        exitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.this.finish();
            }
        });

        Button loginButton = (Button) findViewById(R.id.btn_login);
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                initLogin();
            }
        });

        loginFormView = findViewById(R.id.login_form);
        loginFormView.setVisibility(needLogin? View.VISIBLE : View.GONE);
        progressView = findViewById(R.id.login_progress);

        //adding underline and link to signup textview
        signUpTextView = (TextView) findViewById(R.id.signUpTextView);
        signUpTextView.setPaintFlags(signUpTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        Linkify.addLinks(signUpTextView, Linkify.ALL);

        signUpTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("LoginActivity", "Sign Up Activity activated.");
                // this is where you should start the signup Activity
                // LoginActivity.this.startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });
    }

    /**
     * Validate Login form and authenticate.
     */
    public void initLogin() {

        phoneTextView.setError(null);
        passwordTextView.setError(null);

        String phone = phoneTextView.getText().toString();
        String password = passwordTextView.getText().toString();

        boolean cancelLogin = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordTextView.setError(getString(R.string.invalid_password));
            focusView = passwordTextView;
            cancelLogin = true;
        }

        if (TextUtils.isEmpty(phone)) {
            phoneTextView.setError(getString(R.string.field_required));
            focusView = phoneTextView;
            cancelLogin = true;
        } else if (!isPhoneValid(phone)) {
            phoneTextView.setError(getString(R.string.invalid_phone));
            focusView = phoneTextView;
            cancelLogin = true;
        }

        if (cancelLogin) {
            // error in login
            focusView.requestFocus();
        } else {
            // show progress spinner, and start background task to login
            showProgress(true);
            mLoginHelper.standardLogin("86-"+phone, password);
        }
    }

    private boolean isPhoneValid(String phone) {
        //add your own logic
        return phone.length() == 11;
    }

    private boolean isPasswordValid(String password) {
        //add your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        loginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });

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


    /**
     * 直接跳转主界面
     */
    private void jumpIntoMainActivity() {
//        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//        startActivity(intent);
        finish();
    }

    @Override
    public void loginSuccess() {
        Toast.makeText(LoginActivity.this, "" + MySelfInfo.getInstance().getId() + " 登录 ", Toast.LENGTH_SHORT).show();
        jumpIntoMainActivity();
    }

    @Override
    public void loginFail(String module, int errCode, String errMsg) {
        Log.i(TAG, "Login FAIL " + errCode + " : " + errMsg);
        Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
        showProgress(false);
        initView(true);
    }
}
