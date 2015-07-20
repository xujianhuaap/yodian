package maimeng.yodian.app.client.android.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.gson.annotations.SerializedName;


/**
 * Created by henjue on 2015/4/7.
 */
public class User {
    private static final String PREFERENCES_NAME = "_userinfo";
    private static final String KEY_T_NICK = "_t_nickname";
    private static final String KEY_T_IMG = "_t_headimg";
    private static final String KEY_NICK = "_nickname";
    private static final String KEY_IMG = "_headimg";
    private static final String KEY_UID = "_uid";
    private static final String KEY_TOKEN = "_token";
    private static final String KEY_TYPE = "_type";
    private static final String KEY_PUSH = "_PUSH";
    private static final String KEY_WECHAT = "_wechat";
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("SN_KEY_API")
    private  String token;
    @SerializedName("weichat")
    private String wechat;

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    @SerializedName("uid")
    private long uid;
    private String t_nickname;//第三方昵称
    private String t_img;//第三方头像

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getT_nickname() {
        return t_nickname;
    }

    public void setT_nickname(String t_nickname) {
        this.t_nickname = t_nickname;
    }

    public String getT_img() {
        return t_img;
    }

    public void setT_img(String t_img) {
        this.t_img = t_img;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public boolean isPushOn() {
        return pushOn;
    }

    public void setPushOn(boolean pushOn) {
        this.pushOn = pushOn;
    }

    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean pushOn = true;
    public int loginType;//0官方,1微信,2微博

    public User(){

    }
    public User(String t_name, String t_img, int loginType, String token, long uid, String nickname, String avatar) {
        this.uid = uid;
        this.nickname = nickname;
        this.avatar = avatar;
        this.loginType = loginType;
        this.token = token;
        this.t_nickname=t_name;
        this.t_img=t_img;
    }

    public static synchronized User read(Context context) {
        synchronized (User.class) {
            SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
            String uid = pref.getString(KEY_UID, "");
            String nickname = pref.getString(KEY_NICK, "");
            String img = pref.getString(KEY_IMG, "");
            String t_nickname = pref.getString(KEY_T_NICK, "");
            String t_img = pref.getString(KEY_T_IMG, "");
            String token = pref.getString(KEY_TOKEN, "");
            String wechat = pref.getString(KEY_WECHAT, "");
            int type = pref.getInt(KEY_TYPE, 0);
            User user = new User(t_nickname, t_img, type, token, "".equals(uid)?0: Long.parseLong(uid), nickname, img);
            user.setWechat(wechat);
            user.pushOn = pref.getBoolean(KEY_PUSH, true);
            return user;
        }
    }

    public void write(Context context) {
        synchronized (User.class) {
            SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(KEY_T_IMG, t_img == null ? "" : t_img);
            editor.putString(KEY_T_NICK, t_nickname == null ? "" : t_nickname);
            editor.putString(KEY_TOKEN, token == null ? "" : token);
            editor.putString(KEY_NICK, nickname == null ? "" : nickname);
            editor.putString(KEY_UID, uid == 0 ? "" : ""+uid);
            editor.putString(KEY_IMG, avatar == null ? "" : avatar);
            editor.putString(KEY_TOKEN, token == null ? "" : token);
            editor.putString(KEY_WECHAT, wechat == null ? "" : wechat);
            editor.putBoolean(KEY_PUSH, pushOn);
            editor.putInt(KEY_TYPE, loginType);

            editor.apply();
        }
    }


    public static User parse(Bundle bundle) {
        String nickname = bundle.getString(KEY_NICK, "");
        String uid = bundle.getString(KEY_UID, "");
        String img = bundle.getString(KEY_IMG, "");
        String token = bundle.getString(KEY_TOKEN, "");
        String wechat = bundle.getString(KEY_WECHAT, "");
        int type = bundle.getInt(KEY_TYPE, 0);
        String t_nickname = bundle.getString(KEY_T_NICK, "");
        String t_img = bundle.getString(KEY_T_IMG, "");
        boolean pushOn = bundle.getBoolean(KEY_PUSH, true);
        User user = new User(t_nickname,t_img, type, token, "".equals(uid)?0: Long.parseLong(uid), nickname, img);
        user.setWechat(wechat);
        user.pushOn = pushOn;
        return user;
    }

    public static void clear(Context context) {
        if (null == context) {
            return;
        }
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }
}
