package maimeng.yodian.app.client.android.view.deal;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;


import maimeng.yodian.app.client.android.R;

/**
 * Created by xujianhua on 9/19/15.
 */
public class ActionBarAbstractActivity extends AppCompatActivity{
    private TextView mTitle;
    private FrameLayout mContent;

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
//        super.onTitleChanged("", color);
        if(mTitle!=null){
            mTitle.setText(title);
            mTitle.setTextColor(Color.WHITE);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        initSupportActionBar();

    }

    /***
     *
     */
    private void initSupportActionBar() {
        setContentView(R.layout.activity_action_bar_abstract_activity);
        mTitle=(TextView)findViewById(R.id.title);
        mTitle.setText("");
        mContent=(FrameLayout)findViewById(R.id.content);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContent.removeAllViews();
    }


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_action_bar_abstract_activity);
        getLayoutInflater().inflate(layoutResID,mContent,true);
    }


    @Override
    public void setContentView(View view) {
        super.setContentView(R.layout.activity_action_bar_abstract_activity);
        mContent.addView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(R.layout.activity_action_bar_abstract_activity);
        mContent.addView(view);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if(mTitle!=null){
            mTitle.setText(title);
            mTitle.setTextColor(Color.WHITE);
        }
    }
}
