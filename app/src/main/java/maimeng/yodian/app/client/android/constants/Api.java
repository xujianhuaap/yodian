package maimeng.yodian.app.client.android.constants;

/**
 * Created by android on 2015/10/12.
 */
public class Api {
    public static final String AUTH_LOGIN = "/login/login";//手机号登录
    public static final String AUTH_GETCODE = "/login/getcode";//获取验证码
    public static final String USER_INFO_UPDATE = "/user/info";//上传头像和昵称
    public static final String PUSH = "/user/push";//消息开关设置
    public static final String FLOAT = "/index/getfloatad";//获取弹窗图片

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
    public static final String GET_USER_INFO = "/user/getinfo";//用户信息
    public static final String QRODE_URL = "http://share.yodian.me/";


    public static final String MONEY_MY = "/money/my";//我的余额
    public static final String MONEY_WITHDRAW = "/money/withdraw";//提现申请
    public static final String MONEY_WDLIST = "/money/wdlist";//提现历史

    //bank
    public static final String BANK_LIST
            = "/card/getbank";//获取银行列表
    public static final String BANK_GETCODE
            = "/card/getcode";//获取验证码
    public static final String BANK_BIND
            = "/card/bind";//获取验证码
    public static final String BANK_BIND_INFO
            = "/card/detail";//绑定详情
    public static final String BANK_UNBIND
            = "/card/unbind";//解除绑定

    //担保交易
    public static final String VOUCH_APPLY
            = "/vouch/apply";//申请担保
    public static final String VOUCH_DETAIL
            = "/vouch/detail";//获取担保详情
    public static final String VOUCH_CANCEL
            = "/vouch/cancel";//取消担保

    //订单
    public static final String ORDER_LIST_BUYER = "/order/buylist";//获取买家列表
    public static final String ORDER_LIST_SELLER = "/order/selllist";//获取卖家列表
    public static final String ORDER_ACCEPT = "/order/accept";//接单
    public static final String ORDER_SEND = "/order/send";//发货
    public static final String ORDER_CONFIRM = "/order/receive";//确认收货
    public static final String ORDER_INFO = "/order/getorderinfo";//订单详情


    //购买
    public static final String ORDER_BUY = "/order/pay";//未付订单购买
    public static final String ORDER_REMAINDER_BUY = "/order/balancepay";//订单余额支付
    public static final String SKILL_BUY = "/skill/buy";//技能购买

    //basic info
    public static final String INFO_CODE="/user/getcode";//个人手机验证码
    public static final String INFO_CERTIFY="/user/certifi";//个人信息验证
    public static final String INFO_GET="/user/getcertifi";//获得个人认证信息

}
