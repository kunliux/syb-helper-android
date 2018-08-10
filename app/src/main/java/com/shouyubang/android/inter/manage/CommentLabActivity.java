package com.shouyubang.android.inter.manage;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.shouyubang.android.inter.R;
import com.shouyubang.android.inter.adapter.CommentAdapter;
import com.shouyubang.android.inter.model.Comment;
import com.shouyubang.android.inter.model.MySelfInfo;
import com.shouyubang.android.inter.model.Video;
import com.shouyubang.android.inter.presenters.StaffServerHelper;

import java.util.ArrayList;
import java.util.List;

public class CommentLabActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;// mRecyclerView维护少量的View来进行显示大量的数据
    private List<Comment> mComments = new ArrayList<>();
    private CommentAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;//布局管理器，设置每一项view在RecyclerView中的位置布局以及控件item view的显示或者隐藏

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, CommentLabActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_lab);

        mRecyclerView = (RecyclerView) findViewById(R.id.comments_view);
        mRecyclerView.setHasFixedSize(true);//使RecyclerView保持固定的大小
        layoutManager = new LinearLayoutManager(CommentLabActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);

        new CommentListTask(MySelfInfo.getInstance().getId()).execute();
    }
    private void setupAdapter() {
        if (mAdapter == null) {
            mAdapter = new CommentAdapter(CommentLabActivity.this, mComments);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setComments(mComments);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class CommentListTask extends AsyncTask<Void, Void, List<Comment>> {
        private static final String TAG = "VideoInfoVideo";

        String mStaffId;

        public CommentListTask(String staffId) {
            mStaffId = staffId;
        }

        @Override
        protected List<Comment> doInBackground(Void... params) {
            Log.i(TAG, "StaffProfile get task list.");
            return StaffServerHelper.getComments(mStaffId);
        }

        @Override
        protected void onPostExecute(List<Comment> comments) {
            if(comments.size() == 0) {
                return;
            }
            mComments = comments;
            setupAdapter();
        }
    }
}
