package maimeng.yodian.app.client.android.network.response;

import maimeng.yodian.app.client.android.model.Remainder;

/**
 * Created by android on 2015/9/21.
 */
public class RemainderResponse extends Response {
    private Remainder data;

    public Remainder getData() {
        return data;
    }

    public void setData(Remainder data) {
        this.data = data;
    }
}
