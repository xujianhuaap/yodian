package maimeng.yodian.app.client.android.model.chat;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 仅仅用于打开聊天界面的时候参数的传入
 */
public class ChatUser implements Parcelable {

    private final long uid;//用户id
    private final String chatName;//环信用户名
    private final String nickName;//用户昵称
    private String qq = "";
    private String wechat = "";
    private String mobile = "";

    public String getChatName() {
        return chatName;
    }

    public String getMobile() {
        return mobile == null ? "" : mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNickName() {
        return nickName;
    }

    public String getQq() {
        return qq == null ? "" : qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public long getUid() {
        return uid;
    }

    public String getWechat() {
        return wechat == null ? "" : wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public ChatUser(String chatName, long uid, String nickName) {
        this.chatName = chatName;
        this.uid = uid;
        this.nickName = nickName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.uid);
        dest.writeString(this.chatName);
        dest.writeString(this.nickName);
        dest.writeString(this.qq);
        dest.writeString(this.wechat);
        dest.writeString(this.mobile);
    }

    protected ChatUser(Parcel in) {
        this.uid = in.readLong();
        this.chatName = in.readString();
        this.nickName = in.readString();
        this.qq = in.readString();
        this.wechat = in.readString();
        this.mobile = in.readString();
    }

    public static final Parcelable.Creator<ChatUser> CREATOR = new Parcelable.Creator<ChatUser>() {
        public ChatUser createFromParcel(Parcel source) {
            return new ChatUser(source);
        }

        public ChatUser[] newArray(int size) {
            return new ChatUser[size];
        }
    };
}
