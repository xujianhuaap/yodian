package maimeng.yodian.app.client.android.view.common;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import maimeng.yodian.app.client.android.R;

/**
 * Created by xujianhua on 05/01/16.
 */
public class AcceptAddressActivity extends AbstractActivity{
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
    }
}
