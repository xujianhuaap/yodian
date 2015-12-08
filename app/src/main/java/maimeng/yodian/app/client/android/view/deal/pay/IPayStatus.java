package maimeng.yodian.app.client.android.view.deal.pay;

/**
 * Created by xujianhua on 10/14/15.
 */
public interface IPayStatus {
    public static final int PAY_ERROR_REMAINDER_SHORT=0x12;
    public static final int PAY_ERROR_ELSE=0x14;
    public static final int PAY_SUCESS=0x13;
    public static final int PAY_ERROR_CANCEL_USER=0x15;

    public void sucessPay(int errCode);
    public void failurepay(int errCode);
}
