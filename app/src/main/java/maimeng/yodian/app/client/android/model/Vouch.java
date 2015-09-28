package maimeng.yodian.app.client.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.henjue.library.hnet.anntoation.Param;

/**
 * Created by xujianhua on 9/25/15.
 */
public class Vouch implements Parcelable{
//   {"id":"13","name":"\u623f\u7ba1\u5c40","telephone":"\u4f46ghjj","uid":"70","email":"\u8179\u80a1\u6c9f","qq":"\u623f\u7ba1\u5c40","content":"ghjj\u5443","bwid":"","back_detail":"","status":"0","createtime":"1443090834"}}
private long id;
    private String name;
    private long uid;
    private String email;
    private String qq;
    private String telephone;
    private String content;
    private long bwid;
    private String back_detail;
    private int status;
    private long createtime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getBwid() {
        return bwid;
    }

    public void setBwid(long bwid) {
        this.bwid = bwid;
    }

    public String getBack_detail() {
        return back_detail;
    }

    public void setBack_detail(String back_detail) {
        this.back_detail = back_detail;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int statuas) {
        this.status = status;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeLong(this.uid);
        dest.writeString(this.email);
        dest.writeString(this.qq);
        dest.writeString(this.telephone);
        dest.writeString(this.content);
        dest.writeLong(this.bwid);
        dest.writeString(this.back_detail);
        dest.writeInt(this.status);
        dest.writeLong(this.createtime);
    }

    public Vouch() {
    }

    protected Vouch(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.uid = in.readLong();
        this.email = in.readString();
        this.qq = in.readString();
        this.telephone = in.readString();
        this.content = in.readString();
        this.bwid = in.readLong();
        this.back_detail = in.readString();
        this.status = in.readInt();
        this.createtime = in.readLong();
    }

    public static final Creator<Vouch> CREATOR = new Creator<Vouch>() {
        public Vouch createFromParcel(Parcel source) {
            return new Vouch(source);
        }

        public Vouch[] newArray(int size) {
            return new Vouch[size];
        }
    };
}
