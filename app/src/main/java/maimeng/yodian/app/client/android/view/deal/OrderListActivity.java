package maimeng.yodian.app.client.android.view.deal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.view.AbstractActivity;

/**
 * Created by xujianhua on 9/28/15.
 */
public class OrderListActivity extends AbstractActivity{
    private View mOrderSaled;
    private View mOrderBuyed;
    private TextView mTvOrderSaled;
    private TextView mTvOrderBuyed;
    private View mSaledSelect;
    private View mBuyedSelect;
    private View mGoBack;

    public static void show(Context context,boolean isSell){
        Intent intent=new Intent(context,OrderListActivity.class);
        intent.putExtra("isSell",isSell);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list, false);
        boolean isSell=getIntent().getBooleanExtra("isSell",false);
        mOrderSaled=findViewById(R.id.btn_saled);
        mOrderBuyed=findViewById(R.id.btn_buyed);
        mTvOrderSaled=(TextView)findViewById(R.id.tv_saled);
        mTvOrderBuyed=(TextView)findViewById(R.id.tv_buyed);
        mBuyedSelect=findViewById(R.id.btn_ordered_select);
        mSaledSelect=findViewById(R.id.btn_saled_select);
        mOrderSaled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderFragment.show(OrderListActivity.this,true,R.id.content);
                mSaledSelect.setVisibility(View.VISIBLE);
                mBuyedSelect.setVisibility(View.INVISIBLE);
                mTvOrderBuyed.setTextColor(getResources().getColor(R.color.edit_color));
                mTvOrderSaled.setTextColor(Color.BLACK);

            }
        });
        mOrderBuyed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderFragment.show(OrderListActivity.this,false,R.id.content);
                mSaledSelect.setVisibility(View.INVISIBLE);
                mBuyedSelect.setVisibility(View.VISIBLE);
                mTvOrderBuyed.setTextColor(Color.BLACK);
                mTvOrderSaled.setTextColor(getResources().getColor(R.color.edit_color));
            }
        });

        mGoBack=findViewById(R.id.btn_back);
        mGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if(isSell){
            mOrderSaled.callOnClick();
        }else {
            mOrderBuyed.callOnClick();
        }


    }
}
