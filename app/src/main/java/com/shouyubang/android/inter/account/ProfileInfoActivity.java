package com.shouyubang.android.inter.account;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.shouyubang.android.inter.R;
import com.shouyubang.android.inter.model.MySelfInfo;
import com.shouyubang.android.inter.model.StaffProfile;
import com.shouyubang.android.inter.presenters.ProfileInfoHelper;
import com.shouyubang.android.inter.presenters.UploadHelper;
import com.shouyubang.android.inter.presenters.view.ProfileView;
import com.shouyubang.android.inter.presenters.view.UploadImageView;
import com.shouyubang.android.inter.utils.Constants;
import com.shouyubang.android.inter.utils.UIUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileInfoActivity extends AppCompatActivity implements ProfileView, UploadImageView {

    private static final String TAG = "ProfileInfoActivity";

    private static final int CROP_CHOOSE = 10;
    private static final int CAPTURE_IMAGE_CAMERA = 100;
    private static final int IMAGE_STORE = 200;

    @BindView(R.id.profile_avatar)
    ImageView mProfileAvatar;
    @BindView(R.id.profile_name)
    EditText mProfileName;
    @BindView(R.id.profile_gender)
    EditText mProfileGender;
    @BindView(R.id.profile_age)
    EditText mProfileAge;
    @BindView(R.id.profile_industry)
    EditText mProfileIndustry;
    @BindView(R.id.profile_company)
    EditText mProfileCompany;
    @BindView(R.id.profile_profession)
    EditText mProfileProfession;

    private UploadHelper mUploadHelper;
    private ProfileInfoHelper mProfileHelper;

    private StaffProfile mProfile;

    private boolean bPermission = false;

    private Uri iconUrl, iconCrop;

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, ProfileInfoActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);
        ButterKnife.bind(this);

        mProfile = new StaffProfile();

        mProfileAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotoDialog();
            }
        });

        mProfileHelper = new ProfileInfoHelper(this);
        mUploadHelper = new UploadHelper(this, this);

        bPermission = checkCropPermission();

        updateView();
    }

    private void updateView(){
        mProfileName.setText(MySelfInfo.getInstance().getNickName());
        if (!TextUtils.isEmpty(MySelfInfo.getInstance().getAvatar())){
            Log.d(TAG, "profile avatar: " + MySelfInfo.getInstance().getAvatar());
            Glide.with(ProfileInfoActivity.this).load(MySelfInfo.getInstance().getAvatar())
                    .apply(RequestOptions.circleCropTransform()).into(mProfileAvatar);
        }

        int gender = mProfile.getGender();
        String age = mProfile.getAge();
        String industry = mProfile.getIndustry();
        String company = mProfile.getCompany();
        String profession = mProfile.getProfession();

        if(!TextUtils.isEmpty(age)) {
            mProfileAge.setText(age);
        }
        if(!TextUtils.isEmpty(industry)) {
            mProfileIndustry.setText(industry);
        }
        if(!TextUtils.isEmpty(company)) {
            mProfileCompany.setText(company);
        }
        if(!TextUtils.isEmpty(profession)) {
            mProfileProfession.setText(profession);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != mProfileHelper) {
            mProfileHelper.getMyProfile();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUploadHelper.onDestroy();
    }

    private Uri createAvatarUri(String type, boolean bCrop) {
        String filename = MySelfInfo.getInstance().getId()+ type + ".jpg";
        File outputImage = new File(Environment.getExternalStorageDirectory(), filename);
        if (ContextCompat.checkSelfPermission(ProfileInfoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ProfileInfoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.WRITE_PERMISSION_REQ_CODE);
            return null;
        }
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bCrop) {
            return Uri.fromFile(outputImage);
        }else {
            return UIUtils.getUriFromFile(this, outputImage);
        }
    }

    private boolean checkCropPermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(ProfileInfoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(ProfileInfoActivity.this, Manifest.permission.READ_PHONE_STATE)){
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (permissions.size() != 0){
                ActivityCompat.requestPermissions(ProfileInfoActivity.this,
                        permissions.toArray(new String[0]),
                        Constants.WRITE_PERMISSION_REQ_CODE);
                return false;
            }
        }

        return true;
    }


    /**
     * 获取图片资源
     *
     * @param type
     */
    private void getPicFrom(int type) {
        if (!bPermission){
            Toast.makeText(this, getString(R.string.tip_no_permission), Toast.LENGTH_SHORT).show();
            return;
        }
        switch (type) {
            case CAPTURE_IMAGE_CAMERA:
                Intent intent_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                iconUrl = createAvatarUri("_icon", false);
                intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, iconUrl);
                startActivityForResult(intent_photo, CAPTURE_IMAGE_CAMERA);
                break;
            case IMAGE_STORE:
                iconUrl = createAvatarUri("_select_icon", false);
                Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
                intent_album.setType("image/*");
                startActivityForResult(intent_album, IMAGE_STORE);
                break;

        }
    }

    /**
     * 图片选择对话框
     */
    private void showPhotoDialog() {
        final Dialog pickDialog = new Dialog(this, R.style.float_dialog);
        pickDialog.setContentView(R.layout.pic_choose);

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Window dlgwin = pickDialog.getWindow();
        WindowManager.LayoutParams lp = dlgwin.getAttributes();
        dlgwin.setGravity(Gravity.BOTTOM);
        lp.width = (int)(display.getWidth()); //设置宽度

        pickDialog.getWindow().setAttributes(lp);

        TextView camera = (TextView) pickDialog.findViewById(R.id.chos_camera);
        TextView picLib = (TextView) pickDialog.findViewById(R.id.pic_lib);
        TextView cancel = (TextView) pickDialog.findViewById(R.id.btn_cancel);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPicFrom(CAPTURE_IMAGE_CAMERA);
                pickDialog.dismiss();
            }
        });

        picLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPicFrom(IMAGE_STORE);
                pickDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDialog.dismiss();
            }
        });

        pickDialog.show();
    }

    public void startPhotoZoom(Uri uri) {
        iconCrop = createAvatarUri("_icon_crop", true);

        revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            this.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 300);
        intent.putExtra("aspectY", 300);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, iconCrop);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, CROP_CHOOSE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_profile_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save_changes:
                setStaffInfo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void setStaffInfo() {
        String nickname = mProfileName.getText().toString().trim();
        String gender = mProfileGender.getText().toString().trim();
        String age = mProfileAge.getText().toString().trim();
        String industry = mProfileIndustry.getText().toString().trim();
        String company = mProfileCompany.getText().toString().trim();
        String profession = mProfileProfession.getText().toString().trim();

        if(!TextUtils.isEmpty(nickname)) {
            mProfile.setNickname(nickname);
        }
//        if(!TextUtils.isEmpty(gender)) {
//            mProfile.setGender(gender);
//        }
        if(!TextUtils.isEmpty(age)) {
            mProfile.setAge(age);
        }
        if(!TextUtils.isEmpty(industry)) {
            mProfile.setIndustry(industry);
        }
        if(!TextUtils.isEmpty(company)) {
            mProfile.setCompany(company);
        }
        if(!TextUtils.isEmpty(profession)) {
            mProfile.setProfession(profession);
        }

        mProfileHelper.setMyProfile(mProfile);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK){
            Log.e(TAG, "onActivityResult->failed for request: " + requestCode + "/" + resultCode);
            return;
        }
        switch (requestCode){
            case CAPTURE_IMAGE_CAMERA:
                startPhotoZoom(iconUrl);
                break;
            case IMAGE_STORE:
                String path = UIUtils.getPath(this, data.getData());
                if (null != path){
                    Log.e(TAG, "startPhotoZoom->path:" + path);
                    File file = new File(path);
                    startPhotoZoom(UIUtils.getUriFromFile(this, file));
                }
                break;
            case CROP_CHOOSE:
                mUploadHelper.uploadAvatar(iconCrop.getPath());
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case Constants.WRITE_PERMISSION_REQ_CODE:
                for (int ret : grantResults){
                    if (ret != PackageManager.PERMISSION_GRANTED){
                        return;
                    }
                }
                bPermission = true;
                break;
            default:
                break;
        }
    }

    @Override
    public void updateStaffProfile(StaffProfile profile) {
        mProfile = profile;
        if (TextUtils.isEmpty(profile.getNickname())) {
            MySelfInfo.getInstance().setNickName(profile.getPhone());
        } else {
            MySelfInfo.getInstance().setNickName(profile.getNickname());
        }
        if (!TextUtils.isEmpty(profile.getAvatarUrl())) {
            MySelfInfo.getInstance().setAvatar(profile.getAvatarUrl());
        }
        updateView();
    }

    @Override
    public void onUploadProcess(int percent) {}

    @Override
    public void onUploadResult(int code, String url) {
        if (0 == code) {
            mProfileHelper.setMyAvatar(url);
        }else{
            Log.w(TAG, "onUploadResult->failed: "+code);
        }
    }
}
