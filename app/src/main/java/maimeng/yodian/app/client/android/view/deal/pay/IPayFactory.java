package maimeng.yodian.app.client.android.view.deal.pay;

import android.app.Activity;

import java.util.HashMap;
import java.util.Map;

import maimeng.yodian.app.client.android.model.pay.IPayParams;
import maimeng.yodian.app.client.android.model.pay.RemainderPayParams;
import maimeng.yodian.app.client.android.model.pay.WXPayParams;
import maimeng.yodian.app.client.android.model.pay.ZhiFuBaoParams;

/**
 * Created by xujianhua on 10/13/15.
 */
public class IPayFactory {
    public enum Type {
        Alipay,
        Wechat,
        Remainder
    }

    private static Map<Type, IPay> ipays = new HashMap<>();

    public static IPay get(Type type) {
        return ipays.get(type);
    }

    public static IPay createPay(Activity context, Type type, IPayParams payParams, IPayStatus status) {
        if (type == Type.Alipay) {
            ZhiFuBaoPay zhiFuBaoPay = new ZhiFuBaoPay(((ZhiFuBaoParams) payParams).getParams(), context, status);
            ipays.put(type, zhiFuBaoPay);
            return zhiFuBaoPay;
        } else if (type == Type.Wechat) {
            WXPay wxPay = new WXPay((WXPayParams) payParams, context, status);
            ipays.put(type, wxPay);
            return wxPay;
        } else if (type == Type.Remainder) {
            RemainderPay remainderPay = new RemainderPay(((RemainderPayParams) payParams).getOid(), context, status);
            ipays.put(type, remainderPay);
            return remainderPay;
        } else {
            return null;
        }
    }

}
