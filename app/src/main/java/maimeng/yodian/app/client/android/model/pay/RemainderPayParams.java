package maimeng.yodian.app.client.android.model.pay;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xujianhua on 10/15/15.
 */
public class RemainderPayParams implements IPayParams,Parcelable{

    /**
     * total_fee : 0.01
     * out_trade_no : 20151015171715139690
     * body : 购买技能: 华盛顿脸色看&ldquo;&rdquo;&hellip;
     * oid : 260
     */

    private String total_fee;
    private String out_trade_no;
    private String body;
    private String oid;

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

    public String getTotal_fee() {
        return total_fee;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public String getBody() {
        return body;
    }

    public String getOid() {
        return oid;
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
    }

    public RemainderPayParams() {
    }

    protected RemainderPayParams(Parcel in) {
        this.total_fee = in.readString();
        this.out_trade_no = in.readString();
        this.body = in.readString();
        this.oid = in.readString();
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
