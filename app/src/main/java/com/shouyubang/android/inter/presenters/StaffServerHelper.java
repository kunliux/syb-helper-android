package com.shouyubang.android.inter.presenters;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.shouyubang.android.inter.model.Comment;
import com.shouyubang.android.inter.model.MySelfInfo;
import com.shouyubang.android.inter.model.RequestBackInfo;
import com.shouyubang.android.inter.model.StaffProfile;
import com.shouyubang.android.inter.model.Video;
import com.shouyubang.android.inter.utils.Constants;
import com.shouyubang.android.inter.utils.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 网络请求类
 */
public class StaffServerHelper {
    private static final String TAG = "StaffServerHelper";
    private static StaffServerHelper instance = null;

    private static final String SERVER = Constants.STAFF_URL;
    private static final String AUTH = Constants.AUTH_URL;

    private static final String REGISTER        = SERVER + "/register";
    private static final String LOGIN           = SERVER + "/login";
    private static final String CHANGE          = SERVER + "/change";
    private static final String LOGOUT          = SERVER + "/logout";
    private static final String REPLY           = SERVER + "/video/reply";
    private static final String TODO            = SERVER + "/video/todo";
    private static final String VIDEO_LIST      = SERVER + "/video/list";
    private static final String COMMETS         = SERVER + "/call/comments";
    private static final String ACCEPT_CALL     = SERVER + "/call/accept";
    private static final String END_CALL        = SERVER + "/call/end";
    private static final String PROFILE_INFO    = SERVER + "/profile";
    private static final String PROFILE_UPDATE  = SERVER + "/profile/update";
    private static final String PROFILE_AVATAR  = SERVER + "/profile/avatar";
    private static final String COS_SIG = AUTH + "/getSign?bucket=cover&service=image";

    private String token = ""; //后续使用唯一标示
    private String sig = ""; //登录唯一标示


    public static StaffServerHelper getInstance() {
        if (instance == null) {
            instance = new StaffServerHelper();
        }
        return instance;
    }


