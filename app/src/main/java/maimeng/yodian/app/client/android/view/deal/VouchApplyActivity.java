package maimeng.yodian.app.client.android.view.deal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
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
public class VouchApplyActivity extends AbstractActivity implements View.OnClickListener {


    private String mNicknameStr;
    private String mPhoneStr;
    private String mEmailStr;
    private String mQQStr;
    private String mReasonStr;

    private MoneyService mService;
    private EditText mNickName;
    private ImageView mClearNickName;
    private EditText mPhone;
    private ImageView mClearPhone;
    private EditText mEmail;
    private ImageView mClearEmail;
    private EditText mQQ;
    private ImageView mClearQQ;
    private EditText mReason;
    private TextView mReasonNum;
    private Button mSubmit;


    public static void show(Context contenxt) {
        Intent intent = new Intent(contenxt, VouchApplyActivity.class);
        contenxt.startActivity(intent);
    }

    public static void show(Activity contenxt, Vouch vouch, int requestCode) {
        Intent intent = new Intent(contenxt, VouchApplyActivity.class);
        intent.putExtra("vouch", vouch);
        contenxt.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.activity_vouch_apply, null, false);
        this.mSubmit = (Button) view.findViewById(R.id.apply_submit);
        this.mReasonNum = (TextView) view.findViewById(R.id.reasonNumber);
        this.mReason = (EditText) view.findViewById(R.id.reason);
        this.mClearQQ = (ImageView) view.findViewById(R.id.clear_qq);
        this.mQQ = (EditText) view.findViewById(R.id.qq);
        this.mClearEmail = (ImageView) view.findViewById(R.id.clear_email);
        this.mEmail = (EditText) view.findViewById(R.id.email);
        this.mClearPhone = (ImageView) view.findViewById(R.id.clear_phone);
        this.mPhone = (EditText) view.findViewById(R.id.phone);
        this.mClearNickName = (ImageView) view.findViewById(R.id.clear_nickname);
        this.mNickName = (EditText) view.findViewById(R.id.nickname);
        setContentView(view);

        mClearNickName.setOnClickListener(this);
        mClearEmail.setOnClickListener(this);
        mClearQQ.setOnClickListener(this);
        mClearPhone.setOnClickListener(this);
        mSubmit.setOnClickListener(this);

        TextWatcherProxy textWatcherProxy = new TextWatcherProxy();
        mNickName.addTextChangedListener(textWatcherProxy);
        mPhone.addTextChangedListener(textWatcherProxy);
        mEmail.addTextChangedListener(textWatcherProxy);
        mReason.addTextChangedListener(textWatcherProxy);
        mQQ.addTextChangedListener(textWatcherProxy);

        mService = Network.getService(MoneyService.class);

        if (getIntent().hasExtra("vouch")) {
            Vouch vouch = getIntent().getParcelableExtra("vouch");
            mNickName.setText(vouch.getName());
            mPhone.setText(vouch.getTelephone());
            mEmail.setText(vouch.getEmail());
            mQQ.setText(vouch.getQq());
            mReason.setText(vouch.getContent());
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /***
     *
     */

    private void freshApplyInfo() {
        mNicknameStr = mNickName.getText().toString();
        mPhoneStr = mPhone.getText().toString();
        mEmailStr = mEmail.getText().toString();
        mQQStr = mQQ.getText().toString();
        mReasonStr = mReason.getText().toString();
    }

    @Override
    protected void initToolBar(android.support.v7.widget.Toolbar toolbar) {
        super.initToolBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_go_back);
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
            freshApplyInfo();
            mReasonNum.setText(mReasonStr.length() + "/500");

            if (TextUtils.isEmpty(mNicknameStr)) {
                mClearNickName.setVisibility(View.INVISIBLE);
            } else {
                mClearNickName.setVisibility(View.VISIBLE);
            }
            if (TextUtils.isEmpty(mPhoneStr)) {
                mClearPhone.setVisibility(View.INVISIBLE);
            } else {
                mClearPhone.setVisibility(View.VISIBLE);
            }
            if (TextUtils.isEmpty(mEmailStr)) {
                mClearEmail.setVisibility(View.INVISIBLE);
            } else {
                mClearEmail.setVisibility(View.VISIBLE);
            }
            if (TextUtils.isEmpty(mQQStr)) {
                mClearQQ.setVisibility(View.INVISIBLE);
            } else {
                mClearQQ.setVisibility(View.VISIBLE);
            }


        }
    }


    /***
     * @param v
     */
    @Override
    public void onClick(View v) {

        //清空信息
        if (v == mClearNickName) {
            mNickName.setText("");
        } else if (v == mClearPhone) {
            mPhone.setText("");
        } else if (v == mClearEmail) {
            mEmail.setText("");
        } else if (v == mClearQQ) {
            mQQ.setText("");
        } else if (v == mSubmit) {
            freshApplyInfo();
            if (TextUtils.isEmpty(mNicknameStr)) {
                Toast.makeText(this, getString(R.string.apply_nickname_null), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(mPhoneStr)) {
                Toast.makeText(this, getString(R.string.apply_phone_null), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(mEmailStr)) {
                Toast.makeText(this, getString(R.string.apply_email_null), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(mQQStr)) {
                Toast.makeText(this, getString(R.string.apply_qq_null), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(mReasonStr)) {
                Toast.makeText(this, getString(R.string.apply_reason_null), Toast.LENGTH_SHORT).show();
                return;
            }
            mService.vouchApply(mNicknameStr, mPhoneStr, mQQStr, mEmailStr, mReasonStr, new ToastCallBack());
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
