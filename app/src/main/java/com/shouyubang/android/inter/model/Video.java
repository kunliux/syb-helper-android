package com.shouyubang.android.inter.model;

/**
 * Created by KunLiu on 2017/7/26.
 */

public class Video {

    private int id;
    private String coverUrl;
    private String title;
    private String userId;
    private String staffId;
    private String videoUrl;
    private String uploadTime;
    private String replyUrl;
    private String replyTime;
    private int status;
    private String orderId;
    private int reward;

    public Video(String staffId, String replyUrl) {
        this.staffId = staffId;
        this.replyUrl = replyUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getReplyUrl() {
        return replyUrl;
    }

    public void setReplyUrl(String replyUrl) {
        this.replyUrl = replyUrl;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    @Override
    public String toString() {
        return "VideoMsg{" +
                "id=" + id +
                ", coverUrl='" + coverUrl + '\'' +
                ", title='" + title + '\'' +
                ", userId='" + userId + '\'' +
                ", staffId='" + staffId + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", uploadTime='" + uploadTime + '\'' +
                ", replyUrl='" + replyUrl + '\'' +
                ", replyTime='" + replyTime + '\'' +
                ", status=" + status +
                ", orderId='" + orderId + '\'' +
                ", reward=" + reward +
                '}';
    }
}
