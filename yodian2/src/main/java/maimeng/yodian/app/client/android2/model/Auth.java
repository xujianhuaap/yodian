package maimeng.yodian.app.client.android2.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by android on 2015/11/12.
 */
@Parcel(
        value = Parcel.Serialization.BEAN, analyze = {Auth.class})
public class Auth {
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("uid")
    private long uid;
    @SerializedName("SN_KEY_API")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
