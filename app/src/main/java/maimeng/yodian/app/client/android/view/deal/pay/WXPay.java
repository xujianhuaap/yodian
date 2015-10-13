package maimeng.yodian.app.client.android.view.deal.pay;

import android.content.Context;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import maimeng.yodian.app.client.android.BuildConfig;
import maimeng.yodian.app.client.android.model.pay.IPayParams;
import maimeng.yodian.app.client.android.model.pay.WXPayParams;

/**
 * Created by xujianhua on 10/13/15.
 */
public class WXPay implements IPay{
    private final WXPayParams params;
    private final Context context;
    private final IWXAPI api;
    private  PayReq req;

    public WXPay(WXPayParams params, Context context) {
        this.params = params;
        this.context = context;
        api = WXAPIFactory.createWXAPI(context, null);
        api.registerApp(BuildConfig.WEIXIN_APP_KEY);

    }

    /***
     * 设置PayReq
     *
     */
    private void genReq() {

        req = new PayReq();
        req.appId=params.getAppid();
        req.nonceStr=params.getNoncestr();
        req.packageValue=params.getPackageX();
        req.partnerId=params.getPartnerid();
        req.prepayId=params.getPrepayid();
        req.sign=params.getSign();
        req.timeStamp=params.getTimestamp()+"";
    }

    /***
     * 请求支付
     */
    @Override
    public void sendReq() {
        genReq();
        api.sendReq(req);
    }


}