    /**
     * 注册ID （独立方式）
     */
    public RequestBackInfo registerId(String id, String password) {
        try {
            JSONObject jasonPacket = new JSONObject();
            jasonPacket.put("phone", id);
            jasonPacket.put("password", password);
            String json = jasonPacket.toString();
            String res = HttpUtil.post(REGISTER, json);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();
            int code = response.getInt("code");
            String errorInfo = response.getString("password");

            return new RequestBackInfo(code, errorInfo);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 登录ID （独立方式）
     */
    public RequestBackInfo loginId(String id, String password) {
        try {
            JSONObject jasonPacket = new JSONObject();
            jasonPacket.put("phone", id);
            jasonPacket.put("password", password);
            String json = jasonPacket.toString();
            String res = HttpUtil.post(LOGIN, json);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();

            int code = response.getInt("code");
            String errorInfo = response.getString("message");
            if (code >= 0) {
                String data = response.getString("data");
                sig = data;
//                sig = data.getString("userSig");
//                token = data.getString("token");
                MySelfInfo.getInstance().setId(id);
                MySelfInfo.getInstance().setUserSig(data);
//                MySelfInfo.getInstance().setToken(token);
            }
            return new RequestBackInfo(code, errorInfo);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 登出ID （独立方式）
     */
    public RequestBackInfo logoutId(String id) {
        try {
            JSONObject jasonPacket = new JSONObject();
            jasonPacket.put("phone", id);
//            jasonPacket.put("token", MySelfInfo.getInstance().getToken());
            String json = jasonPacket.toString();
            String res = HttpUtil.post(LOGOUT, json);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();

            int code = response.getInt("code");
            String errorInfo = response.getString("message");
            return new RequestBackInfo(code, errorInfo);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 上线
     */
    public static Boolean switchOnline(String staffId, Integer status){
        try {
            String url = Uri.parse(CHANGE)
                    .buildUpon()
                    .appendQueryParameter("id", staffId)
                    .appendQueryParameter("status", String.valueOf(status))
                    .build().toString();
            String res = HttpUtil.get(url);
            Log.i(TAG, "Response: " + res);
            JSONObject response = new JSONObject(res);

            int code = response.getInt("code");
            String message = response.getString("message");

            if(code >= 0) {
                return true;
            }
        } catch (JSONException | IOException e) {
            Log.e(TAG, "Exception:", e);
        }
        return false;
    }

    /**
     * 离开
     */
    public static Boolean switchAway(String staffId, Integer status){
        try {
            String url = Uri.parse(CHANGE)
                    .buildUpon()
                    .appendQueryParameter("id", staffId)
                    .appendQueryParameter("status", String.valueOf(status))
                    .build().toString();
            String res = HttpUtil.get(url);
            Log.i(TAG, "Response: " + res);
            JSONObject response = new JSONObject(res);

            int code = response.getInt("code");
            String message = response.getString("message");

            if(code >= 0) {
                return true;
            }
        } catch (JSONException | IOException e) {
            Log.e(TAG, "Exception:", e);
        }
        return false;
    }

    /**
     * 回复视频
     */
    public RequestBackInfo replyVideo(Video video) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(video);
            Log.i(TAG, "Request: " + json);
            String res = HttpUtil.post(REPLY, json);
            Log.i(TAG, "Response: " + res);
            JSONObject response = new JSONObject(res);

            int code = response.getInt("code");
            String message = response.getString("message");

            if (code >= 0) {
                return new RequestBackInfo(code, message);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取任务
     */
    public static List<Video> getToDoTasks() {
        List<Video> videos = new ArrayList<>();
        try {
            Gson gson = new Gson();
            String res = HttpUtil.get(TODO);
            JSONObject response = new JSONObject(res);
            String data = response.getString("data");
            Log.i(TAG, "data: "+ data);
            JsonArray array = new JsonParser().parse(data).getAsJsonArray();
            for (JsonElement jsonElement : array) {
                videos.add(gson.fromJson(jsonElement, Video.class));
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return videos;
    }

    /**
     * 获取视频消息列表
     */
    public static List<Video> getVideoList(String userId) {
        List<Video> videos = new ArrayList<>();
        try {
            Gson gson = new Gson();
            String res = HttpUtil.get(VIDEO_LIST +'/'+userId);
            JSONObject response = new JSONObject(res);
            String data = response.getString("data");
            Log.i(TAG, "data: "+ data);
            JsonArray array = new JsonParser().parse(data).getAsJsonArray();
            for (JsonElement jsonElement : array) {
                videos.add(gson.fromJson(jsonElement, Video.class));
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return videos;
    }

    /**
     * 建立连接
     */
    public static Boolean acceptCall(Integer callId, String callerId, String answerId){
        try {
            String url = Uri.parse(ACCEPT_CALL)
                    .buildUpon()
                    .appendQueryParameter("callId", String.valueOf(callId))
                    .appendQueryParameter("caller", callerId)
                    .appendQueryParameter("answer", answerId)
                    .build().toString();
            String res = HttpUtil.get(url);
            Log.i(TAG, "Response: " + res);
            JSONObject response = new JSONObject(res);

            int code = response.getInt("code");
            String message = response.getString("message");

            if(code >= 0) {
                return true;
            }
        } catch (JSONException | IOException e) {
            Log.e(TAG, "Exception:", e);
        }
        return false;
    }

    /**
     * 建立连接
     */
    public static Boolean endCall(Integer callId, String callerId, String answerId){
        try {
            String url = Uri.parse(END_CALL)
                    .buildUpon()
                    .appendQueryParameter("callId", String.valueOf(callId))
                    .appendQueryParameter("caller", callerId)
                    .appendQueryParameter("answer", answerId)
                    .build().toString();
            String res = HttpUtil.get(url);
            Log.i(TAG, "Response: " + res);
            JSONObject response = new JSONObject(res);

            int code = response.getInt("code");
            String message = response.getString("message");

            if(code >= 0) {
                return true;
            }
        } catch (JSONException | IOException e) {
            Log.e(TAG, "Exception:", e);
        }
        return false;
    }

    /**
     * 获取用户评论
     */
    public static List<Comment> getComments(String staffId) {
        List<Comment> comments = new ArrayList<>();
        try {
            Gson gson = new Gson();
            String res = HttpUtil.get(COMMETS +'/'+staffId);
            JSONObject response = new JSONObject(res);
            String data = response.getString("data");
            Log.i(TAG, "data: "+ data);
            JsonArray array = new JsonParser().parse(data).getAsJsonArray();
            for (JsonElement jsonElement : array) {
                comments.add(gson.fromJson(jsonElement, Comment.class));
            }
        } catch (JSONException | IOException e) {
            Log.e(TAG, "Exception:", e);
        }
        return comments;
    }


    /**
     * 获取客服资料
     */
    public static StaffProfile getProfile(String id) {
        StaffProfile profile = new StaffProfile();
        try {
            Gson gson = new Gson();
            String url = PROFILE_INFO + "/" + id;
            String res = HttpUtil.get(url);
            if(res == null || TextUtils.isEmpty(res))
                return profile;
            JSONObject response = new JSONObject(res);
            String data = response.getString("data");
            profile = gson.fromJson(data, StaffProfile.class);
            Log.i(TAG, "data: "+ data);
        } catch (JSONException | IOException e) {
            Log.e(TAG, "Exception:", e);
        }
        return profile;
    }

    /**
     * 更新信息
     */
    public static Boolean updateProfile(StaffProfile staffProfile){
        try {
            Gson gson = new Gson();
            String json = gson.toJson(staffProfile);
            Log.i(TAG, "Request: " + json);
            String res = HttpUtil.post(PROFILE_UPDATE, json);
            Log.i(TAG, "Response: " + res);
            JSONObject response = new JSONObject(res);

            int code = response.getInt("code");
            String message = response.getString("message");

            if(code >= 0) {
                return true;
            }
        } catch (JSONException | IOException e) {
            Log.e(TAG, "Exception:", e);
        }
        return false;
    }

    /**
     * 更新用户头像
     */
    public static Boolean updateAvatar(String userId, String avatarUrl){
        try {
            String url = Uri.parse(PROFILE_AVATAR)
                    .buildUpon()
                    .appendQueryParameter("id", userId)
                    .appendQueryParameter("avatar", avatarUrl)
                    .build().toString();
            String res = HttpUtil.get(url);
            Log.i(TAG, "Response: " + res);
            JSONObject response = new JSONObject(res);

            int code = response.getInt("code");
            String message = response.getString("message");

            if(code >= 0) {
                return true;
            }
        } catch (JSONException | IOException e) {
            Log.e(TAG, "Exception:", e);
        }
        return false;
    }

    String getCosSig() {
        String sign = "";
        try {
            String res = HttpUtil.get(COS_SIG);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();
            int ret = response.getInt("code");
            if (ret >= 0) {
                sign = response.getString("data");
            }
            Log.w(TAG,"sign = " +sign);

        } catch (JSONException | IOException e) {
            Log.e(TAG, "Exception:", e);
        }
        return sign;
    }
}



