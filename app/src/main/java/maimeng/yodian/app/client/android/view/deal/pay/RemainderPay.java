package maimeng.yodian.app.client.android.view.deal.pay;

import android.app.Activity;
import android.content.Context;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.HNet;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import maimeng.yodian.app.client.android.BuildConfig;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.common.RequestIntercept;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.service.BuyService;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.view.dialog.WaitDialog;

/**
 * Created by xujianhua on 10/15/15.
 */
public class RemainderPay implements IPay{
    private String mOid;
    private Activity mContext;
    private IPayStatus mStatus;
    private BuyService mService;
    private WaitDialog mWaitDialog;

    public RemainderPay(String oid, Activity context,IPayStatus status) {
        this.mOid = oid;
        this.mContext = context;
        this.mStatus=status;
        mService= Network.getService((BuyService.class));
    }

    @Override
    public void sendReq() {
        Callback<ToastResponse> callback=new Callback<ToastResponse>() {
            @Override
            public void start() {

                mWaitDialog=WaitDialog.show(mContext);
            }

            @Override
            public void success(ToastResponse toastResponse, Response response) {

                if(toastResponse.getCode()==20000){
                    mStatus.sucessPay(IPayStatus.PAY_SUCESS);
                }else {
                    int errCode=IPayStatus.PAY_ERROR_ELSE;
                    if(toastResponse.getCode()==55000){
                        errCode=IPayStatus.PAY_ERROR_REMAINDER_SHORT;
                    }

                    mStatus.failurepay(errCode);


                }
            }

            @Override
            public void failure(HNetError hNetError) {
                ErrorUtils.checkError(mContext,hNetError);
            }

            @Override
            public void end() {
                mWaitDialog.dismiss();
            }
        };
        mService.remainderPay(mOid,callback);
    }
}
