package maimeng.yodian.app.client.android.constants

import android.os.Build

import maimeng.yodian.app.client.android.BuildConfig


/**
 * Version 1.0
 * Date: 2015-03-27 17:16
 */
public object ApiConfig {
    public val API_HOST: String
    public val WEIXIN_APP_KEY: String = "wxa8accb138107c861"
    public val WEIXIN_APP_SECRET: String = "030bc9daa9b842fd44fc67ca166b125a"


    public val WEIBO_APP_KEY: String = "1846400889"
    public val QQ_APP_KEY: String = "1104669989"


    public val REDIRECT_URL: String = "http://sns.whalecloud.com/sina2/callback"


    public val SDK_VERSION: Int = Build.VERSION.SDK_INT

    init {
        if (BuildConfig.DEBUG) {
            API_HOST = "http://skilltest.maiquanshop.com/"
        } else {
            API_HOST = "http://skillpre.maiquanshop.com"
            //            API_HOST = "http://skillapi.maiquanshop.com/";
        }
    }

    public object Api {
        public val AUTH_LOGIN: String = "/login/login"//手机号登录
        public val AUTH_GETCODE: String = "/login/getcode"//获取验证码
        public val USER_INFO_UPDATE: String = "/user/info"//上传头像和昵称
        public val PUSH: String = "/user/push"//消息开关设置

        public val SKILL_LIST: String = "/skill/list"//我/他的技能列表
        public val SKILL_CHOICE: String = "/choice/index"//精选技能列表
        public val SKILL_TEMPLATE: String = "/skill/gettemplate"//技能模板列表
        public val SKILL_ADD: String = "/skill/add"//添加技能
        public val SKILL_UPDATE: String = "/skill/edit"//修改技能
        public val SKILL_DELETE: String = "/skill/delete"//删除技能
        public val SKILL_UP: String = "/skill/up"//技能上架下架
        public val RMARK_LIST: String = "/skillcontent/list"//技能日记列表
        public val RMARK_DELETE: String = "/skillcontent/delete"//删除技能日记
        public val RMARK_ADD: String = "/skillcontent/add"//添加日记
        public val REPORT: String = "/index/report"//举报
        public val CHECK_VERSION: String = "/index/checkupdate"//检查更新
        public val USER_INFO: String = "/user/info"//用户信息
        public val QRODE_URL: String = "http://share.yodian.me/"
        public val MONEY_MY: String = "/money/my"//我的余额
        public val MONEY_WITHDRAW: String = "/money/withdraw"//提现申请


    }
}
