package maimeng.yodian.app.client.android.model.remainder;

import org.parceler.Parcel;

@Parcel(value = Parcel.Serialization.BEAN)
public class Remainder {

    /**
     * money : 0.00
     * withdraw : 0.00
     * during : 0.00
     * draw_account :
     * readicon : 0
     */

    private float money;
    private float withdraw;
    private float during;
    private float freeze;
    private String draw_account;
    private int readicon;
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getFreeze() {
        return freeze;
    }

    public void setFreeze(float freeze) {
        this.freeze = freeze;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public void setWithdraw(float withdraw) {
        this.withdraw = withdraw;
    }

    public void setDuring(float during) {
        this.during = during;
    }

    public void setDraw_account(String draw_account) {
        this.draw_account = draw_account;
    }

    public void setReadicon(int readicon) {
        this.readicon = readicon;
    }

    public float getMoney() {
        return money;
    }

    public float getWithdraw() {
        return withdraw;
    }

    public float getDuring() {
        return during;
    }

    public String getDraw_account() {
        return draw_account;
    }

    public int getReadicon() {
        return readicon;
    }


    public Remainder() {
    }

}
