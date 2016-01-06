package maimeng.yodian.app.client.android.network.response;

import maimeng.yodian.app.client.android.model.Address;

/**
 * Created by xujianhua on 06/01/16.
 */
public class AddressRespoonse extends Response{

    private DataNode data;

    public DataNode getData() {
        return data;
    }

    public void setData(DataNode data) {
        this.data = data;
    }

    public final class DataNode{
        private Address address;

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }
    }
}
