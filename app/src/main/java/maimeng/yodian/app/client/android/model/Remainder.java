package maimeng.yodian.app.client.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by android on 2015/9/21.
 */
public class Remainder implements Parcelable {
    private double money;//我的余额
    private double withdraw;//已提现金额
    private int bankMsg;//是否又银行信息0否、1是
    private double during;//提现中的金额

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public double getWithdraw() {
        return withdraw;
    }

    public void setWithdraw(double withdraw) {
        this.withdraw = withdraw;
    }

    public double getDuring() {
        return during;
    }

    public void setDuring(double during) {
        this.during = during;
    }

    private int vouchMsg;//是否有申请担保交易状态消息 0 否 1 是
    @SerializedName("vouch_status")
    private int vouchStatus;//申请担保交易状态 0 待审核 2 审核通过 3 拒绝 4 用户取消绑定
    @SerializedName("card_status")
    private int cardStatus;//绑定银行卡状态 0 待审核 2 审核通过 3拒绝 4 用户取消申请

    public int getBankMsg() {
        return bankMsg;
    }

    public void setBankMsg(int bankMsg) {
        this.bankMsg = bankMsg;
    }


    public int getVouchMsg() {
        return vouchMsg;
    }

    public void setVouchMsg(int vouchMsg) {
        this.vouchMsg = vouchMsg;
    }

    public int getVouchStatus() {
        return vouchStatus;
    }

    public void setVouchStatus(int vouchStatus) {
        this.vouchStatus = vouchStatus;
    }

    public int getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(int cardStatus) {
        this.cardStatus = cardStatus;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.money);
        dest.writeDouble(this.withdraw);
        dest.writeInt(this.bankMsg);
        dest.writeDouble(this.during);
        dest.writeInt(this.vouchMsg);
        dest.writeInt(this.vouchStatus);
        dest.writeInt(this.cardStatus);
    }

    public Remainder() {
    }

    protected Remainder(Parcel in) {
        this.money = in.readDouble();
        this.withdraw = in.readDouble();
        this.bankMsg = in.readInt();
        this.during = in.readDouble();
        this.vouchMsg = in.readInt();
        this.vouchStatus = in.readInt();
        this.cardStatus = in.readInt();
    }

    public static final Creator<Remainder> CREATOR = new Creator<Remainder>() {
        public Remainder createFromParcel(Parcel source) {
            return new Remainder(source);
        }

        public Remainder[] newArray(int size) {
            return new Remainder[size];
        }
    };
}