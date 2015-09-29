package maimeng.yodian.app.client.android.view.deal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import butterknife.Bind;
import butterknife.ButterKnife;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.model.Vouch;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.response.VouchResponse;
import maimeng.yodian.app.client.android.network.service.MoneyService;
import maimeng.yodian.app.client.android.view.AbstractActivity;
import maimeng.yodian.app.client.android.view.MainTabActivity;

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
    @Bind(R.id.tip)
    TextView mTitle;
    private MoneyService mService;
    private int status;
    private Vouch mVouch;
    private final  int REQUEST_CODE_APPLY=0x23;


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
            mService.vouchCancel(new CancelCallBack());
        }else if(v==mReEdit){
            if(status==BindStatus.PASS.getValue()){
                Intent intent =new Intent(VouchDetailActivity.this,MainTabActivity.class);
                intent.putExtra("home",true);
                startActivity(intent);
            }else{
                if(mVouch!=null){
                    VouchApplyActivity.show(this,mVouch,REQUEST_CODE_APPLY);
                }

            }
        }
    }

    /***
     * 担保取消
     */
    public final class CancelCallBack implements Callback<ToastResponse>{
        @Override
        public void start() {

        }

        @Override
        public void success(ToastResponse toastResponse, Response response) {
            if(toastResponse.getCode()==20000){
                Toast.makeText(VouchDetailActivity.this,toastResponse.getMsg(),Toast.LENGTH_SHORT).show();
                finish();
            }

        }

        @Override
        public void failure(HNetError hNetError) {
            ErrorUtils.checkError(VouchDetailActivity.this,hNetError);
        }

        @Override
        public void end() {

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
            mVouch= vouchResponse.getData();
            status= mVouch.getStatus();
            String detail=mVouch.getBack_detail();
            if(status==BindStatus.DENY.getValue()){
                //3
                mCancel.setVisibility(View.GONE);
                mReEdit.setText(getString(R.string.apply_button_reedit));
            }else if(status==BindStatus.PASS.getValue()){
                //2
                mTitle.setVisibility(View.VISIBLE);
                mCancel.setVisibility(View.GONE);
                mReEdit.setText(getString(R.string.apply_button_edit));
            }else if(status==BindStatus.WAITCONFIRM.getValue()){
                //0
                mCancel.setVisibility(View.VISIBLE);
            }else if(status==BindStatus.CANCEL.getValue()){
                //4
                mCancel.setVisibility(View.GONE);
                mReEdit.setText(getString(R.string.apply_button_reedit));
            }


            mNickName.setText(mVouch.getName());
            mPhone.setText(mVouch.getTelephone());
            mQQ.setText(mVouch.getQq());
            mEmail.setText(mVouch.getEmail());
            mReason.setText(mVouch.getContent());
            if(status==BindStatus.WAITCONFIRM.getValue()){
                detail=getString(R.string.vouch_detail_during);
            }if(status==BindStatus.DENY.getValue()){
                detail=getString(R.string.vouch_detail_deny);
            } if(status==BindStatus.PASS.getValue()){
                detail=getString(R.string.vouch_detail_success);
            } if(status==BindStatus.CANCEL.getValue()){
                detail=getString(R.string.vouch_detail_cancel);
            }


            mVouchDetail.setText(detail);


        }

        @Override
        public void failure(HNetError hNetError) {
            ErrorUtils.checkError(VouchDetailActivity.this,hNetError);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==REQUEST_CODE_APPLY){
                if(mService==null){
                    mService=Network.getService(MoneyService.class);
                }
                mService.vouchDetail(new CallBackProxy());
            }
        }
    }
}
