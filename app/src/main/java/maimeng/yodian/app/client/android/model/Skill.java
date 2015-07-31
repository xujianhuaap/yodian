package maimeng.yodian.app.client.android.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import maimeng.yodian.app.client.android.BR;
import maimeng.yodian.app.client.android.BuildConfig;

/**
 * Created by android on 15-7-14.
 */
public class Skill extends BaseObservable implements Parcelable {

    @SerializedName("sid")
    private long id;//技能id
    private long uid;//用户id
    private String weichat;//微信号
    private String qrcode;//二维码图片
    private String qrcodeUrl;//二维码地址
    @SerializedName("hxname")
    private String chatLoginName;

    public String getChatLoginName() {
        return chatLoginName;
    }

    public void setChatLoginName(String chatLoginName) {
        this.chatLoginName = chatLoginName;
    }

    public String getWeichat() {
        return weichat;
    }

    public void setWeichat(String weichat) {
        this.weichat = weichat;
    }

    public String getQrcodeUrl() {
        return BuildConfig.DEBUG ? qrcodeUrl.replaceAll("^http://share.yodian.me/", "http://sharetest.yodian.me/") : qrcodeUrl;
    }

    public void setQrcodeUrl(String qrcodeUrl) {
        this.qrcodeUrl = qrcodeUrl;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    private String name;//技能名称
    private String pic;//技能图片
    private String content;//技能描述
    private String price;//技能价格
    private String unit;//技能价格单位
    @Bindable
    private int status = 0;//2下架状态，0上架状态
    private Date createtime;//技能添加时间戳
    private String nickname;//用户昵称
    private String avatar;//用户头像数据

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    @Bindable
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        notifyPropertyChanged(BR.content);
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
        notifyPropertyChanged(BR.status);
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

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

    public Skill() {
    }

    public void update(Skill skill) {
        this.uid = skill.uid;
        this.name = skill.name;
        this.pic = skill.pic;
        this.content = skill.content;
        this.price = skill.price;
        this.unit = skill.unit;
        this.status = skill.status;
        this.createtime = skill.createtime;
        this.nickname = skill.nickname;
        this.avatar = skill.avatar;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.uid);
        dest.writeString(this.weichat);
        dest.writeString(this.qrcode);
        dest.writeString(this.qrcodeUrl);
        dest.writeString(this.name);
        dest.writeString(this.pic);
        dest.writeString(this.content);
        dest.writeString(this.price);
        dest.writeString(this.unit);
        dest.writeInt(this.status);
        dest.writeLong(createtime != null ? createtime.getTime() : -1);
        dest.writeString(this.nickname);
        dest.writeString(this.avatar);
    }

    protected Skill(Parcel in) {
        this.id = in.readLong();
        this.uid = in.readLong();
        this.weichat = in.readString();
        this.qrcode = in.readString();
        this.qrcodeUrl = in.readString();
        this.name = in.readString();
        this.pic = in.readString();
        this.content = in.readString();
        this.price = in.readString();
        this.unit = in.readString();
        this.status = in.readInt();
        long tmpCreatetime = in.readLong();
        this.createtime = tmpCreatetime == -1 ? null : new Date(tmpCreatetime);
        this.nickname = in.readString();
        this.avatar = in.readString();
    }

    public static final Creator<Skill> CREATOR = new Creator<Skill>() {
        public Skill createFromParcel(Parcel source) {
            return new Skill(source);
        }

        public Skill[] newArray(int size) {
            return new Skill[size];
        }
    };
}
