package maimeng.yodian.app.client.android.view.deal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.model.Vouch;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.service.MoneyService;
import maimeng.yodian.app.client.android.view.AbstractActivity;

/**
 * Created by xujianhua on 9/25/15.
 */
public class DrawMoneyInfoConfirmActivity extends AbstractActivity implements View.OnClickListener {


    private String mZhiFuBaoStr;
    private String mReconfirmStr;

    private MoneyService mService;
    private EditText mZhiFuBaoAccount;
    private EditText mConfirmAccount;
    private Button mSubmit;
    private TextView mTip;
    private boolean mIsModify;

    /***
     *
     * @param contenxt
     */
    public static void show(Context contenxt,String account) {
        Intent intent = new Intent(contenxt, DrawMoneyInfoConfirmActivity.class);
        intent.putExtra("alipay",account);
        contenxt.startActivity(intent);
    }


    /***
     *
     * @param contenxt
     */
    public static void show(Activity contenxt,int requestCode) {
        Intent intent = new Intent(contenxt, DrawMoneyInfoConfirmActivity.class);
        contenxt.startActivityForResult(intent,requestCode);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_money_confirm_info);
        this.mSubmit = (Button)findViewById(R.id.apply_submit);
        this.mConfirmAccount=(EditText)findViewById(R.id.confirm_account);
        this.mZhiFuBaoAccount=(EditText)findViewById(R.id.zhifubao_account);
        this.mTip=(TextView)findViewById(R.id.tip);
        String alipay=getIntent().getStringExtra("alipay");
        if(!TextUtils.isEmpty(alipay)){
            mIsModify=true;
        }
        mZhiFuBaoAccount.setText(alipay);
        Spanned span= Html.fromHtml(getResources().getString(R.string.draw_money_info_certify_tip));
        mTip.setText(span);
        mSubmit.setOnClickListener(this);


        mConfirmAccount.addTextChangedListener(new TextWatcherProxy(mConfirmAccount));
        mZhiFuBaoAccount.addTextChangedListener(new TextWatcherProxy(mZhiFuBaoAccount));

        mService = Network.getService(MoneyService.class);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void initToolBar(android.support.v7.widget.Toolbar toolbar) {
        super.initToolBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            mTitle.setTextColor(Color.WHITE);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_go_back);
        }
    }



    /***
     *
     */
    public final class TextWatcherProxy implements TextWatcher {
        private EditText mEdit;

        public TextWatcherProxy(EditText mEdit) {
            this.mEdit = mEdit;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if(mEdit==mZhiFuBaoAccount){
                mZhiFuBaoStr=s.toString();
            }else  if(mEdit==mConfirmAccount){
                mReconfirmStr=s.toString();
            }




        }
    }


    /***
     * @param v
     */
    @Override
    public void onClick(View v) {

        if(v==mSubmit){
            String tip="填写内容不能为空";
            if(TextUtils.isEmpty(mZhiFuBaoStr)){
                Toast.makeText(DrawMoneyInfoConfirmActivity.this,tip,Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(mReconfirmStr)){
                Toast.makeText(DrawMoneyInfoConfirmActivity.this,tip,Toast.LENGTH_SHORT).show();
                return;
            }
            if(mZhiFuBaoStr.equals(mReconfirmStr)){
                mService.addAccount(mReconfirmStr,new ToastCallBack());
            }else{
                tip="两次所填的内容不一致";
                Toast.makeText(DrawMoneyInfoConfirmActivity.this,tip,Toast.LENGTH_SHORT).show();
            }

        }
    }


    public final class ToastCallBack implements Callback<ToastResponse> {
        @Override
        public void end() {

        }

        @Override
        public void start() {

        }

        @Override
        public void success(ToastResponse toastResponse, Response response) {
            if (toastResponse.getCode() == 20000) {
                if(!mIsModify){
                    setResult(RESULT_OK,getIntent().putExtra("alipay",mConfirmAccount.getText().toString()));
                }
                finish();
            }
        }

        @Override
        public void failure(HNetError hNetError) {

        }
    }



}
