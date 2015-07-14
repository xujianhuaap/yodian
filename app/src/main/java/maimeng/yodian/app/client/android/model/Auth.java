package maimeng.yodian.app.client.android.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by android on 15-7-13.
 */
public class Auth {
    @SerializedName("SN_KEY_API")
    private String token;
    private String nickname;
    private int uid;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
