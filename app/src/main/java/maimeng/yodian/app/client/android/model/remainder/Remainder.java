package maimeng.yodian.app.client.android.model.remainder;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by android on 2015/9/21.
 */
public class Remainder implements Parcelable {

    /**
     * money : 0.00
     * withdraw : 0.00
     * during : 0.00
     * draw_account :
     * readicon : 0
     */

    private String money;
    private String withdraw;
    private String during;
    private String draw_account;
    private int readicon;

    public void setMoney(String money) {
        this.money = money;
    }

    public void setWithdraw(String withdraw) {
        this.withdraw = withdraw;
    }

    public void setDuring(String during) {
        this.during = during;
    }

    public void setDraw_account(String draw_account) {
        this.draw_account = draw_account;
    }

    public void setReadicon(int readicon) {
        this.readicon = readicon;
    }

    public String getMoney() {
        return money;
    }

    public String getWithdraw() {
        return withdraw;
    }

    public String getDuring() {
        return during;
    }

    public String getDraw_account() {
        return draw_account;
    }

    public int getReadicon() {
        return readicon;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.money);
        dest.writeString(this.withdraw);
        dest.writeString(this.during);
        dest.writeString(this.draw_account);
        dest.writeInt(this.readicon);
    }

    public Remainder() {
    }

    protected Remainder(Parcel in) {
        this.money = in.readString();
        this.withdraw = in.readString();
        this.during = in.readString();
        this.draw_account = in.readString();
        this.readicon = in.readInt();
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
