package maimeng.yodian.app.client.android.view.deal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import butterknife.Bind;
import butterknife.ButterKnife;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.model.Vouch;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.response.VouchResponse;
import maimeng.yodian.app.client.android.network.service.MoneyService;
import maimeng.yodian.app.client.android.view.AbstractActivity;

/**
 * Created by xujianhua on 9/25/15.
 */
public class VouchDetailActivity extends AbstractActivity implements View.OnClickListener{

    @Bind(R.id.nickname)
    TextView mNickName;
    @Bind(R.id.phone)
    TextView mPhone;
    @Bind(R.id.email)
    TextView mEmail;
    @Bind(R.id.qq)
    TextView mQQ;
    @Bind(R.id.reason)
    TextView mReason;
    @Bind(R.id.apply_reedit)
    Button mReEdit;
    @Bind(R.id.apply_cancel)
    Button mCancel;
    @Bind(R.id.vouch_detail)
    TextView mVouchDetail;
    private MoneyService mService;


    public static void show(Context context){
        Intent intent=new Intent(context,VouchDetailActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vouch_detail);
        ButterKnife.bind(this);

        mService= Network.getService(MoneyService.class);
        mService.vouchDetail(new CallBackProxy());

        mReEdit.setOnClickListener(this);
        mCancel.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initToolBar(Toolbar toolbar) {
        super.initToolBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_go_back);
        }
    }

    @Override
    public void onClick(View v) {
        if(v==mCancel){
            mService.vouchCancel(new ToastCallback(VouchDetailActivity.this));
        }
    }

    /***
     *
     */
    public final class CallBackProxy implements Callback<VouchResponse>{
        @Override
        public void end() {

        }

        @Override
        public void start() {

        }

        @Override
        public void success(VouchResponse vouchResponse, Response response) {
            Vouch vouch=vouchResponse.getData();
            int status=vouch.getStatuas();
            String detail=vouch.getBack_detail();
            if(status==BindStatus.PASS.getValue()){

                mCancel.setVisibility(View.VISIBLE);
                mReEdit.setText(getString(R.string.apply_button_edit));
            }else{
                mCancel.setVisibility(View.GONE);
                mReEdit.setText(getString(R.string.apply_button_reedit));
            }

            mNickName.setText(vouch.getName());
            mPhone.setText(vouch.getTelephone());
            mQQ.setText(vouch.getQq());
            mEmail.setText(vouch.getEmail());
            mReason.setText(vouch.getContent());
            if(TextUtils.isEmpty(detail)){
                if(status==BindStatus.WAITCONFIRM.getValue()){
                    detail=getString(R.string.vouch_detail_status);
                }

            }
            mVouchDetail.setText(detail);


        }

        @Override
        public void failure(HNetError hNetError) {
            ErrorUtils.checkError(VouchDetailActivity.this,hNetError);
        }
    }

}
