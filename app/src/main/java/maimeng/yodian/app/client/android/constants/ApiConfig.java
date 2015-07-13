package maimeng.yodian.app.client.android.constants;

import android.os.Build;

import maimeng.yodian.app.client.android.BuildConfig;


/**
 * Version 1.0
 * <p/>
 * Date: 2015-03-27 17:16
 * Author: henjue@ketie.me
 */
public class ApiConfig {
    public static final String API_HOST;
    public static final String WEIXIN_APP_KEY = "wxa8accb138107c861";
    public static final String WEIXIN_APP_SECRET = "030bc9daa9b842fd44fc67ca166b125a";





    public static final String WEIBO_APP_KEY = "1947947813";
    public static final String QQ_APP_KEY = "1103508527";

    public static final String REDIRECT_URL = "http://sns.whalecloud.com/sina2/callback";
    public static final String API_HEADIMG;


    public static final int SDK_VERSION= Build.VERSION.SDK_INT;


    static {
        if (BuildConfig.DEBUG) {

            API_HOST = "http://test.maiquanshop.com/";
            API_HEADIMG="http://mmckttest.oss-cn-hangzhou.aliyuncs.com/";


        } else {
            API_HOST = "http://api.maiquanshop.com/";
            API_HEADIMG="http://image.maiquanshop.com/";
        }
    }

    public static class Api {
        public static final String USER_AUTH = "/user/reglogin";//用户授权(登陆或注册)


    }
}
