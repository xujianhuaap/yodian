package maimeng.yodian.app.client.android.network.response;

import maimeng.yodian.app.client.android.model.OrderInfo;

/**
 * Created by android on 2015/10/31.
 */
public class OrderInfoResponse extends Response {
    private OrderInfo data;

    public OrderInfo getData() {
        return data;
    }

    public void setData(OrderInfo data) {
        this.data = data;
    }
}
