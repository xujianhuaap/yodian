package maimeng.yodian.app.client.android.view.deal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import org.parceler.Parcels;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.common.UEvent;
import maimeng.yodian.app.client.android.model.Lottery;
import maimeng.yodian.app.client.android.view.common.AbstractActivity;
import maimeng.yodian.app.client.android.view.common.WebViewActivity;
import maimeng.yodian.app.client.android.widget.ViewPager;

/**
 * Created by xujianhua on 12/1/15.
 */
public class LotteryActivity extends AbstractActivity implements View.OnClickListener{
    private Lottery mLottery;

    public static void show(Context context,Lottery lottery){
        Intent intent=new Intent(context,LotteryActivity.class);
        intent.putExtra("lottery", Parcels.wrap(lottery));
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery,false);
        mLottery=get("lottery");
        ImageView go=(ImageView)findViewById(R.id.btn_go);
        ImageView quit=(ImageView)findViewById(R.id.btn_quit);

        go.setOnClickListener(this);
        quit.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_go){
            MobclickAgent.onEvent(this, UEvent.PAY_SUCESS_GET_LOTTORY);
            WebViewActivity.show(LotteryActivity.this, mLottery.getLotUrl());
        }else{
            MobclickAgent.onEvent(this, UEvent.PAY_SUCESS_QUIT_LOTTORY);
            finish();
        }
    }
}
