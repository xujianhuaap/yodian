package maimeng.yodian.app.client.android.view.deal.pay;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

import maimeng.yodian.app.client.android.R;

/**
 * Created by xujianhua on 10/14/15.
 */
public class ZhiFuBaoPay implements IPay{
    private final String mPayParams;
    private final Activity mActivity;
    private Handler mHandler;

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_CHECK_FLAG = 2;


    public ZhiFuBaoPay(String payParams, Activity activity) {
        this.mPayParams = payParams;
        this.mActivity = activity;
      
    }

    @Override
    public void sendReq() {
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
                            Toast.makeText(mActivity, "支付成功",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // 判断resultStatus 为非“9000”则代表可能支付失败
                            // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                            if (TextUtils.equals(resultStatus, "8000")) {
                                Toast.makeText(mActivity, "支付结果确认中",
                                        Toast.LENGTH_SHORT).show();

                            } else {
                                // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                                Toast.makeText(mActivity, "支付失败",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                        break;
                    }
                    case SDK_CHECK_FLAG: {
                        if((Boolean)msg.obj){
                            excutePay();
                        }else{
                            /*Toast.makeText(mActivity,mActivity.getResources().getString(R.string.),
                                    Toast.LENGTH_SHORT).show(); */
                        }

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
