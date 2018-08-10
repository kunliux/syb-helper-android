package com.shouyubang.android.inter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.shouyubang.android.inter.R;
import com.shouyubang.android.inter.model.Comment;
import com.shouyubang.android.inter.utils.TimeUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder>{

    private static final String TAG = "CommentAdapter";

    private Context mContext;
    private List<Comment> mComments;

    public CommentAdapter(Context packageContext, List<Comment> Comments) {
        mContext = packageContext;
        mComments = Comments;
    }

    class CommentHolder extends RecyclerView.ViewHolder{

        private Comment mComment;
        private TextView mUserName, mCommentTime, mCommentContent;
        private RatingBar mCommentRating;

        CommentHolder(View itemView) {
            super(itemView);
            mUserName = (TextView) itemView.findViewById(R.id.user_name);
            mCommentRating = (RatingBar) itemView.findViewById(R.id.comment_rating);
            mCommentTime = (TextView) itemView.findViewById(R.id.comment_time);
            mCommentContent = (TextView) itemView.findViewById(R.id.comment_content);
        }
        void bindAnswer(Comment Comment) {
            mComment = Comment;
            if(mComment.getIsAnonymous() !=1) {
                mUserName.setText(encryptPhoneNumber(mComment.getUserId()));
            } else {
                mUserName.setText("匿名用户");
            }
            mCommentRating.setRating((float) (mComment.getRating() / 2.0));
            Date currentTime = convertToDate(mComment.getTime());
            mCommentTime.setText(TimeUtil.getTimeShowString(currentTime, true));
            mCommentContent.setText(mComment.getContent());
        }
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.list_comment_item, parent, false);
        return new CommentHolder(v);
    }

    @Override
    public void onBindViewHolder(CommentHolder holder, int position) {
        Comment Comment = mComments.get(position);
        holder.bindAnswer(Comment);
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public void setComments(List<Comment> Comments) {
        mComments = Comments;
    }

    private Date convertToDate(String str) {
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        Date date = new Date();
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            Log.e(TAG, "Datetime Parse Error", e);
        }
        return date;
    }

    private String encryptPhoneNumber(String phone) {
        return phone.substring(0,6)+"****"+phone.substring(10,phone.length());
    }


}

