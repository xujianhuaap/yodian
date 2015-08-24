package maimeng.yodian.app.client.android.network.response;

import maimeng.yodian.app.client.android.model.Version;

/**
 * Created by android on 2015/8/24.
 */
public class VersionResponse extends Response {
    private Version data;

    public Version getData() {
        return data;
    }

    public void setData(Version data) {
        this.data = data;
    }
}
