package maimeng.yodian.app.client.android.network.response;

import maimeng.yodian.app.client.android.model.remainder.BindBank;

/**
 * Created by android on 15-9-24.
 */
public class BankBindInfoResponse extends Response {
    public BindBank getData() {
        return data;
    }

    public void setData(BindBank data) {
        this.data = data;
    }

    private BindBank data;
}
