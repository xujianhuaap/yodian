package maimeng.yodian.app.client.android.network.response;

import maimeng.yodian.app.client.android.model.user.CertifyInfo;

/**
 * Created by xujianhua on 11/9/15.
 */
public class CertifyInfoResponse extends Response{
    private DataNode data;

    public DataNode getData() {
        return data;
    }

    public void setData(DataNode data) {
        this.data = data;
    }

    public final class DataNode{
        private CertifyInfo certifi;

        public CertifyInfo getCertifi() {
            return certifi;
        }

        public void setCertifi(CertifyInfo certifi) {
            this.certifi = certifi;
        }
    }

}
