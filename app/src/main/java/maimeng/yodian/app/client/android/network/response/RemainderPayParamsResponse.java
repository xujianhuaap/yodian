package maimeng.yodian.app.client.android.network.response;

import maimeng.yodian.app.client.android.model.pay.RemainderPayParams;

/**
 * Created by xujianhua on 10/15/15.
 */
public class RemainderPayParamsResponse extends Response{
    private  RemainderPayParams data;

    public RemainderPayParams getData() {
        return data;
    }

    public void setData(RemainderPayParams data) {
        this.data = data;
    }
}
