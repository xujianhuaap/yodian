package maimeng.yodian.app.client.android.network.response;

import maimeng.yodian.app.client.android.model.pay.ZhiFuBaoParams;

/**
 * Created by xujianhua on 10/14/15.
 */
public class ZhiFuBaoPayParamsResponse extends Response{
    private ZhiFuBaoParams data;

    public ZhiFuBaoParams getData() {
        return data;
    }

    public void setData(ZhiFuBaoParams data) {
        this.data = data;
    }
}
