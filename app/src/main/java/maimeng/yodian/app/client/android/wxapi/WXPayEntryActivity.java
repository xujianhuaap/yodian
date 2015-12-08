package maimeng.yodian.app.client.android.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.sina.weibo.sdk.constant.WBConstants;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import maimeng.yodian.app.client.android.BuildConfig;
import maimeng.yodian.app.client.android.view.deal.pay.IPay;
import maimeng.yodian.app.client.android.view.deal.pay.IPayFactory;
import maimeng.yodian.app.client.android.view.deal.pay.IPayStatus;
import maimeng.yodian.app.client.android.view.deal.pay.WXPay;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
    private static final int COMMAND_PAY_BY_WX = 5;

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        api = WXAPIFactory.createWXAPI(this, BuildConfig.WEIXIN_APP_KEY);

        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {

        if (resp.getType() == COMMAND_PAY_BY_WX) {
            IPay pay = IPayFactory.get(IPayFactory.Type.Wechat);
            IPayStatus status = null;
            if (pay != null) {
                if (pay instanceof WXPay) {
                    status = ((WXPay) pay).getmStatus();

                    if (resp.errCode == BaseResp.ErrCode.ERR_OK) {

                        status.sucessPay(IPayStatus.PAY_SUCESS);
                    } else  if(resp.errCode==BaseResp.ErrCode.ERR_USER_CANCEL){
                        status.failurepay(IPayStatus.PAY_ERROR_CANCEL_USER);
                    }else {

                        status.failurepay(IPayStatus.PAY_ERROR_ELSE);

                    }
                }
            }

        }
        finish();

    }
}