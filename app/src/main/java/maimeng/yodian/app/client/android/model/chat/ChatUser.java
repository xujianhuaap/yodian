package maimeng.yodian.app.client.android.model.chat;

import org.parceler.Parcel;

/**
 * 仅仅用于打开聊天界面的时候参数的传入
 */
@Parcel(value = Parcel.Serialization.BEAN)
public class ChatUser {
    public ChatUser() {

    }

    private long uid;//用户id
    private String chatName;//环信用户名
    private String nickName;//用户昵称
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

}
