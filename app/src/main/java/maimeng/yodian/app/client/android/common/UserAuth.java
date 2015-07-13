package maimeng.yodian.app.client.android.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;



/**
 * Created by henjue on 2015/4/7.
 */
public class UserAuth {
    private static final String PREFERENCES_NAME = "_userinfo";
    private static final String KEY_T_NICK = "_t_nickname";
    private static final String KEY_T_IMG = "_t_headimg";
    private static final String KEY_NICK = "_nickname";
    private static final String KEY_IMG = "_headimg";
    private static final String KEY_UID = "_uid";
    private static final String KEY_TOKEN = "_token";
    private static final String KEY_TYPE = "_type";
    private static final String KEY_PUSH = "_PUSH";
    public final String nickname;
    public final String img;
    public final String t_nickname;//第三方昵称
    public final String t_img;//第三方头像
    public final long uid;
    public final String token;
    public boolean pushOn = true;
    public final int loginType;//0官方,1微信,2微博


    public UserAuth( String t_name, String t_img, int loginType, String token, long uid, String nickname, String img) {
        this.uid = uid;
        this.nickname = nickname;
        this.img = img;
        this.loginType = loginType;
        this.token = token;
        this.t_nickname=t_name;
        this.t_img=t_img;
    }

    public static synchronized UserAuth read(Context context) {
        synchronized (UserAuth.class) {
            SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
            String uid = pref.getString(KEY_UID, "");
            String nickname = pref.getString(KEY_NICK, "");
            String img = pref.getString(KEY_IMG, "");
            String t_nickname = pref.getString(KEY_T_NICK, "");
            String t_img = pref.getString(KEY_T_IMG, "");
            String token = pref.getString(KEY_TOKEN, "");
            int type = pref.getInt(KEY_TYPE, 0);
            UserAuth userAuth = new UserAuth(t_nickname, t_img, type, token, "".equals(uid)?0: Long.parseLong(uid), nickname, img);
            userAuth.pushOn = pref.getBoolean(KEY_PUSH, true);
            return userAuth;
        }
    }

    public void write(Context context) {
        synchronized (UserAuth.class) {
            SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(KEY_T_IMG, t_img == null ? "" : t_img);
            editor.putString(KEY_T_NICK, t_nickname == null ? "" : t_nickname);
            editor.putString(KEY_TOKEN, token == null ? "" : token);
            editor.putString(KEY_NICK, nickname == null ? "" : nickname);
            editor.putString(KEY_UID, uid == 0 ? "" : ""+uid);
            editor.putString(KEY_IMG, img == null ? "" : img);
            editor.putString(KEY_TOKEN, token == null ? "" : token);
            editor.putBoolean(KEY_PUSH, pushOn);
            editor.putInt(KEY_TYPE, loginType);

            editor.apply();
        }
    }


    public static UserAuth parse(Bundle bundle) {
        String nickname = bundle.getString(KEY_NICK, "");
        String uid = bundle.getString(KEY_UID, "");
        String img = bundle.getString(KEY_IMG, "");
        String token = bundle.getString(KEY_TOKEN, "");
        int type = bundle.getInt(KEY_TYPE, 0);
        String t_nickname = bundle.getString(KEY_T_NICK, "");
        String t_img = bundle.getString(KEY_T_IMG, "");
        boolean pushOn = bundle.getBoolean(KEY_PUSH, true);
        UserAuth userAuth = new UserAuth(t_nickname,t_img, type, token, "".equals(uid)?0: Long.parseLong(uid), nickname, img);
        userAuth.pushOn = pushOn;
        return userAuth;
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
