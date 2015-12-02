package maimeng.yodian.app.client.android.view.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;

import com.umeng.analytics.MobclickAgent;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.common.UEvent;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.RemainderResponse;
import maimeng.yodian.app.client.android.network.service.MoneyService;
import maimeng.yodian.app.client.android.view.deal.BasicalInfoConfirmActivity;
import maimeng.yodian.app.client.android.view.deal.BindStatus;
import maimeng.yodian.app.client.android.view.deal.DrawMoneyInfoConfirmActivity;

/**
 * Created by xujianhua on 10/16/15.
 */
public class VouchDealActivity extends AppCompatActivity  {
    private static final int REQUEST_CERTIFY =0x234 ;
    private BindStatus status;

    /***
     * @param context
     */

    public static void show(Context context) {
        Intent intent = new Intent(context, VouchDealActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        status = User.read(this).getInfo().getVouch_status();
        getWindow().setGravity(Gravity.BOTTOM);
        setContentView(R.layout.activity_vouch_deal);
        findViewById(R.id.vouch_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(VouchDealActivity.this, UEvent.INFO_CERTIFY_GO);
                BasicalInfoConfirmActivity.show(VouchDealActivity.this);
            }
        });


        findViewById(R.id.vouch_later).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(VouchDealActivity.this, UEvent.INFO_CERTIFY_LATER);
                finish();
            }
        });
    }


}
