package maimeng.yodian.app.client.android.network.response;

import maimeng.yodian.app.client.android.model.user.User;

/**
 * Created by android on 15-7-13.
 */
public class AuthResponse extends Response {
    private User data;

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }
}
