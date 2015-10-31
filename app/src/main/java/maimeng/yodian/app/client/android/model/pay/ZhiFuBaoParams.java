package maimeng.yodian.app.client.android.model.pay;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xujianhua on 10/31/15.
 */
public  class ZhiFuBaoParams implements IPayParams,Parcelable{


    /**
     * params :
     * oid : 577
     * isLottery : 1
     * lotUrl : http://share.yodian.me/lottery.php
     */

    private String params;
    private int oid;
    private int isLottery;
    private String lotUrl;

    public void setParams(String params) {
        this.params = params;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    public void setIsLottery(int isLottery) {
        this.isLottery = isLottery;
    }

    public void setLotUrl(String lotUrl) {
        this.lotUrl = lotUrl;
    }

    public String getParams() {
        return params;
    }

    public int getOid() {
        return oid;
    }

    public int getIsLottery() {
        return isLottery;
    }

    public String getLotUrl() {
        return lotUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.params);
        dest.writeInt(this.oid);
        dest.writeInt(this.isLottery);
        dest.writeString(this.lotUrl);
    }

    public ZhiFuBaoParams() {
    }

    protected ZhiFuBaoParams(Parcel in) {
        this.params = in.readString();
        this.oid = in.readInt();
        this.isLottery = in.readInt();
        this.lotUrl = in.readString();
    }

    public static final Creator<ZhiFuBaoParams> CREATOR = new Creator<ZhiFuBaoParams>() {
        public ZhiFuBaoParams createFromParcel(Parcel source) {
            return new ZhiFuBaoParams(source);
        }

        public ZhiFuBaoParams[] newArray(int size) {
            return new ZhiFuBaoParams[size];
        }
    };
}
