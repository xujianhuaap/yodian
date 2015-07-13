package maimeng.yodian.app.client.android;

import android.app.Application;

import org.henjue.library.share.ShareSDK;

import maimeng.yodian.app.client.android.constants.ApiConfig;
import maimeng.yodian.app.client.android.network.Network;

/**
 * Created by android on 15-7-13.
 */
public class YApplicaton extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Network.getOne().init(this);
        ShareSDK.getInstance().initShare(ApiConfig.WEIXIN_APP_KEY, ApiConfig.WEIBO_APP_KEY, ApiConfig.QQ_APP_KEY, "67100dc9c7e8e8dd6fed148b37b3f0f0", ApiConfig.REDIRECT_URL);
    }
}
