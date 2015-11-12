package maimeng.yodian.app.client.android2.network.response;

import maimeng.yodian.app.client.android2.model.Auth;

/**
 * Created by android on 15-7-13.
 */
public class AuthResponse extends Response {
    private Auth data;

    public Auth getData() {
        return data;
    }

    public void setData(Auth data) {
        this.data = data;
    }
}
