package maimeng.yodian.app.client.android.network.response;

import java.util.List;

import maimeng.yodian.app.client.android.model.BalanceInfo;

/**
 * Created by xujianhua on 11/01/16.
 */
public class BalanceResponse extends Response{

    private DataNode data;
    public DataNode getData() {
        return data;
    }

    public void setData(DataNode data) {
        this.data = data;
    }

    public final class DataNode{
        private List<BalanceInfo>list;

        public List<BalanceInfo> getList() {
            return list;
        }

        public void setList(List<BalanceInfo> list) {
            this.list = list;
        }
    }
}
