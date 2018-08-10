package com.shouyubang.android.inter.presenters;

import android.os.AsyncTask;
import android.util.Log;

import com.shouyubang.android.inter.App;
import com.shouyubang.android.inter.model.MySelfInfo;
import com.shouyubang.android.inter.model.StaffProfile;
import com.shouyubang.android.inter.presenters.view.ProfileView;
import com.shouyubang.android.inter.utils.DialogUtil;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendshipManager;

/**
 * 用户资料获取
 */
public class ProfileInfoHelper {
    private String TAG = getClass().getName();
    private ProfileView mView;

    public ProfileInfoHelper(ProfileView view){
        mView = view;
    }

    public void getMyProfile(){
        new ProfileInfoTask().execute();
    }

    public void setMyProfile(StaffProfile profile){

        new UpdateInfoTask(profile).execute();

        TIMFriendshipManager.getInstance().setNickName(profile.getNickname(), new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Log.w(TAG, "setNickName->error:" + i + "," + s);
            }

            @Override
            public void onSuccess() {
                getMyProfile();
            }
        });
    }

    public void setMyAvatar(String url){

        new UpdateAvatarTask(url).execute();

        TIMFriendshipManager.getInstance().setFaceUrl(url, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Log.w(TAG, "setMyAvatar->error:" + i + "," + s);
            }

            @Override
            public void onSuccess() {
                getMyProfile();
            }
        });
    }

    private class ProfileInfoTask extends AsyncTask<Void, Void, StaffProfile> {

        @Override
        protected StaffProfile doInBackground(Void... params) {
            return StaffServerHelper.getProfile(MySelfInfo.getInstance().getId());
        }

        @Override
        protected void onPostExecute(StaffProfile profile) {
            if(null != profile) {
                Log.i(TAG, "Update staff info: " + profile.toString());
                mView.updateStaffProfile(profile);
            }
        }
    }

    private class UpdateInfoTask extends AsyncTask<Void, Void, Boolean> {

        private StaffProfile mStaffProfile;

        UpdateInfoTask(StaffProfile staffProfile) {
            mStaffProfile = staffProfile;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return StaffServerHelper.updateProfile(mStaffProfile);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(success) {
                DialogUtil.showToast(App.getContext(), "修改成功");
            } else {
                DialogUtil.showToast(App.getContext(), "修改失败");
            }
        }
    }

    private class UpdateAvatarTask extends AsyncTask<Void, Void, Boolean> {

        private String mAvatarUrl;

        UpdateAvatarTask(String avatarUrl) {
            mAvatarUrl = avatarUrl;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return StaffServerHelper.updateAvatar(MySelfInfo.getInstance().getId(), mAvatarUrl);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(success) {
                DialogUtil.showToast(App.getContext(), "修改成功");
            } else {
                DialogUtil.showToast(App.getContext(), "修改失败");
            }
        }
    }
}
