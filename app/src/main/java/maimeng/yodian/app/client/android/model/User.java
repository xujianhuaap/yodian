package maimeng.yodian.app.client.android.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.Bindable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import maimeng.yodian.app.client.android.BR;
import maimeng.yodian.app.client.android.YApplication;
import maimeng.yodian.app.client.android.chat.DemoApplication;
import maimeng.yodian.app.client.android.common.model.UserBaseColum;


/**
 * Created by henjue on 2015/4/7.
 */
public class User extends UserBaseColum {
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("avatar")
    @Bindable
    private String avatar;
    @SerializedName("SN_KEY_API")
    private String token;

    @SerializedName("hxname")
    private String chatLoginName;

    public String getChatLoginName() {
        return chatLoginName;
    }

    public void setChatLoginName(String chatLoginName) {
        this.chatLoginName = chatLoginName;
    }

    public String getWechat() {
        return info != null ? info.getWechat() : "";
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
        return TextUtils.isEmpty(avatar) ? "http://" : avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
        notifyPropertyChanged(BR.avatar);
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

    public User() {

    }

    public User(String t_name, String t_img, int loginType, String token, long uid, String nickname, String chatLoginName, String avatar) {
        this.uid = uid;
        this.nickname = nickname;
        this.avatar = avatar;
        this.loginType = loginType;
        this.token = token;
        this.t_nickname = t_name;
        this.t_img = t_img;
        this.chatLoginName = chatLoginName;
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
            String chatname = pref.getString(KEY_CHATNAME, "");

            maimeng.yodian.app.client.android.chat.domain.User u = new maimeng.yodian.app.client.android.chat.domain.User();
            u.setAvatar(img);
            u.setUsername(chatname);
            u.setNick(nickname);
            u.setId(uid);
            YApplication.getInstance().setCurrentUser(u);

            int type = pref.getInt(KEY_TYPE, 0);
            User user = new User(t_nickname, t_img, type, token, "".equals(uid) ? 0 : Long.parseLong(uid), nickname, chatname, img);
            user.setInfo(Info.read(pref));
            user.pushOn = pref.getBoolean(KEY_PUSH, true);
            return user;
        }
    }

    public synchronized void write(Context context) {
        synchronized (User.class) {
            SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(KEY_T_IMG, t_img == null ? "" : t_img);
            editor.putString(KEY_T_NICK, t_nickname == null ? "" : t_nickname);
            editor.putString(KEY_TOKEN, token == null ? "" : token);
            editor.putString(KEY_NICK, nickname == null ? "" : nickname);
            editor.putString(KEY_CHATNAME, chatLoginName == null ? "" : chatLoginName);
            editor.putString(KEY_UID, uid == 0 ? "" : "" + uid);
            editor.putString(KEY_IMG, avatar == null ? "" : avatar);
            editor.putString(KEY_TOKEN, token == null ? "" : token);
            editor.putBoolean(KEY_PUSH, pushOn);
            editor.putInt(KEY_TYPE, loginType);
            if (info != null) info.write(editor);
            editor.apply();
        }
    }


    public static void clear(Context context) {
        if (null == context) {
            return;
        }
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
        // 登陆成功，保存用户名密码
        DemoApplication.getInstance().setUserName("");
    }

    public Info getInfo() {
        return info;
    }

    public User setInfo(Info info) {
        this.info = info;
        return this;
    }

    private Info info;

    public void setWechat(String wechat) {
        if (info != null) info.setWechat(wechat);
    }

    public static class Info {

        private String mobile;
        @SerializedName("weichat")
        private String wechat;

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getWechat() {
            return wechat != null ? wechat : "";
        }

        public void setWechat(String wechat) {
            this.wechat = wechat;
        }

        public synchronized void write(SharedPreferences.Editor editor) {
            synchronized (User.class) {
                editor.putString(KEY_MOBILE, this.mobile == null ? "" : this.mobile);
                editor.putString(KEY_WECHAT, this.wechat == null ? "" : this.wechat);
            }
        }

        public static synchronized Info read(SharedPreferences pref) {
            synchronized (User.class) {
                Info info = new Info();
                String mobile = pref.getString(KEY_MOBILE, "");
                String wechat = pref.getString(KEY_WECHAT, "");
                info.setWechat(wechat);
                info.setMobile(mobile);
                return info;
            }
        }
    }
}
