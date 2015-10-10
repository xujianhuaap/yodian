package maimeng.yodian.app.client.android.view.deal;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.databinding.ActivityOrderDetailBinding;
import maimeng.yodian.app.client.android.model.OrderInfo;
import maimeng.yodian.app.client.android.view.AbstractActivity;


/**
 * Created by xujianhua on 10/10/15.
 */
public class OrderDetailActivity extends AbstractActivity {

    private ActivityOrderDetailBinding mBinding;


    public static void show(Context context,OrderInfo orderInfo,boolean isSaled){
        Intent intent=new Intent(context,OrderDetailActivity.class);
        intent.putExtra("orderInfo",orderInfo);
        intent.putExtra("isSaled", isSaled);
        context.startActivity(intent);
    }

    @Override
    protected void initToolBar(Toolbar toolbar) {
        super.initToolBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_go_back);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= bindView( R.layout.activity_order_detail);
        OrderInfo info=getIntent().getParcelableExtra("orderInfo");
        boolean isSaled=getIntent().getBooleanExtra("isSaled",false);
        mBinding.setOrderInfo(info);

        if(isSaled){
            mBinding.orderBuyer.setText(Html.fromHtml(getResources().getString(R.string.order_buyer_name,info.getBuyer().getNickname())));
            mBinding.orderTotalFee.setText(Html.fromHtml(getResources().getString(R.string.order_saled_total_fee ,info.getTotal_fee())));
        }else{
            mBinding.orderBuyer.setText(Html.fromHtml(getResources().getString(R.string.order_seller_name,info.getSkill().getNickname())));
            mBinding.orderTotalFee.setText(Html.fromHtml(getResources().getString(R.string.order_buyed_total_fee,info.getTotal_fee())));
        }

        int status=Integer.parseInt(info.getStatus());
        switch (status){
            case 0:
                //未支付
                break;
            case 1:
                break;
            case 2:
                //支付

                break;
            case 3:
                //
                break;

        }
        mBinding.orderNumber.setText(Html.fromHtml(getResources().getString(R.string.order_number,info.getNumber())));
        mBinding.orderSkillPrice.setText(Html.fromHtml(getResources().getString(R.string.order_skill_price,info.getSkill().getPrice(),info.getSkill().getUnit())));
    }
}
