package maimeng.yodian.app.client.android.view.deal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.view.AbstractActivity;

/**
 * Created by xujianhua on 9/19/15.
 */
public class MyRemainderActivity extends ActionBarAbstractActivity{

    public static void show(Context context){
        Intent intent=new Intent(context,MyRemainderActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            setTitle("我的余额");
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_go_back);
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.colorPrimary));
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }


}
