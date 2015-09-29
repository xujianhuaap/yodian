package maimeng.yodian.app.client.android.network.response;

import maimeng.yodian.app.client.android.model.user.User;

/**
 * Created by android on 2015/8/17.
 */
public class UserInfoResponse extends Response {
    private User.Info data;

    public User.Info getData() {
        return data;
    }

    public void setData(User.Info data) {
        this.data = data;
    }
}
