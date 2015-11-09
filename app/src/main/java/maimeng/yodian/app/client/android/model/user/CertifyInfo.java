package maimeng.yodian.app.client.android.model.user;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xujianhua on 11/9/15.
 */
public class CertifyInfo implements Parcelable{

    /**
     * cname : 优点
     * idcard : 412823199101187220
     * mobile : 18516668150
     */

    private String cname;
    private String idcard;
    private String mobile;

    public void setCname(String cname) {
        this.cname = cname;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCname() {
        return cname;
    }

    public String getIdcard() {
        return idcard;
    }

    public String getMobile() {
        return mobile;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cname);
        dest.writeString(this.idcard);
        dest.writeString(this.mobile);
    }

    public CertifyInfo() {
    }

    protected CertifyInfo(Parcel in) {
        this.cname = in.readString();
        this.idcard = in.readString();
        this.mobile = in.readString();
    }

    public static final Creator<CertifyInfo> CREATOR = new Creator<CertifyInfo>() {
        public CertifyInfo createFromParcel(Parcel source) {
            return new CertifyInfo(source);
        }

        public CertifyInfo[] newArray(int size) {
            return new CertifyInfo[size];
        }
    };
}
