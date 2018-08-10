package com.shouyubang.android.inter.manage;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.shouyubang.android.inter.R;
import com.shouyubang.android.inter.adapter.VideoAdapter;
import com.shouyubang.android.inter.model.MySelfInfo;
import com.shouyubang.android.inter.model.Video;
import com.shouyubang.android.inter.presenters.StaffServerHelper;

import java.util.ArrayList;
import java.util.List;

//视频管理（我的视频 + 视频库）
public class VideoLabActivity extends AppCompatActivity {

    private static final String TAG = "VideoLabActivity";

    private RecyclerView mVideoRecyclerView;
    private List<Video> mVideos = new ArrayList<>();
    private VideoAdapter mAdapter;

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, VideoLabActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_lab);
        mVideoRecyclerView = (RecyclerView) findViewById(R.id.video_recycler_view);
        mVideoRecyclerView.setLayoutManager(new LinearLayoutManager(VideoLabActivity.this));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        new VideoListTask(MySelfInfo.getInstance().getId()).execute();
    }

    private void setupAdapter() {

        if (mAdapter == null) {
            mAdapter = new VideoAdapter(VideoLabActivity.this, mVideos);
            mVideoRecyclerView.setAdapter(mAdapter);
        } else {
            Log.d(TAG, "Size:" + mVideos.size());
            mAdapter.setVideos(mVideos);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class VideoListTask extends AsyncTask<Void, Void, List<Video>> {
        private static final String TAG = "VideoInfoVideo";

        String userId;

        VideoListTask(String userId) {
            this.userId = userId;
        }

        @Override
        protected List<Video> doInBackground(Void... params) {
            Log.i(TAG, "StaffProfile get task list.");
            return StaffServerHelper.getVideoList(userId);
        }

        @Override
        protected void onPostExecute(List<Video> videos) {
            if(videos.size() == 0) {
                return;
            }
            mVideos = videos;
            setupAdapter();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
