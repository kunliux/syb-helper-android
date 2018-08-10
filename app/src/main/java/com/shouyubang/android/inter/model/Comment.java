package com.shouyubang.android.inter.model;

/**
 * Created by KunLiu on 2017/5/31.
 */

public class Comment {

    private String id;

    private String staffId;
    private String userId;
    private String callId;
    private String orderId;
    private String time;
    private int rating; //评分，整型，取值1-5
    private int reward; //打赏
    private int isAnonymous;
    private String content;

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getIsAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(int isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", staffId='" + staffId + '\'' +
                ", userId='" + userId + '\'' +
                ", callId='" + callId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", time='" + time + '\'' +
                ", rating=" + rating +
                ", isAnonymous=" + isAnonymous +
                ", content='" + content + '\'' +
                '}';
    }
}
