package maimeng.yodian.app.client.android.view.auth;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.message.UmengRegistrar;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.AuthResponse;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.service.AuthService;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.view.dialog.WaitDialog;

/**
 * Created by android on 15-7-13.
 */
public class AuthActivity extends AppCompatActivity implements View.OnClickListener, Callback<AuthResponse> {
    private View mBtnLogin;
    private AuthService service;
    private EditText mMobile;
    private WaitDialog dialog;
    private Handler mHanlder;
    private int mTotalTime = 0;
    private TextView mCode;
    private EditText mValidateCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = Network.getService(AuthService.class);
        setContentView(R.layout.activity_auth);
        ViewCompat.setTransitionName(findViewById(R.id.icon), "icon");
        mMobile = (EditText) findViewById(R.id.mobile);
//        setTitle("登录");
        mBtnLogin = findViewById(R.id.btn_login);
        mValidateCode = ((EditText) findViewById(R.id.code));
        mCode = (TextView) findViewById(R.id.btn_getcode);
        mCode.setOnClickListener(this);
        mCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mValidateCode.getText().length() == 4 && mMobile.getText().length() >= 11) {
                    onClick(mBtnLogin);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mBtnLogin.setOnClickListener(this);
        findViewById(R.id.btn_clean).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMobile.setText("");
            }
        });

        mHanlder = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mCode.setText((mTotalTime - msg.arg1) + "");
                mTotalTime = -msg.arg1;
            }
        };
    }

    @Override
    public void onClick(View v) {
        Editable text = mMobile.getText();
        if (text != null) {
            if (v.getId() == R.id.btn_getcode) {
                mValidateCode.setFocusable(true);
                mValidateCode.setFocusableInTouchMode(true);
                mValidateCode.requestFocus();
                service.getCode(text.toString(), new Callback<ToastResponse>() {
                    @Override
                    public void start() {

                    }

                    @Override
                    public void success(ToastResponse toastResponse, Response response) {
                        LogUtil.d(AuthActivity.class.getName(), "response" + response);
                        if (toastResponse.isSuccess()) {
                            mHanlder.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mCode.setText("剩余" + (60 - mTotalTime) + "秒");
                                    mTotalTime++;
                                    if (mTotalTime < 60) {
                                        mHanlder.postDelayed(this, 1000);
                                    } else {
                                        mCode.setText("获取验证码");
                                        mTotalTime = 0;
                                    }
                                }
                            }, 1000);

                        }
                    }

                    @Override
                    public void failure(HNetError hNetError) {

                    }

                    @Override
                    public void end() {

                    }
                });
            } else if (v.getId() == R.id.btn_login) {

                if (TextUtils.isEmpty(mValidateCode.getText())) {
                    Toast.makeText(this, R.string.code_input_empty_message, Toast.LENGTH_SHORT).show();
                } else {
                    String device_token = UmengRegistrar.getRegistrationId(this);
                    service.login(text.toString(), mValidateCode.getText().toString(), device_token, this)
                    ;
                }
            }
        } else {
            Toast.makeText(this, R.string.mobile_input_empty_message, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void start() {
        dialog = WaitDialog.show(this, "登录中...");
    }

    @Override
    public void success(AuthResponse res, Response response) {
        if (res.isSuccess()) {
            User data = res.getData();
            data.setT_img("");
            data.setT_nickname("");
            data.setLoginType(0);
            data.write(this);
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, res.getMsg(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void failure(HNetError hNetError) {
        ErrorUtils.checkError(this, hNetError);
    }

    @Override
    public void end() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
