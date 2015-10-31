package maimeng.yodian.app.client.android.network.response;

/**
 * Created by xujianhua on 10/14/15.
 */
public class ZhiFuBaoPayResponse extends Response {
    private DataNode data;

    public DataNode getData() {
        return data;
    }

    public void setData(DataNode data) {
        this.data = data;
    }

    public final class DataNode {
        private String params;

        public String getParams() {
            return params;
        }

        public void setParams(String params) {
            this.params = params;
        }
    }
}
