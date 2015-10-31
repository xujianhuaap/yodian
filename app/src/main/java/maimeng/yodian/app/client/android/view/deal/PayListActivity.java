package maimeng.yodian.app.client.android.view.deal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.HNet;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import maimeng.yodian.app.client.android.BuildConfig;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.model.OrderInfo;
import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.common.RequestIntercept;
import maimeng.yodian.app.client.android.network.response.RemainderPayParamsResponse;
import maimeng.yodian.app.client.android.network.response.WXPayParamResponse;
import maimeng.yodian.app.client.android.network.response.ZhiFuBaoPayParamsResponse;
import maimeng.yodian.app.client.android.network.service.BuyService;
import maimeng.yodian.app.client.android.view.deal.pay.IPay;
import maimeng.yodian.app.client.android.view.deal.pay.IPayStatus;
import maimeng.yodian.app.client.android.view.deal.pay.RemainderFactory;
import maimeng.yodian.app.client.android.view.deal.pay.WXFactory;
import maimeng.yodian.app.client.android.view.deal.pay.ZhiFuBaoFactory;
import maimeng.yodian.app.client.android.view.dialog.ViewDialog;
import maimeng.yodian.app.client.android.view.dialog.WaitDialog;

/**
 * Created by xujianhua on 10/12/15.
 */
