package maimeng.yodian.app.client.android.model.pay;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xujianhua on 10/13/15.
 */
public class WXPayParams implements IPayParams,Parcelable{

    /**
     * appid : wxa8accb138107c861
     * partnerid : 1253302901
     * prepayid : wx2015101317471239b8ef722b0353762043
     * package : Sign=WXPay
     * noncestr : 14447296321090636207
     * timestamp : 1444729632
     * sign : C1E2C81B3002C902E65DA76063E8DFA0
     */

    private String appid;
    private String partnerid;
    private String prepayid;
    @SerializedName("package")
    private String packageX;
    private String noncestr;
    private int timestamp;
    private String sign;

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public void setPackageX(String packageX) {
        this.packageX = packageX;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getAppid() {
        return appid;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public String getPackageX() {
        return packageX;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public String getSign() {
        return sign;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.appid);
        dest.writeString(this.partnerid);
        dest.writeString(this.prepayid);
        dest.writeString(this.packageX);
        dest.writeString(this.noncestr);
        dest.writeInt(this.timestamp);
        dest.writeString(this.sign);
    }

    public WXPayParams() {
    }

    protected WXPayParams(Parcel in) {
        this.appid = in.readString();
        this.partnerid = in.readString();
        this.prepayid = in.readString();
        this.packageX = in.readString();
        this.noncestr = in.readString();
        this.timestamp = in.readInt();
        this.sign = in.readString();
    }

    public static final Creator<WXPayParams> CREATOR = new Creator<WXPayParams>() {
        public WXPayParams createFromParcel(Parcel source) {
            return new WXPayParams(source);
        }

        public WXPayParams[] newArray(int size) {
            return new WXPayParams[size];
        }
    };
}
