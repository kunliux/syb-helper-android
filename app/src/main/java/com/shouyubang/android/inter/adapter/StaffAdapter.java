package com.shouyubang.android.inter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shouyubang.android.inter.R;
import com.shouyubang.android.inter.model.StaffProfile;
import com.shouyubang.android.inter.utils.CallUtil;
import com.tencent.callsdk.ILVCallConstants;

import java.util.List;

/**
 * Created by KunLiu on 2016/5/27.
 */
public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.StaffHolder> {

    private static final String TAG = "StaffAdapter";

    private Context mContext;

    private List<StaffProfile> mStaffs;

    public StaffAdapter(Context packageContext, List<StaffProfile> accounts) {
        mContext = packageContext;
        mStaffs = accounts;
    }


    @Override
    public StaffHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_staff_item, parent, false);
        return new StaffHolder(view);
    }

    @Override
    public void onBindViewHolder(StaffHolder holder, int position) {
        StaffProfile staff = mStaffs.get(position);
        holder.bindStaff(staff);
    }

    @Override
    public int getItemCount() {
        return mStaffs.size();
    }

    public void setStaffs(List<StaffProfile> accounts) {
        mStaffs = accounts;
    }

    public class StaffHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private StaffProfile mStaff;

        private TextView mNickName;
        private ImageView mAvatar;
        private TextView mStatus;

        public StaffHolder(View itemView) {
            super(itemView);
            mNickName = (TextView) itemView.findViewById(R.id.item_nickname);
            mAvatar = (ImageView) itemView.findViewById(R.id.item_avatar);
            mStatus = (TextView) itemView.findViewById(R.id.item_status);
            itemView.setOnClickListener(this);
        }

        public void bindStaff(StaffProfile staff) {
            mStaff = staff;
            mNickName.setText(mStaff.getNickname());
//            mAvatar.setImageResource(R.drawable.ic_default_user);
//            Log.i(TAG, mStaff.toString());
        }

        @Override
        public void onClick(View v) {
            CallUtil.makeCall(mContext, ILVCallConstants.CALL_TYPE_VIDEO, mStaff.getPhone());

        }


    }
}
