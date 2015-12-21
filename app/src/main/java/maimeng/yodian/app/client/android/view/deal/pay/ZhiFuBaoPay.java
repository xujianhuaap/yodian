package maimeng.yodian.app.client.android.view.deal.pay;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.umeng.analytics.MobclickAgent;

import maimeng.yodian.app.client.android.common.UEvent;

/**
 * Created by xujianhua on 10/14/15.
 */
public class ZhiFuBaoPay implements IPay{
    private final String mPayParams;
    private final Activity mActivity;
    private final IPayStatus mstatus;
    private Handler mHandler;

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_CHECK_FLAG = 2;


    public ZhiFuBaoPay(String payParams, Activity activity,IPayStatus status) {
        this.mPayParams = payParams;
        this.mActivity = activity;
        this.mstatus=status;
      
    }

    @Override
    public void sendReq() {
        MobclickAgent.onEvent(mActivity, UEvent.ENTRY_PAY_ZHIFUBAO);
        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case SDK_PAY_FLAG: {
                        PayResult payResult = new PayResult((String) msg.obj);

                        // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                        String resultInfo = payResult.getResult();

                        String resultStatus = payResult.getResultStatus();

                        // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                        if (TextUtils.equals(resultStatus, "9000")) {
                            MobclickAgent.onEvent(mActivity, UEvent.PAY_ZHIFUBAO_SUBMIT);
                            mstatus.sucessPay(IPayStatus.PAY_SUCESS);
                        } else {
                            MobclickAgent.onEvent(mActivity, UEvent.PAY_ZHIFUBAO_CANCEL);
                            mstatus.failurepay(IPayStatus.PAY_ERROR_ELSE);

                        }
                        break;
                    }
                    case SDK_CHECK_FLAG: {
//                        if((Boolean)msg.obj){
//                        }else{
//                            Spanned checkStr=Html.fromHtml(mActivity.getResources().getString(R.string.pay_check_account));
//                            Toast.makeText(mActivity,checkStr,
//                                    Toast.LENGTH_SHORT).show();
//                        }
                        excutePay();
                        break;
                    }
                    default:
                        break;
                }
            }
        };
        checkIsExist();


    }

    /***
     * 支付
     */
    private void excutePay() {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(mActivity);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(mPayParams);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /***
     *检查账户是否存在
     */
    private void checkIsExist() {
        Runnable checkRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask payTask = new PayTask(mActivity);
                // 调用查询接口，获取查询结果
                boolean isExist = payTask.checkAccountIfExist();

                Message msg = new Message();
                msg.what = SDK_CHECK_FLAG;
                msg.obj = isExist;
                mHandler.sendMessage(msg);
            }
        };

        Thread checkThread = new Thread(checkRunnable);
        checkThread.start();
    }
}
