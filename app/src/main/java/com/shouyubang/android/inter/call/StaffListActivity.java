package com.shouyubang.android.inter.call;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.shouyubang.android.inter.R;
import com.shouyubang.android.inter.adapter.StaffAdapter;
import com.shouyubang.android.inter.model.StaffProfile;

import java.util.ArrayList;
import java.util.List;

public class StaffListActivity extends AppCompatActivity {

    private static final String TAG = "StaffListActivity";

    private RecyclerView mStaffRecyclerView;
    private List<StaffProfile> mStaffs = new ArrayList<>();
    private StaffAdapter mAdapter;

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, StaffListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_list);
        mStaffRecyclerView = (RecyclerView) findViewById(R.id.staff_recycler_view);
        mStaffRecyclerView.setLayoutManager(new LinearLayoutManager(StaffListActivity.this));

        mStaffRecyclerView.addItemDecoration(new DividerItemDecoration(StaffListActivity.this, DividerItemDecoration.VERTICAL));
        StaffProfile s = new StaffProfile();
        s.setNickname("手语帮团队");
        s.setPhone("86-18975565206");
        mStaffs.add(s);
        setupAdapter();
    }

    private void setupAdapter() {

        if (mAdapter == null) {
            mAdapter = new StaffAdapter(StaffListActivity.this, mStaffs);
            mStaffRecyclerView.setAdapter(mAdapter);
        } else {
            Log.d(TAG, "Size:" + mStaffs.size());
            mAdapter.setStaffs(mStaffs);
            mAdapter.notifyDataSetChanged();
        }
    }


}
