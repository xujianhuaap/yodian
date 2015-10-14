package maimeng.yodian.app.client.android.view.deal.pay;

import android.app.Activity;
import android.content.Context;

import maimeng.yodian.app.client.android.model.pay.IPayParams;
import maimeng.yodian.app.client.android.model.pay.WXPayParams;

/**
 * Created by xujianhua on 10/13/15.
 */
public class WXFactory implements IPayFactory{
    public static IPay pay;
    public static IPay createInstance(Activity context,WXPayParams payParams,IPayStatus status){
        pay=new WXPay(payParams,context,status);
        return pay;
    }
}
