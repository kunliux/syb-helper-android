package com.shouyubang.android.inter.record;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.shouyubang.android.inter.R;
import com.shouyubang.android.inter.adapter.TaskAdapter;
import com.shouyubang.android.inter.manage.VideoLabActivity;
import com.shouyubang.android.inter.model.Video;
import com.shouyubang.android.inter.presenters.StaffServerHelper;

import java.util.ArrayList;
import java.util.List;

public class VideoListActivity extends AppCompatActivity {

    private static final String TAG = "VideoListActivity";

    private RecyclerView mTaskRecyclerView;
    private List<Video> mTasks = new ArrayList<>();
    private TaskAdapter mAdapter;

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, VideoListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        mTaskRecyclerView = (RecyclerView) findViewById(R.id.task_recycler_view);
        mTaskRecyclerView.setLayoutManager(new GridLayoutManager(VideoListActivity.this, 2));

        new TaskListTask().execute();
    }

    private void setupAdapter() {

        if (mAdapter == null) {
            mAdapter = new TaskAdapter(VideoListActivity.this, mTasks);
            mTaskRecyclerView.setAdapter(mAdapter);
        } else {
            Log.d(TAG, "Size:" + mTasks.size());
            mAdapter.setTasks(mTasks);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class TaskListTask extends AsyncTask<Void, Void, List<Video>> {
        private static final String TAG = "TaskInfoTask";

        @Override
        protected List<Video> doInBackground(Void... params) {
            Log.i(TAG, "StaffProfile get task list.");
            return StaffServerHelper.getToDoTasks();
        }

        @Override
        protected void onPostExecute(List<Video> tasks) {
            if(tasks.size() == 0) {
                return;
            }
            mTasks = tasks;
            setupAdapter();
        }
    }

    /**
     *操作栏
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_task_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_archive:
                Intent i = VideoLabActivity.newIntent(VideoListActivity.this);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }



}
