package maimeng.yodian.app.client.android.view.deal;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.view.AbstractActivity;

/**
 * Created by xujianhua on 9/28/15.
 */
public class OrderListActivity extends AbstractActivity{
    private View mOrderSaled;
    private View mOrderBuyed;
    private View mSaledSelect;
    private View mBuyedSelect;

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
        setContentView(R.layout.activity_order_list);
        mOrderSaled=findViewById(R.id.btn_saled);
        mOrderBuyed=findViewById(R.id.btn_buyed);
        mBuyedSelect=findViewById(R.id.btn_ordered_select);
        mSaledSelect=findViewById(R.id.btn_saled_select);
        mOrderSaled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderFragment.show(OrderListActivity.this,true,R.id.content);
                mSaledSelect.setVisibility(View.VISIBLE);
                mBuyedSelect.setVisibility(View.INVISIBLE);

            }
        });
        mOrderBuyed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderFragment.show(OrderListActivity.this,false,R.id.content);
                mSaledSelect.setVisibility(View.INVISIBLE);
                mBuyedSelect.setVisibility(View.VISIBLE);
            }
        });

        mOrderSaled.callOnClick();


    }
}
