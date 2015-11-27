package maimeng.yodian.app.client.android.network.response;

import maimeng.yodian.app.client.android.model.Lottery;

/**
 * Created by xujianhua on 11/27/15.
 */
public class LotteryResponse extends Response{
    private Lottery data;

    public Lottery getData() {
        return data;
    }

    public void setData(Lottery data) {
        this.data = data;
    }
}
