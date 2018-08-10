package com.shouyubang.android.inter.record;

import android.os.AsyncTask;
import android.util.Log;

import com.shouyubang.android.inter.model.MySelfInfo;
import com.shouyubang.android.inter.model.RequestBackInfo;
import com.shouyubang.android.inter.model.Video;
import com.shouyubang.android.inter.presenters.AccountHelper;
import com.shouyubang.android.inter.presenters.StaffServerHelper;
import com.shouyubang.android.inter.utils.TimeUtil;

/**
 * Created by KunLiu on 2017/7/26.
 */

public class TaskInfoTask extends AsyncTask<Void, Void, RequestBackInfo> {
    private static final String TAG = "TaskInfoTask";

    private Video mVideo;
    private AccountHelper mUploadHelper;

    TaskInfoTask(AccountHelper uploadHelper, String path, Integer videoId) {
        mUploadHelper = uploadHelper;
        String staffId = MySelfInfo.getInstance().getId();
        String fileName = getFileName(path)+".mp4";
        mVideo = new Video(staffId, fileName);
        mVideo.setId(videoId);
        mVideo.setReplyTime(TimeUtil.getNowTime());
    }

    @Override
    protected RequestBackInfo doInBackground(Void... params) {
        Log.i(TAG, "User submit video info");
        return StaffServerHelper.getInstance().replyVideo(mVideo);
    }

    @Override
    protected void onPostExecute(RequestBackInfo result) {
        if (result.getErrorCode() >= 0) {
            mUploadHelper.mUploadView.uploadSuccess();
        } else {
            mUploadHelper.mUploadView.uploadFail(TAG, result.getErrorCode(), result.getErrorInfo());
        }
    }

    public String getFileName(String path){

        int start=path.lastIndexOf("/");
        int end=path.lastIndexOf(".");
        if(start!=-1 && end!=-1){
            return path.substring(start+1,end);
        }else{
            return null;
        }

    }

}
