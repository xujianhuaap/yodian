package maimeng.yodian.app.client.android.view.deal.pay;

import android.app.Activity;

import maimeng.yodian.app.client.android.model.pay.WXPayParams;

/**
 * Created by xujianhua on 10/15/15.
 */
public class RemainderFactory {
    public static IPay createInstance(Activity context,String payParams,IPayStatus status){

        return  new RemainderPay(payParams,context,status);
    }
}
