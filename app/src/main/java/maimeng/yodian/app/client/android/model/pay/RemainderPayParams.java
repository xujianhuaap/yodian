package maimeng.yodian.app.client.android.model.pay;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xujianhua on 10/15/15.
 */
public class RemainderPayParams implements IPayParams,Parcelable{


    /**
     * total_fee : 7000.00
     * out_trade_no : 20151030153418467685
     * body : 购买技能: 板凳的手绘小屋
     * oid : 522
     * isLottery : 1
     * lotUrl : http://share.yodian.me/lottery.php
     */

    private String total_fee;
    private String out_trade_no;
    private String body;
    private String oid;
    private int isLottery;
    private String lotUrl;

    public void setTotal_fee(String total_fee) {
        this.total_fee = total_fee;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public void setIsLottery(int isLottery) {
        this.isLottery = isLottery;
    }

    public void setLotUrl(String lotUrl) {
        this.lotUrl = lotUrl;
    }

    public String getTotal_fee() {
        return total_fee;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public String getBody() {
        return body;
    }

    public String  getOid() {
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
        dest.writeString(this.total_fee);
        dest.writeString(this.out_trade_no);
        dest.writeString(this.body);
        dest.writeString(this.oid);
        dest.writeInt(this.isLottery);
        dest.writeString(this.lotUrl);
    }

    public RemainderPayParams() {
    }

    protected RemainderPayParams(Parcel in) {
        this.total_fee = in.readString();
        this.out_trade_no = in.readString();
        this.body = in.readString();
        this.oid = in.readString();
        this.isLottery = in.readInt();
        this.lotUrl = in.readString();
    }

    public static final Creator<RemainderPayParams> CREATOR = new Creator<RemainderPayParams>() {
        public RemainderPayParams createFromParcel(Parcel source) {
            return new RemainderPayParams(source);
        }

        public RemainderPayParams[] newArray(int size) {
            return new RemainderPayParams[size];
        }
    };
}
