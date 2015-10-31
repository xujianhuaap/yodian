package maimeng.yodian.app.client.android.view.deal.pay;

import android.app.Activity;

/**
 * Created by xujianhua on 10/15/15.
 */
public class RemainderFactory {
    public static IPay createInstance(Activity context, long oid, IPayStatus status) {
        return new RemainderPay(oid, context, status);
    }
}
