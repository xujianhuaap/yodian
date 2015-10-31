package maimeng.yodian.app.client.android.network.response;

import maimeng.yodian.app.client.android.model.pay.WXPayParams;

/**
 * Created by xujianhua on 10/13/15.
 */
public class WXPayParamResponse {
    private DataNode data;

    public DataNode getData() {
        return data;
    }

    public void setData(DataNode data) {
        this.data = data;
    }

    public final class DataNode {
        private WXPayParams params;

        public long getOid() {
            return oid;
        }

        public void setOid(long oid) {
            this.oid = oid;
        }

        private long oid;

        public WXPayParams getParams() {
            return params;
        }

        public void setParams(WXPayParams params) {
            this.params = params;
        }
    }
}
