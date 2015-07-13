package maimeng.yodian.app.client.android.view.auth;

import android.os.Bundle;
import android.view.View;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.AuthResponse;
import maimeng.yodian.app.client.android.network.service.AuthService;
import maimeng.yodian.app.client.android.view.AbstractActivity;

/**
 * Created by android on 15-7-13.
 */
public class AuthActivity extends AbstractActivity implements View.OnClickListener, Callback<AuthResponse> {
    private View mBtnNext;
    private AuthService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service=Network.getService(AuthService.class);
        setContentView(R.layout.activity_auth);
        setTitle("登录");
        mBtnNext=findViewById(R.id.btn_next);
        mBtnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        service.login("15884421212","123456","ahjfasdjfsahfkj",this);
    }

    @Override
    public void start() {

    }

    @Override
    public void success(AuthResponse authResponse, Response response) {
        System.out.println(authResponse.getMsg());
    }

    @Override
    public void failure(HNetError hNetError) {
            hNetError.printStackTrace();
    }

    @Override
    public void end() {

    }
}
