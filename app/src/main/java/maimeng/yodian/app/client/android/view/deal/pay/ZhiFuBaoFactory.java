package maimeng.yodian.app.client.android.view.deal.pay;

import android.app.Activity;
import android.content.Context;

import maimeng.yodian.app.client.android.model.pay.WXPayParams;

/**
 * Created by xujianhua on 10/14/15.
 */
public class ZhiFuBaoFactory implements IPayFactory {
    public static IPay createInstance(Activity context,String payParams,IPayStatus status){
        return new ZhiFuBaoPay(payParams,context,status);
    }
}
