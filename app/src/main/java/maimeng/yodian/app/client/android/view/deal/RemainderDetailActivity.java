package maimeng.yodian.app.client.android.view.deal;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.view.common.AbstractActivity;
import maimeng.yodian.app.client.android.widget.ListLayoutManager;

/**
 * Created by xujianhua on 05/01/16.
 */
public class RemainderDetailActivity extends AbstractActivity{


    private View title;
    private RecyclerView list;
    private final int titleColums=3;
    private TextView income;
    private TextView fee;
    private TextView total;
    private View totalDivinder;
    private View feeDivinder;
    private View incomeDivinder;
    private RemainderDetailFragment incomeFragment;
    private RemainderDetailFragment feeFragment;
    private RemainderDetailFragment totalFragment;


    public static void show(Activity activity){
        Intent intent=new Intent(activity,RemainderDetailActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void initToolBar(Toolbar toolbar) {
        super.initToolBar(toolbar);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_go_back);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            mTitle.setTextColor(Color.WHITE);
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
        setContentView(R.layout.activity_remainder_detail);
        title=findViewById(R.id.title_containar);
        income = (TextView)findViewById(R.id.title_income);
        fee = (TextView)findViewById(R.id.title_fee);
        total = (TextView)findViewById(R.id.title_total);

        incomeDivinder =findViewById(R.id.divinder_income);
        feeDivinder =findViewById(R.id.divinder_fee);
        totalDivinder =findViewById(R.id.divinder_total);


        final FragmentManager manager=getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction=manager.beginTransaction();
        init(fragmentTransaction);
        income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshTitle(0);
                fragmentTransaction.show(incomeFragment).hide(feeFragment).hide(totalFragment);
            }
        });

        fee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshTitle(1);
                fragmentTransaction.hide(incomeFragment).show(feeFragment).hide(totalFragment);
            }
        });

       total.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               refreshTitle(2);
               fragmentTransaction.hide(incomeFragment).hide(feeFragment).show(totalFragment);
           }
       });




    }


    /***
     *
     */
    private void init( FragmentTransaction transaction) {
        incomeFragment = RemainderDetailFragment.newInstance(0);
        feeFragment = RemainderDetailFragment.newInstance(1);
        totalFragment = RemainderDetailFragment.newInstance(2);
        transaction.add(R.id.content, incomeFragment)
                .add(R.id.content, feeFragment)
                .add(R.id.content, totalFragment).commit();
    }

    /***
     *
     * @param position
     */
    private void refreshTitle(int position){
       if(position==0){
           income.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
           incomeDivinder.setVisibility(View.VISIBLE);
       }else{
           income.setTextColor(getResources().getColor(R.color.colorPrimaryGrey2));
           incomeDivinder.setVisibility(View.INVISIBLE);
       }
        if(position==1){
            fee.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            feeDivinder.setVisibility(View.VISIBLE);
        }else{
            fee.setTextColor(getResources().getColor(R.color.colorPrimaryGrey2));
            feeDivinder.setVisibility(View.INVISIBLE);
        }
        if(position==2){
            total.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            totalDivinder.setVisibility(View.VISIBLE);
        }else{
            total.setTextColor(getResources().getColor(R.color.colorPrimaryGrey2));
            totalDivinder.setVisibility(View.INVISIBLE);
        }
    }

}
