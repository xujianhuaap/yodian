package maimeng.yodian.app.client.android.view.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.model.remainder.Remainder;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.RemainderResponse;
import maimeng.yodian.app.client.android.network.service.MoneyService;
import maimeng.yodian.app.client.android.view.deal.BindStatus;
import maimeng.yodian.app.client.android.view.deal.VouchApplyActivity;
import maimeng.yodian.app.client.android.view.deal.VouchDetailActivity;

/**
 * Created by xujianhua on 10/16/15.
 */
public class VouchDealActivity extends AppCompatActivity implements Callback<RemainderResponse> {


    private Remainder remainder;

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
        getWindow().setGravity(Gravity.BOTTOM);
        setContentView(R.layout.activity_vouch_deal);
        Network.getService(MoneyService.class).remanider(this);

        findViewById(R.id.vouch_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (remainder != null) {
                    if (remainder.getVouchStatus() != BindStatus.NO_CARD) {
                        VouchDetailActivity.show(VouchDealActivity.this);
                    } else {
                        VouchApplyActivity.show(VouchDealActivity.this);
                    }
                } else {
                    VouchApplyActivity.show(VouchDealActivity.this);
                }
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

    @Override
    public void start() {

    }

    @Override
    public void success(RemainderResponse res, Response response) {
        if (res.isSuccess()) {
            remainder = res.getData();
        } else {
            res.showMessage(this);
        }
    }

    @Override
    public void failure(HNetError hNetError) {
        ErrorUtils.checkError(this, hNetError);
    }

    @Override
    public void end() {

    }
}
