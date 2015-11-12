package maimeng.yodian.app.client.android.view.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.view.deal.BasicalInfoConfirmActivity;
import maimeng.yodian.app.client.android.view.deal.BindStatus;

/**
 * Created by xujianhua on 10/16/15.
 */
public class OrderCancellActivity extends AppCompatActivity  {
    private static final int REQUEST_CERTIFY =0x234 ;
    private BindStatus status;

    /***
     * @param context
     */

    public static void show(Context context) {
        Intent intent = new Intent(context, OrderCancellActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        status = User.read(this).getInfo().getVouch_status();
        getWindow().setGravity(Gravity.BOTTOM);
        setContentView(R.layout.activity_order_cancel);
        findViewById(R.id.contact_us).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //请联系客服
            }
        });


        findViewById(R.id.order_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消订单
            }
        });

        findViewById(R.id.go_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
