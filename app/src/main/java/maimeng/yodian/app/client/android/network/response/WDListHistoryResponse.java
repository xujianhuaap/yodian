package maimeng.yodian.app.client.android.network.response;

import maimeng.yodian.app.client.android.model.remainder.WDList;

/**
 * Created by android on 2015/10/21.
 */
public class WDListHistoryResponse extends Response {
    private WDList data;

    public WDList getData() {
        return data;
    }

    public void setData(WDList data) {
        this.data = data;
    }
}
