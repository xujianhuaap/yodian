package maimeng.yodian.app.client.android.constants


/**
 * Version 1.0
 * Date: 2015-03-27 17:16
 */
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

    //bank
    public val BANK_LIST: String = "/card/getbank"//获取银行列表
    public val BANK_GETCODE: String = "/card/getcode"//获取验证码
    public val BANK_BIND: String = "/card/bind"//获取验证码
    public val BANK_BIND_INFO: String = "/card/detail"//绑定详情
    public val BANK_UNBIND: String = "/card/unbind"//解除绑定

    //担保交易
    public val VOUCH_APPLY: String = "/vouch/apply"//申请担保
    public val VOUCH_DETAIL: String = "/vouch/detail"//获取担保详情
    public val VOUCH_CANCEL: String = "/vouch/cancel"//取消担保

    //订单
    public val ORDER_LIST_BUYER: String = "/order/buylist"//获取买家列表
    public val ORDER_LIST_SELLER: String = "/order/selllist"//获取卖家列表
    public val ORDER_ACCEPT: String = "/order/accept";//接单
    public val ORDER_SEND:String="/order/send";//发货
    public val ORDER_CONFIRM:String = "/order/receive";//确认收货
    //购买
    public val ORDER_BUY:String="/order/pay";//未付订单购买
    public val SKILL_BUY:String="/skill/buy";//技能购买

}
