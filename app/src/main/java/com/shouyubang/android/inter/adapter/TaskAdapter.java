package com.shouyubang.android.inter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shouyubang.android.inter.R;
import com.shouyubang.android.inter.account.LoginActivity;
import com.shouyubang.android.inter.media.DetailPlayerActivity;
import com.shouyubang.android.inter.model.MySelfInfo;
import com.shouyubang.android.inter.model.Video;

import java.util.List;

/**
 * Created by KunLiu on 2016/5/27.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskHolder> {

    private static final String TAG = "TaskAdapter";

    private Context mContext;

    private List<Video> mVideos;

    public TaskAdapter(Context packageContext, List<Video> accounts) {
        mContext = packageContext;
        mVideos = accounts;
    }


    @Override
    public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_video_item, parent, false);
        return new TaskHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskHolder holder, int position) {
        Video video = mVideos.get(position);
        holder.bindTask(video);
    }

    @Override
    public int getItemCount() {
        return mVideos.size();
    }

    public void setTasks(List<Video> videos) {
        mVideos = videos;
    }

    class TaskHolder extends RecyclerView.ViewHolder {

        private Video mVideo;

        TextView mVideoTitle;
        ImageView mVideoCover;
        ImageButton mPlayButton;

        public TaskHolder(View itemView) {
            super(itemView);
            mVideoTitle = (TextView) itemView.findViewById(R.id.video_title);
            mVideoCover = (ImageView) itemView.findViewById(R.id.video_cover);
            mPlayButton = (ImageButton) itemView.findViewById(R.id.list_item_btn);
        }

        void bindTask(Video video) {
            mVideo = video;
            mVideoTitle.setText(video.getTitle());
            String url = video.getCoverUrl();
            if (!TextUtils.isEmpty(url)) {
                Log.d(TAG, "profile avatar: " + url);
                Glide.with(mContext).load(url).into(mVideoCover);
            }
            mPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!needLogin()) {
                        Intent i = DetailPlayerActivity.newIntent(mContext, mVideo.getTitle(), mVideo.getVideoUrl(), mVideo.getId());
                        mContext.startActivity(i);
                    } else {
                        Intent i = LoginActivity.newIntent(mContext);
                        mContext.startActivity(i);
                    }
                }
            });
        }
    }

    /**
     * 判断是否需要登录
     *
     * @return true 代表需要重新登录
     */
    private boolean needLogin() {
        if (MySelfInfo.getInstance().getId() != null) {
            return false;//有账号不需要登录
        } else {
            return true;//需要登录
        }

    }


}
