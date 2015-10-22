package maimeng.yodian.app.client.android.model.remainder;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by android on 2015/10/21.
 */
public class WDModel implements Parcelable {


    private long id;
    private String withdraw_code;
    private String money;
    private String money_read;
    private long uid;
    private String card_id;
    private String why_id;
    private String backwhy;
    private String audit_time;
    private int status;
    private Date createtime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWithdraw_code() {
        return withdraw_code;
    }

    public void setWithdraw_code(String withdraw_code) {
        this.withdraw_code = withdraw_code;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getMoney_read() {
        return money_read;
    }

    public void setMoney_read(String money_read) {
        this.money_read = money_read;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public String getWhy_id() {
        return why_id;
    }

    public void setWhy_id(String why_id) {
        this.why_id = why_id;
    }

    public String getBackwhy() {
        return backwhy;
    }

    public void setBackwhy(String backwhy) {
        this.backwhy = backwhy;
    }

    public String getAudit_time() {
        return audit_time;
    }

    public void setAudit_time(String audit_time) {
        this.audit_time = audit_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.withdraw_code);
        dest.writeString(this.money);
        dest.writeString(this.money_read);
        dest.writeLong(this.uid);
        dest.writeString(this.card_id);
        dest.writeString(this.why_id);
        dest.writeString(this.backwhy);
        dest.writeString(this.audit_time);
        dest.writeInt(this.status);
        dest.writeLong(createtime != null ? createtime.getTime() : -1);
    }

    public WDModel() {
    }

    protected WDModel(Parcel in) {
        this.id = in.readLong();
        this.withdraw_code = in.readString();
        this.money = in.readString();
        this.money_read = in.readString();
        this.uid = in.readLong();
        this.card_id = in.readString();
        this.why_id = in.readString();
        this.backwhy = in.readString();
        this.audit_time = in.readString();
        this.status = in.readInt();
        long tmpCreatetime = in.readLong();
        this.createtime = tmpCreatetime == -1 ? null : new Date(tmpCreatetime);
    }

    public static final Parcelable.Creator<WDModel> CREATOR = new Parcelable.Creator<WDModel>() {
        public WDModel createFromParcel(Parcel source) {
            return new WDModel(source);
        }

        public WDModel[] newArray(int size) {
            return new WDModel[size];
        }
    };
}
