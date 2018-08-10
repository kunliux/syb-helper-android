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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shouyubang.android.inter.R;
import com.shouyubang.android.inter.media.PlayerActivity;
import com.shouyubang.android.inter.model.Video;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by KunLiu on 2016/5/27.
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoHolder> {

    private static final String TAG = "VideoAdapter";

    private Context mContext;

    private List<Video> mVideos;

    public VideoAdapter(Context packageContext, List<Video> accounts) {
        mContext = packageContext;
        mVideos = accounts;
    }


    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_video_item_archive, parent, false);
        return new VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoHolder holder, int position) {
        Video task = mVideos.get(position);
        holder.bindVideo(task);
    }

    @Override
    public int getItemCount() {
        return mVideos.size();
    }

    public void setVideos(List<Video> videos) {
        mVideos = videos;
    }

    public class VideoHolder extends RecyclerView.ViewHolder {

        private Video mVideo;

        @BindView(R.id.msg_video_title)
        TextView mVideoTitle;
        @BindView(R.id.msg_user_video_btn)
        ImageButton mUserVideoBtn;
        @BindView(R.id.msg_staff_video_btn)
        RelativeLayout mStaffVideoBtn;
        @BindView(R.id.msg_video_cover)
        ImageView mVideoCover;
        @BindView(R.id.msg_video_status)
        TextView mVideoStatus;
        @BindView(R.id.msg_video_datetime)
        TextView mVideoDatetime;

        public VideoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindVideo(Video video) {
            mVideo = video;
            mVideoDatetime.setText(getDatetime(video.getReplyTime()));
            mVideoTitle.setText(video.getTitle());
            String url = video.getCoverUrl();
            if (!TextUtils.isEmpty(url)) {
                Log.d(TAG, "profile avatar: " + url);
                Glide.with(mContext).load(url).into(mVideoCover);
            }
            mUserVideoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = PlayerActivity.newIntent(mContext, mVideo.getTitle(), mVideo.getVideoUrl());
                    mContext.startActivity(i);
                }
            });
            mStaffVideoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = PlayerActivity.newIntent(mContext, "回复视频", mVideo.getReplyUrl());
                    mContext.startActivity(i);
                }
            });
        }

    }

    private String getDatetime(String datetime) {
        int month = Integer.valueOf(datetime.substring(4, 6));
        int day = Integer.valueOf(datetime.substring(6, 8));
        int hour = Integer.valueOf(datetime.substring(8, 10));
        String minute = datetime.substring(10, 12);
        return month + "月" + day + "日 "+hour + ":" + minute;
    }


}
