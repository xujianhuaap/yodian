package maimeng.yodian.app.client.android.model.remainder;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import maimeng.yodian.app.client.android.view.deal.BindStatus;

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
    private BindStatus vouchStatus;//申请担保交易状态 0 待审核 2 审核通过 3 拒绝 4 用户取消绑定
    @SerializedName("card_status")
    private BindStatus cardStatus;

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

    public BindStatus getVouchStatus() {
        return vouchStatus;
    }

    public void setVouchStatus(BindStatus vouchStatus) {
        this.vouchStatus = vouchStatus;
    }


    public BindStatus getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(BindStatus cardStatus) {
        this.cardStatus = cardStatus;
    }

    public Remainder() {
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
        dest.writeInt(this.vouchStatus == null ? -1 : this.vouchStatus.ordinal());
        dest.writeInt(this.cardStatus == null ? -1 : this.cardStatus.ordinal());
    }

    protected Remainder(Parcel in) {
        this.money = in.readDouble();
        this.withdraw = in.readDouble();
        this.bankMsg = in.readInt();
        this.during = in.readDouble();
        this.vouchMsg = in.readInt();
        int tmpVouchStatus = in.readInt();
        this.vouchStatus = tmpVouchStatus == -1 ? null : BindStatus.values()[tmpVouchStatus];
        int tmpCardStatus = in.readInt();
        this.cardStatus = tmpCardStatus == -1 ? null : BindStatus.values()[tmpCardStatus];
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
