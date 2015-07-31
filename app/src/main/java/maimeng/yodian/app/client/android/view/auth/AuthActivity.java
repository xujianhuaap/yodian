package maimeng.yodian.app.client.android.view.auth;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.umeng.message.UmengRegistrar;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.model.User;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.response.AuthResponse;
import maimeng.yodian.app.client.android.network.service.AuthService;
import maimeng.yodian.app.client.android.view.dialog.WaitDialog;

/**
 * Created by android on 15-7-13.
 */
public class AuthActivity extends AppCompatActivity implements View.OnClickListener, Callback<AuthResponse> {
    private View mBtnLogin;
    private AuthService service;
    private EditText mMobile;
    private WaitDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = Network.getService(AuthService.class);
        setContentView(R.layout.activity_auth);
        ViewCompat.setTransitionName(findViewById(R.id.icon),"icon");
        mMobile = (EditText) findViewById(R.id.mobile);
//        setTitle("登录");
        mBtnLogin = findViewById(R.id.btn_login);
        findViewById(R.id.btn_getcode).setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(AuthActivity.this);
            }
        });
        findViewById(R.id.btn_clean).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMobile.setText("");
            }
        });
    }

    @Override
    public void onClick(View v) {
        Editable text = mMobile.getText();
        if (text != null) {
            if (v.getId() == R.id.btn_getcode) {
                service.getCode(text.toString(), new ToastCallback(v.getContext()));
            } else if (v.getId() == R.id.btn_login) {
                Editable code = ((EditText) findViewById(R.id.code)).getText();
                if(TextUtils.isEmpty(code)){
                    Toast.makeText(this,R.string.code_input_empty_message,Toast.LENGTH_SHORT).show();
                }else {
                    String device_token = UmengRegistrar.getRegistrationId(this);
                    service.login(text.toString(),code.toString(),device_token, this);
                }
            }
        } else {
            Toast.makeText(this,R.string.mobile_input_empty_message,Toast.LENGTH_SHORT).show();
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
        dialog.dismiss();
    }
}
