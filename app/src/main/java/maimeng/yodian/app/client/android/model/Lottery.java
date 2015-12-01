package maimeng.yodian.app.client.android.model;

import org.parceler.Parcel;

/**
 * Created by xujianhua on 11/27/15.
 */
@Parcel(value = Parcel.Serialization.BEAN)
public class Lottery {
    private int isLottery;
    private String lotUrl;
    private long oid;

    public long getOid() {
        return oid;
    }

    public void setOid(long oid) {
        this.oid = oid;
    }

    public int getIsLottery() {
        return isLottery;
    }

    public void setIsLottery(int isLottory) {
        this.isLottery = isLottory;
    }

    public String getLotUrl() {
        return lotUrl;
    }

    public void setLotUrl(String lotUrl) {
        this.lotUrl = lotUrl;
    }
}
