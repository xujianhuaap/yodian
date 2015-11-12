package maimeng.yodian.app.client.android2;

import android.app.Application;

import maimeng.yodian.app.client.android2.model.Auth;
import maimeng.yodian.app.client.android2.network.Network;

/**
 * Created by android on 2015/11/12.
 */
public class YApplication extends Application {
    //only test
    public static Auth user;
    @Override
    public void onCreate() {
        super.onCreate();
        Network.getOne().init(this);
    }
}
