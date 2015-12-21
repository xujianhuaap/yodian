package maimeng.yodian.app.client.android.view.deal.pay;

import android.app.Activity;
import android.content.Context;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.android.Config;

import maimeng.yodian.app.client.android.BuildConfig;
import maimeng.yodian.app.client.android.model.pay.IPayParams;
import maimeng.yodian.app.client.android.model.pay.WXPayParams;
import maimeng.yodian.app.client.android.utils.LogUtil;

/**
 * Created by xujianhua on 10/13/15.
 */
public class WXPay implements IPay{
    private final WXPayParams params;
    private final Activity context;
    private final IWXAPI api;
    private final IPayStatus mStatus;
    private  PayReq req;

    public IPayStatus getmStatus() {
        return mStatus;
    }

    public WXPay(WXPayParams params, Activity context,IPayStatus status) {
        this.params = params;
        this.context = context;
        this.mStatus=status;
        api = WXAPIFactory.createWXAPI(context, BuildConfig.WEIXIN_APP_KEY);
        api.registerApp(BuildConfig.WEIXIN_APP_KEY);


    }

    /***
     * 设置PayReq
     *
     */
    private void genReq() {

        req = new PayReq();
        if(params!=null){
            req.appId=params.getAppid();
            req.nonceStr=params.getNoncestr();
            req.packageValue=params.getPackageX();
            req.partnerId=params.getPartnerid();
            req.prepayId=params.getPrepayid();
            req.sign=params.getSign();
            req.timeStamp=params.getTimestamp()+"";
            LogUtil.d(WXPay.class.getName(),"WXPay params:"+params.toString());
        }

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
