package maimeng.yodian.app.client.android.view.deal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;
import org.parceler.Parcels;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.common.UEvent;
import maimeng.yodian.app.client.android.model.user.CertifyInfo;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.CertifyInfoResponse;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.service.AuthService;
import maimeng.yodian.app.client.android.view.common.AbstractActivity;
import maimeng.yodian.app.client.android.view.dialog.RemainderCustomDialog;

/**
 * 绑定银行卡表单界面
 */
public class BasicalInfoConfirmActivity extends AbstractActivity implements View.OnClickListener {

    private Toast toast;
    private EditText mName;
    private EditText mID;
    private EditText mMobile;
    private EditText mConfirmCode;
    private Button mSubmit;
    private Button mGetCode;
    private Button mCode;

    private String mNameStr;
    private String mIdStr;
    private String mMobileStr;
    private String mCodeStr;
    private String mNullInfo;
    private boolean isSucess;
    private boolean isCanClick = true;//可以请求ｇｅｔＣｏｄｅ

    private AuthService mService;
    private static final int TYPE_CERTIFY_CODE = 2;
    CountDownTimer timeout;
    Pattern p = Pattern.compile("^170|((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
    final int[] weight = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};    //十七位数字本体码权重
    final char[] validate = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};    //mod11,对应校验码字符值
    private TextView mTip;
    private CertifyInfo mCertifyInfo;

    /***
     * @param context
     * @param
     */
    public static void show(Activity context,CertifyInfo certifyInfo, int requestCode) {
        Intent intent = new Intent(context, BasicalInfoConfirmActivity.class);
        intent.putExtra("info", Parcels.wrap(certifyInfo));
        context.startActivityForResult(intent, requestCode);
    }

    /***
     * @param context
     * @param
     */
    public static void show(Activity context, int requestCode) {
        Intent intent = new Intent(context, BasicalInfoConfirmActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MobclickAgent.onEvent(this, UEvent.INFO_BASIC_CERTIFY_ENTRY);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_info_confirm);
        mCertifyInfo=Parcels.unwrap(getIntent().getParcelableExtra("info"));
        mName = (EditText) findViewById(R.id.basic_name);
        mID = (EditText) findViewById(R.id.basic_id);
        mMobile = (EditText) findViewById(R.id.basic_mobile);
        mSubmit = (Button) findViewById(R.id.apply_submit);
        mConfirmCode = (EditText) findViewById(R.id.basic_code);
        mGetCode = (Button) findViewById(R.id.btn_get_code);
        mCode = (Button) findViewById(R.id.btn_code);
        mTip = (TextView) findViewById(R.id.tip);

        Spanned span = Html.fromHtml(getResources().getString(R.string.certify_info_tip));
        mTip.setText(span);

        mID.addTextChangedListener(new InputListener(mID));
        mName.addTextChangedListener(new InputListener(mName));
        mMobile.addTextChangedListener(new InputListener(mMobile));
        mConfirmCode.addTextChangedListener(new InputListener(mConfirmCode));

        mSubmit.setOnClickListener(this);
        mGetCode.setOnClickListener(this);

        if (mCertifyInfo != null) {
            mName.setText(mCertifyInfo.getCname());
            mMobile.setText(mCertifyInfo.getMobile());
            mID.setText(mCertifyInfo.getIdcard());
        }
        mService = Network.getService(AuthService.class);
    }


    @Override
    protected void initToolBar(Toolbar toolbar) {
        super.initToolBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_go_back);
        }
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

    private class InputListener implements TextWatcher {
        private final TextView mEdit;

        InputListener(TextView edit) {
            mEdit = edit;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mEdit == mName) {
                mNameStr = s.toString();
            } else if (mEdit == mMobile) {
                mMobileStr = s.toString();
            } else if (mEdit == mID) {
                mIdStr = s.toString();
            } else {
                mCodeStr = s.toString();
            }

        }


    }

    private boolean checkNotNull() {
        if (TextUtils.isEmpty(mCodeStr)) {
            mNullInfo = getResources().getString(R.string.basic_info_code_null);
            return false;
        }
        if (TextUtils.isEmpty(mIdStr)) {
            mNullInfo = getResources().getString(R.string.basic_info_id_null);
            return false;
        }
        if (TextUtils.isEmpty(mMobileStr)) {
            mNullInfo = getResources().getString(R.string.basic_info_mobile_null);
            return false;
        }
        if (TextUtils.isEmpty(mNameStr)) {
            mNullInfo = getResources().getString(R.string.basic_info_name_null);
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == mSubmit) {
            if (checkNotNull()) {
                if (checkId(mIdStr)) {
                    MobclickAgent.onEvent(this, UEvent.INFO_BASIC_CERTIFY_SUBMIT);
                    mService.certifyUserInfo(mNameStr, mIdStr, mMobileStr, mCodeStr, new CallBackProxy(false));
                } else {
                    Toast.makeText(this, getResources().getString(R.string.basic_info_id_illegal), Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, mNullInfo, Toast.LENGTH_SHORT).show();
            }
        } else if (v == mGetCode) {
            if (isCanClick) {
                MobclickAgent.onEvent(this, UEvent.INFO_BASIC_CERTIFY_CODE);
                if (!TextUtils.isEmpty(mMobileStr)) {

                    Matcher m = p.matcher(mMobileStr);
                    if (m.matches()) {
                        timeout = new CountDownTimer(mCode, getResources().getInteger(R.integer.code_duration));
                        timeout.start();
                        mGetCode.setVisibility(View.GONE);
                        mCode.setVisibility(View.VISIBLE);
                        isCanClick = false;
                        mConfirmCode.requestFocus();
                        mService.getMobileCode(mMobileStr, TYPE_CERTIFY_CODE, new CallBackProxy(true));
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.basic_info_mobile_illegal), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, getResources().getString(R.string.basic_info_mobile_null), Toast.LENGTH_SHORT).show();
                }

            }

        }
    }


    /**
     * 检查身份证合法性
     *
     * @param id18
     * @return
     */
    public boolean checkId(String id18) {
        if (TextUtils.isEmpty(id18)) return false;
        if (id18.length() < 15) return false;
        if (id18.length() == 15) return true;//15位身份证无法校验
        if (id18.length() > 15 && id18.length() != 18) return false;
        int sum = 0;
        int mode = 0;
        for (int i = 0; i < 17; i++) {
            sum = sum + Integer.parseInt(String.valueOf(id18.charAt(i))) * weight[i];
        }
        mode = sum % 11;
        char valicode = validate[mode];
        return valicode == id18.charAt(17);
    }


    private void toast(String msg) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    private boolean initedBankList = false;

    private void syncBankList() {

    }


    /***
     * 计时器
     */

    private class CountDownTimer extends android.os.CountDownTimer {
        private final TextView tv;
        private final CharSequence oldText;

        public CountDownTimer(TextView tv, long timeout) {
            super(timeout, 1000);
            this.tv = tv;
            this.oldText = tv.getText();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tv.setText((millisUntilFinished / 1000) + "秒后可重发");
        }

        @Override
        public void onFinish() {
            tv.setEnabled(true);
            tv.setText(oldText);
            isCanClick = true;
            mCode.setVisibility(View.GONE);
            mGetCode.setVisibility(View.VISIBLE);
        }
    }


    public final class CallBackProxy implements Callback<ToastResponse> {
        private boolean isGetCode;

        public CallBackProxy(boolean isGetCode) {
            this.isGetCode = isGetCode;
        }

        @Override
        public void start() {

        }

        @Override
        public void success(ToastResponse toastResponse, Response response) {

            //个人信息提交成功
            if (toastResponse.getCode() == 20000) {
                if (!isGetCode) {
                    Toast.makeText(BasicalInfoConfirmActivity.this, toastResponse.getMsg(), Toast.LENGTH_SHORT).show();
                    RemainderCustomDialog.Builder builder = new RemainderCustomDialog.Builder(BasicalInfoConfirmActivity.this);
                    builder.setMesage((Html.fromHtml(getResources().getString(R.string.certify_info_success))));
                    builder.setPositiveListener(new RemainderCustomDialog.IPositiveListener() {
                        @Override
                        public void positiveClick() {
                            if(mCertifyInfo==null){
                                mCertifyInfo=new CertifyInfo();
                            }
                            mCertifyInfo.setCname(mNameStr);
                            mCertifyInfo.setIdcard(mIdStr);
                            mCertifyInfo.setMobile(mMobileStr);
                            setResult(RESULT_OK, getIntent().putExtra("certifyinfo", Parcels.wrap(mCertifyInfo)));
                            finish();
                        }
                    }, "知道了");
                    builder.create().show();

                }

            } else {
                isSucess = true;
                Toast.makeText(BasicalInfoConfirmActivity.this, toastResponse.getMsg(), Toast.LENGTH_SHORT).show();
            }


        }

        @Override
        public void failure(HNetError hNetError) {
            ErrorUtils.checkError(BasicalInfoConfirmActivity.this, hNetError);
        }

        @Override
        public void end() {

        }
    }

    @Override
    public void finish() {
        super.finish();
        if (!isSucess) {
            MobclickAgent.onEvent(BasicalInfoConfirmActivity.this, UEvent.INFO_BASIC_CERTIFY_CANCEL);
        }
    }
}
