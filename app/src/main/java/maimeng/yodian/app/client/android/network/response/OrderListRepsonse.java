package maimeng.yodian.app.client.android.network.response;

import java.util.List;

import maimeng.yodian.app.client.android.model.OrderInfo;

/**
 * Created by xujianhua on 9/29/15.
 */
public class OrderListRepsonse extends Response{
    private DataNode data;

    public DataNode getData() {
        return data;
    }

    public void setData(DataNode data) {
        this.data = data;
    }

    public final class DataNode{
        private List<OrderInfo> list;

        public List<OrderInfo> getList() {
            return list;
        }

        public void setList(List<OrderInfo> list) {
            this.list = list;
        }
    }
}
