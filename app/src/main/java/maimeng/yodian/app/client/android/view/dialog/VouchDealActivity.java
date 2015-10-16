package maimeng.yodian.app.client.android.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.view.deal.VouchApplyActivity;

/**
 * Created by xujianhua on 10/16/15.
 */
public class VouchDealActivity extends AppCompatActivity{


    /***
     *
     * @param context
     */

    public static void show(Context context){
        Intent intent=new Intent(context,VouchDealActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setGravity(Gravity.BOTTOM);
        setContentView(R.layout.activity_vouch_deal);

        findViewById(R.id.vouch_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VouchApplyActivity.show(VouchDealActivity.this);
                finish();
            }
        });


        findViewById(R.id.vouch_later).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }
}
