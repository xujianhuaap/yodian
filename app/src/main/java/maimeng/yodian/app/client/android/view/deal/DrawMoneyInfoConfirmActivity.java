package maimeng.yodian.app.client.android.view.deal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
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



    private MoneyService mService;
    private EditText mZhiFuBaoAccount;
    private EditText mConfirmAccount;
    private Button mSubmit;

    /***
     *
     * @param contenxt
     */
    public static void show(Context contenxt) {
        Intent intent = new Intent(contenxt, DrawMoneyInfoConfirmActivity.class);
        contenxt.startActivity(intent);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.activity_draw_money_confirm_info, null, false);
        this.mSubmit = (Button) view.findViewById(R.id.apply_submit);
        this.mConfirmAccount=(EditText)view.findViewById(R.id.confirm_account);
        this.mZhiFuBaoAccount=(EditText)view.findViewById(R.id.zhifubao_account);
        setContentView(view);

        mSubmit.setOnClickListener(this);

        TextWatcherProxy textWatcherProxy = new TextWatcherProxy();
        mConfirmAccount.addTextChangedListener(textWatcherProxy);
        mZhiFuBaoAccount.addTextChangedListener(textWatcherProxy);


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
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {




        }
    }


    /***
     * @param v
     */
    @Override
    public void onClick(View v) {

        if(v==mSubmit){

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
                setResult(RESULT_OK, new Intent().putExtra("apply", BindStatus.WAITCONFIRM.getValue()));
                finish();
            }
        }

        @Override
        public void failure(HNetError hNetError) {

        }
    }
}
