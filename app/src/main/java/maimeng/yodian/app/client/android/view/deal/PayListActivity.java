package maimeng.yodian.app.client.android.view.deal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.model.OrderInfo;
import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.service.BuyService;

/**
 * Created by xujianhua on 10/12/15.
 */
public class PayListActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mTitle;
    private OrderInfo mInfo;
    private BuyService mService;
    private OrderInfo mOrderInfo;

    /***
     *
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
        mOrderInfo = getIntent().getParcelableExtra("orderInfo");
        mTitle=(TextView)findViewById(R.id.pay_title);
        mTitle.setText(Html.fromHtml(getResources().getString(R.string.pay_list_title, mOrderInfo.getTotal_fee())));

        findViewById(R.id.pay_wechat).setOnClickListener(this);
        findViewById(R.id.pay_zhifubao).setOnClickListener(this);
        findViewById(R.id.pay_remainer).setOnClickListener(this);

        mService= Network.getService(BuyService.class);

    }

    /***
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.pay_remainer){

        }else if(v.getId()==R.id.pay_wechat){
            mService.buyOrder(mOrderInfo.getOid(),2,new CallBackProxy());
        }else if(v.getId()==R.id.pay_zhifubao){
            mService.buyOrder(mOrderInfo.getOid(),1,new CallBackProxy());
        }
    }

    public final class CallBackProxy implements Callback<ToastResponse>{
        @Override
        public void start() {

        }

        @Override
        public void success(ToastResponse toastResponse, Response response) {

        }

        @Override
        public void failure(HNetError hNetError) {

        }

        @Override
        public void end() {

        }
    }
}
