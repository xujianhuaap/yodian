package maimeng.yodian.app.client.android.network.response;

import maimeng.yodian.app.client.android.model.ModifyUser;

/**
 * Created by android on 15-7-14.
 */
public class ModifyUserResponse extends Response {
    private ModifyUser data;

    public ModifyUser getData() {
        return data;
    }

    public void setData(ModifyUser data) {
        this.data = data;
    }
}
