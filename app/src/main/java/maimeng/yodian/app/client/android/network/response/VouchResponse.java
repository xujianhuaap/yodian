package maimeng.yodian.app.client.android.network.response;

import maimeng.yodian.app.client.android.model.Vouch;

/**
 * Created by xujianhua on 9/25/15.
 */
public class VouchResponse extends Response{

    private Vouch data;

    public Vouch getData() {
        return data;
    }

    public void setData(Vouch data) {
        this.data = data;
    }
}
