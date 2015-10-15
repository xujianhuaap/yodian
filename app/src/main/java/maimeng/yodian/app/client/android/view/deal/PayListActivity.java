package maimeng.yodian.app.client.android.view.deal;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.HNet;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;
import org.json.JSONObject;

import maimeng.yodian.app.client.android.BuildConfig;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.chat.activity.AlertDialog;
import maimeng.yodian.app.client.android.model.OrderInfo;
import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.common.GsonConverter;
import maimeng.yodian.app.client.android.network.common.RequestIntercept;
import maimeng.yodian.app.client.android.network.response.RemainderPayParamsResponse;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.response.WXPayParamResponse;
import maimeng.yodian.app.client.android.network.response.ZhiFuBaoPayResponse;
import maimeng.yodian.app.client.android.network.service.BuyService;
import maimeng.yodian.app.client.android.utils.LogUtil;
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
                mService.buyOrder(mOrderInfo.getOid(),3,new CallBackProxy(3));
            }else if(v.getId()==R.id.pay_wechat){
                mService.buyOrder(mOrderInfo.getOid(), 2, new CallBackProxy(2));
            }else if(v.getId()==R.id.pay_zhifubao){
                mService.buyOrder(mOrderInfo.getOid(),1,new CallBackProxy(1));
            }
        }else {
            if(v.getId()==R.id.pay_remainer){
                mService.buySkill(mSkill.getId(), 3, new CallBackProxy(3));
            }else if(v.getId()==R.id.pay_wechat){
                mService.buySkill(mSkill.getId(), 2, new CallBackProxy(2));
            }else if(v.getId()==R.id.pay_zhifubao){
                mService.buySkill(mSkill.getId(),1,new CallBackProxy(1));
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
            //执行支付
             final Gson gson=new Gson();
             IPay pay=null;
             final IPayStatus status=new PayStatus();
            if(payType==1){
                ZhiFuBaoPayResponse zhiFuBaoPayResponse=gson.fromJson(s, ZhiFuBaoPayResponse.class);
                pay= ZhiFuBaoFactory.createInstance(PayListActivity.this,zhiFuBaoPayResponse.getData().getParams(),status);
                pay.sendReq();
            }else if(payType==2){
                WXPayParamResponse paramResponse=gson.fromJson(s, WXPayParamResponse.class);
                pay= WXFactory.createInstance(PayListActivity.this,paramResponse.getData().getParams(),status);
                pay.sendReq();
            }else if(payType==3){

                new ViewDialog.Builder(PayListActivity.this).setMesage(getResources().getString(R.string.pay_deal_tip))
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
                },"").create().show();

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
        public void failurepay() {
            Spanned fail = Html.fromHtml(getResources().getString(R.string.pay_result_fail));
            String btnTip=getResources().getString(R.string.btn_name);
            String title=getResources().getString(R.string.pay_deal_title);
            if(isOrderPay){
                new ViewDialog.Builder(PayListActivity.this).setMesage(fail.toString())
                        .setTitle(title).setPositiveListener(new ViewDialog.IPositiveListener() {
                    @Override
                    public void positiveClick() {
                        finish();
                    }
                }   ,btnTip).create().show();

            }else{
                finish();
            }


        }

        @Override
        public void sucessPay() {
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
