package maimeng.yodian.app.client.android.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.easemob.applib.controller.HXSDKHelper;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import maimeng.yodian.app.client.android.BR;
import maimeng.yodian.app.client.android.YApplication;
import maimeng.yodian.app.client.android.chat.DemoApplication;
import maimeng.yodian.app.client.android.chat.DemoHXSDKHelper;
import maimeng.yodian.app.client.android.chat.db.UserDao;
import maimeng.yodian.app.client.android.chat.domain.RobotUser;


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
    @SerializedName("id")
    private long id;

    public long getId() {
        return id == 0 ? uid : id;
    }

    public void setId(long id) {
        this.id = id;
    }

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
        return uid == 0 ? id : uid;
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
            User authUser = YApplication.getInstance().getAuthUser();
            if (authUser != null) {
                return authUser;
            }
            SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
            String uid = pref.getString(KEY_UID, "");
            String nickname = pref.getString(KEY_NICK, "");
            String img = pref.getString(KEY_IMG, "");
            String t_nickname = pref.getString(KEY_T_NICK, "");
            String t_img = pref.getString(KEY_T_IMG, "");
            String token = pref.getString(KEY_TOKEN, "");
            String chatname = pref.getString(KEY_CHATNAME, "");
            int type = pref.getInt(KEY_TYPE, 0);
            User user = new User(t_nickname, t_img, type, token, "".equals(uid) ? 0 : Long.parseLong(uid), nickname, chatname, img);
            YApplication.getInstance().setAuthUser(user);
            user.setInfo(Info.read(pref));
            user.pushOn = pref.getBoolean(KEY_PUSH, true);
            maimeng.yodian.app.client.android.chat.domain.User u = new maimeng.yodian.app.client.android.chat.domain.User();
            u.setAvatar(img);
            u.setUsername(chatname);
            u.setNick(nickname);
            u.setId(uid);
            u.setWechat(user.getWechat());

            RobotUser rotot = new RobotUser();
            rotot.setAvatar(img);
            rotot.setUsername(chatname);
            rotot.setNick(nickname);
            rotot.setId(uid);
            rotot.setWechat(user.getWechat());


            YApplication.getInstance().setCurrentUser(u);
            // 存入内存
            ((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveOrUpdate(u.getUsername(), u);
            ((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveOrUpdate(u.getUsername(), rotot);
            // 存入db
            UserDao dao = new UserDao(context);
            dao.saveOrUpdate(u);
            dao.saveOrUpdate(rotot);
            return user;
        }
    }

    public synchronized boolean write(Context context) {
        synchronized (User.class) {
            YApplication.getInstance().setAuthUser(this);
            SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
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
            return true;
        }
    }


    public static boolean clear(Context context) {
        if (null == context) {
            return false;
        }
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
        // 登陆成功，保存用户名密码
        DemoApplication.getInstance().setUserName("");
        YApplication.getInstance().setAuthUser(null);
        return true;
    }

    public Info getInfo() {
        return info;
    }

    public User setInfo(Info info) {
        this.info = info;
        update(info);
        return this;
    }

    private Info info;

    public void setWechat(String wechat) {
        if (info != null) info.setWechat(wechat);
    }

    public void update(User user) {
        final String avatar = user.getAvatar();
        final String chatLoginName = user.getChatLoginName();
        final String nickname = user.getNickname();
        final String wecaht = user.getWechat();
        if (!TextUtils.isEmpty(avatar) && !"http://".equalsIgnoreCase(avatar)) {
            this.avatar = avatar;
        }
        if (!TextUtils.isEmpty(chatLoginName)) {
            this.chatLoginName = chatLoginName;
        }
        if (!TextUtils.isEmpty(nickname)) {
            this.nickname = nickname;
        }
        if (!TextUtils.isEmpty(wecaht)) {
            this.setWechat(wecaht);
        }
        final User currentUser = YApplication.getInstance().getAuthUser();
        if (currentUser != null) {
            if (currentUser.getUid() == this.getUid()) {
                YApplication.getInstance().setAuthUser(this);
            }
        }
    }

    public static class Info extends User {
        private static final String KEY_MOBILE = "_key_mobile";
        private static final String KEY_WECHAT = "_key_wechat";
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

        public synchronized boolean write(SharedPreferences.Editor editor) {
            synchronized (User.class) {
                editor.putString(KEY_MOBILE, this.mobile == null ? "" : this.mobile);
                editor.putString(KEY_WECHAT, this.wechat == null ? "" : this.wechat);
                return true;
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
