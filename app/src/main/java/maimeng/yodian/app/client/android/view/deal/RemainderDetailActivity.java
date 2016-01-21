package maimeng.yodian.app.client.android.view.deal;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
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

import com.viewpagerindicator.IconPageIndicator;

import java.util.ArrayList;
import java.util.List;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.view.common.AbstractActivity;
import maimeng.yodian.app.client.android.widget.AutoScrollViewPager;
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
    private ViewPager viewPager;
    private List<RemainderDetailFragment>fragments;
    private FragmentrAdapter fragmentrAdapter;

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

        viewPager=(ViewPager)findViewById(R.id.content);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                refreshTitle(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        initFragment();
        fragmentrAdapter = new FragmentrAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(fragmentrAdapter);

        income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshTitle(1);
                viewPager.setCurrentItem(1);
            }
        });

        fee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshTitle(2);
                viewPager.setCurrentItem(2);
            }
        });

       total.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               refreshTitle(0);
               viewPager.setCurrentItem(0);
           }
       });

        total.callOnClick();




    }


    /***
     *
     */
    private void initFragment( ) {
        fragments=new ArrayList<RemainderDetailFragment>();
        incomeFragment = RemainderDetailFragment.newInstance(1);
        feeFragment = RemainderDetailFragment.newInstance(2);
        totalFragment = RemainderDetailFragment.newInstance(0);
        fragments.add(totalFragment);
        fragments.add(incomeFragment);
        fragments.add(feeFragment);

    }

    /***
     *
     * @param position
     */
    private void refreshTitle(int position){
       if(position==1){
           income.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
           incomeDivinder.setVisibility(View.VISIBLE);
       }else{
           income.setTextColor(getResources().getColor(R.color.colorPrimaryGrey2));
           incomeDivinder.setVisibility(View.INVISIBLE);
       }
        if(position==2){
            fee.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            feeDivinder.setVisibility(View.VISIBLE);
        }else{
            fee.setTextColor(getResources().getColor(R.color.colorPrimaryGrey2));
            feeDivinder.setVisibility(View.INVISIBLE);
        }
        if(position==0){
            total.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            totalDivinder.setVisibility(View.VISIBLE);
        }else{
            total.setTextColor(getResources().getColor(R.color.colorPrimaryGrey2));
            totalDivinder.setVisibility(View.INVISIBLE);
        }
    }


    public final class FragmentrAdapter extends android.support.v4.app.FragmentPagerAdapter{

        private List<RemainderDetailFragment> fragments;

        public FragmentrAdapter(FragmentManager fm, List<RemainderDetailFragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

}
