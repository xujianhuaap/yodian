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


    public static final String WEIBO_APP_KEY = "1846400889";
    public static final String QQ_APP_KEY = "1104669989";


    public static final String REDIRECT_URL = "http://sns.whalecloud.com/sina2/callback";


    public static final int SDK_VERSION = Build.VERSION.SDK_INT;


    static {
        if (BuildConfig.DEBUG) {
            API_HOST = "http://skilltest.maiquanshop.com/";
        } else {
            API_HOST="http://skillpre.maiquanshop.com";
//            API_HOST = "http://skillapi.maiquanshop.com/";
        }
    }

    public static class Api {
        public static final String AUTH_LOGIN = "/login/login";//手机号登录
        public static final String AUTH_GETCODE = "/login/getcode";//获取验证码


        public static final String USER_INFO_UPDATE = "/user/info";//上传头像和昵称


        public static final String SKILL_LIST = "/skill/list";//我/他的技能列表
        public static final String SKILL_CHOICE = "/choice/index";//精选技能列表
        public static final String SKILL_TEMPLATE = "/skill/gettemplate";//技能模板列表
        public static final String SKILL_ADD = "/skill/add";//添加技能
        public static final String SKILL_UPDATE = "/skill/edit";//修改技能
        public static final String SKILL_DELETE = "/skill/delete";//删除技能
        public static final String SKILL_UP = "/skill/up";//技能上架下架
        public static final String RMARK_LIST = "/skillcontent/list";//技能日记列表
        public static final String RMARK_DELETE = "/skillcontent/delete";//删除技能日记
        public static final String RMARK_ADD = "/skillcontent/add";//添加日记
        public static final String REPORT = "/index/report";//举报
        public static final String CHECK_VERSION = "/index/checkupdate";//检查更新
        public static final String USER_INFO = "/user/info";//用户信息
        public static final String QRODE_URL = "http://share.yodian.me/";


    }
}
