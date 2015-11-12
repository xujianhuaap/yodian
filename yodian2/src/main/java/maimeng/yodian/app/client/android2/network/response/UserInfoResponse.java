package maimeng.yodian.app.client.android2.network.response;

import maimeng.yodian.app.client.android2.model.UserInfo;

/**
 * Created by android on 2015/11/12.
 */
public class UserInfoResponse extends Response {
    private UserInfo data;

    public UserInfo getData() {
        return data;
    }

    public void setData(UserInfo data) {
        this.data = data;
    }
}