public class PayListActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView mTitle;
    private OrderInfo mInfo;
    private Skill mSkill;
    private BuyService mService;
    private OrderInfo mOrderInfo;
    private boolean isOrderPay;
    private WaitDialog mDialog;

    private final static int PAY_TYPE_ZHIFUBAO=1;
    private final static int PAY_TYPE_REMAINDER=3;
    private final static int PAY_TYPE_WECHAT=2;

    /***
     *订单支付
     * @param context
     */
    public  static void show(Activity context,OrderInfo orderInfo,int requestCode){
        Intent intent=new Intent(context,PayListActivity.class);
        intent.putExtra("orderInfo", orderInfo);
        context.startActivityForResult(intent,requestCode);
    }

    /***
     * 技能购买
     * @param context
     * @param orderInfo
     * @param requestCode
     */
    public  static void show(Activity context,Skill orderInfo,int requestCode){
        Intent intent=new Intent(context,PayListActivity.class);
        intent.putExtra("skill", orderInfo);
        context.startActivityForResult(intent,requestCode);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setGravity(Gravity.BOTTOM);
        setContentView(R.layout.activity_pay_list);

        mTitle=(TextView)findViewById(R.id.pay_title);
        findViewById(R.id.pay_wechat).setOnClickListener(this);
        findViewById(R.id.pay_zhifubao).setOnClickListener(this);
        findViewById(R.id.pay_remainer).setOnClickListener(this);

        Intent intent=getIntent();
        if(intent.hasExtra("orderInfo")){
            mOrderInfo = intent.getParcelableExtra("orderInfo");
            mTitle.setText(Html.fromHtml(getResources().getString(R.string.pay_list_title, mOrderInfo.getTotal_fee())));
            isOrderPay=true;
        }else{
            mSkill=intent.getParcelableExtra("skill");
            mTitle.setText(Html.fromHtml(getResources().getString(R.string.pay_list_title, mSkill.getPrice())));
        }


        HNet net = new HNet.Builder()
                .setEndpoint(BuildConfig.API_HOST)
                .setIntercept(new RequestIntercept(this))
                .build();
        mService= net.create(BuyService.class);


    }



    /***
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        //生成订单信息
        if(isOrderPay){
            if(v.getId()==R.id.pay_remainer){
                mService.buyOrder(mOrderInfo.getOid(),PAY_TYPE_REMAINDER,new CallBackProxy(PAY_TYPE_REMAINDER));
            }else if(v.getId()==R.id.pay_wechat){
                mService.buyOrder(mOrderInfo.getOid(), PAY_TYPE_WECHAT, new CallBackProxy(PAY_TYPE_WECHAT));
            }else if(v.getId()==R.id.pay_zhifubao){
                mService.buyOrder(mOrderInfo.getOid(),PAY_TYPE_ZHIFUBAO,new CallBackProxy(PAY_TYPE_ZHIFUBAO));
            }
        }else {
            if(v.getId()==R.id.pay_remainer){
                mService.buySkill(mSkill.getId(), PAY_TYPE_REMAINDER, new CallBackProxy(PAY_TYPE_REMAINDER));
            }else if(v.getId()==R.id.pay_wechat){
                mService.buySkill(mSkill.getId(),  PAY_TYPE_WECHAT, new CallBackProxy(PAY_TYPE_WECHAT));
            }else if(v.getId()==R.id.pay_zhifubao){
                mService.buySkill(mSkill.getId(),PAY_TYPE_ZHIFUBAO,new CallBackProxy(PAY_TYPE_ZHIFUBAO));
            }
        }

    }


    /***
     *
     */
    public final class CallBackProxy implements Callback<String>{
        private final int payType;

        public CallBackProxy(int payType) {
            this.payType = payType;
        }

        @Override
        public void start() {
            mDialog=WaitDialog.show(PayListActivity.this);
        }

        @Override
        public void success(final  String s, Response response) {
             final Gson gson=Network.getOne().getGson();
             IPay pay=null;
             final IPayStatus status=new PayStatus();
            if(payType==PAY_TYPE_ZHIFUBAO){

                ZhiFuBaoPayParamsResponse zhiFuBaoPayParamsResponse =gson.fromJson(s, ZhiFuBaoPayParamsResponse.class);
                pay= ZhiFuBaoFactory.createInstance(PayListActivity.this, zhiFuBaoPayParamsResponse.getData().getParams(),status);
                pay.sendReq();
            }else if(payType==PAY_TYPE_WECHAT){
                WXPayParamResponse paramResponse=gson.fromJson(s, WXPayParamResponse.class);
                pay= WXFactory.createInstance(PayListActivity.this,paramResponse.getData().getParams(),status);
                pay.sendReq();
            }else if(payType==PAY_TYPE_REMAINDER){

                 final android.support.v7.app.AlertDialog alertDialog=new ViewDialog.Builder(PayListActivity.this).setMesage(getResources().getString(R.string.pay_deal_tip))
                        .setPositiveListener(new ViewDialog.IPositiveListener() {
                            @Override
                            public void positiveClick() {
                                RemainderPayParamsResponse remainderPayParamsResponse=gson.fromJson(s, RemainderPayParamsResponse.class);
                                IPay pay=RemainderFactory.createInstance(PayListActivity.this,remainderPayParamsResponse.getData().getOid(),status);
                                pay.sendReq();
                            }
                        },"").setNegtiveListener(new ViewDialog.INegativeListener() {
                    @Override
                    public void negtiveClick() {
                        finish();
                    }
                },"").create();
                alertDialog.show();

            }




        }

        @Override
        public void failure(HNetError hNetError) {
            ErrorUtils.checkError(PayListActivity.this,hNetError);
        }

        @Override
        public void end() {
            mDialog.dismiss();
        }
    }

    /***
     * 支付结果
     */
    public class PayStatus implements IPayStatus{
        @Override
        public void failurepay(int errCode) {
            Spanned fail = Html.fromHtml(getResources().getString(R.string.pay_result_fail));
            String failStr=fail.toString();
            if(errCode==IPayStatus.PAY_ERROR_REMAINDER_SHORT){
                failStr=getResources().getString(R.string.pay_deal_remainder_shortage);
            }
            String btnTip=getResources().getString(R.string.btn_name);
            String title=getResources().getString(R.string.pay_deal_title);

            new ViewDialog.Builder(PayListActivity.this).setMesage(failStr)
                    .setTitle(title).setPositiveListener(new ViewDialog.IPositiveListener() {
                @Override
                public void positiveClick() {
                    finish();
                }
            }   ,btnTip).create().show();



        }

        @Override
        public void sucessPay(int errCode) {
            Spanned sucess = Html.fromHtml(getResources().getString(R.string.pay_result_sucess));
            String btnTip=getResources().getString(R.string.btn_name);
            String title=getResources().getString(R.string.pay_deal_title);
            new ViewDialog.Builder(PayListActivity.this).setMesage(sucess.toString())
                    .setTitle(title).setPositiveListener(new ViewDialog.IPositiveListener() {
                        @Override
                        public void positiveClick() {
                            finish();
                        }
                    },btnTip).create().show();
        }
    }
}
