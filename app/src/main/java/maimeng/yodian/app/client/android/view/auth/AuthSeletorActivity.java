package maimeng.yodian.app.client.android.view.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.umeng.message.UmengRegistrar;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;
import org.henjue.library.share.AuthListener;
import org.henjue.library.share.Type;
import org.henjue.library.share.manager.AuthFactory;
import org.henjue.library.share.manager.IAuthManager;
import org.henjue.library.share.manager.WeiboAuthManager;
import org.henjue.library.share.model.AuthInfo;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.common.UserAuth;
import maimeng.yodian.app.client.android.model.Auth;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.AuthResponse;
import maimeng.yodian.app.client.android.network.service.AuthService;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.view.MainTabActivity;
import maimeng.yodian.app.client.android.view.dialog.WaitDialog;

public class AuthSeletorActivity extends AppCompatActivity implements View.OnClickListener {
    private static AuthInfo authInfo;
    private SsoHandler mSsoHandler;
    private AuthService service;
    private WaitDialog dialog;
    private static final int REQUEST_MOBILE_AUTH=0x5001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!TextUtils.isEmpty(UserAuth.read(this).token)){
            startActivity(new Intent(this,MainTabActivity.class));
            finish();
        }else {
            service = Network.getService(AuthService.class);
            setContentView(R.layout.activity_auth_selector);
            findViewById(R.id.btn_loginwechat).setOnClickListener(this);
            findViewById(R.id.btn_loginweibo).setOnClickListener(this);
            findViewById(R.id.btn_loginphone).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_loginphone){
            startActivityForResult(new Intent(this, AuthActivity.class), REQUEST_MOBILE_AUTH);
            finish();
        }else if(v.getId()==R.id.btn_loginwechat){
            IAuthManager authManager = AuthFactory.create(this, Type.Platform.WEIXIN);
            authManager.login(new YDAuthListener( Type.Platform.WEIXIN));
        }else if(v.getId()==R.id.btn_loginweibo){
            IAuthManager authManager = AuthFactory.create(this, Type.Platform.WEIBO);
            authManager.login(new YDAuthListener( Type.Platform.WEIBO));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_MOBILE_AUTH){
            handlerFinsh();
        }else {
            mSsoHandler = WeiboAuthManager.getSsoHandler();
            if (mSsoHandler != null) {
                mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
        }
    }


    private void handlerFinsh(){
        Intent intent = getIntent();
        if(intent.getBooleanExtra("result",false)){
            setResult(RESULT_OK);
            finish();
        }else {
            startActivity(new Intent(AuthSeletorActivity.this, MainTabActivity.class));
            finish();
        }
    }

    class YDAuthListener implements AuthListener,Callback<AuthResponse>{
        private final Type.Platform type;
        private int typeValue=0;
        YDAuthListener(Type.Platform type){
            this.type=type;
            if(this.type== Type.Platform.WEIBO){
                typeValue=1;
            }else if(this.type== Type.Platform.WEIXIN){
                typeValue=2;
            }
        }

        @Override
        public void onComplete(AuthInfo authInfo) {
            AuthSeletorActivity.authInfo=authInfo;
            service.thirdParty(typeValue,authInfo.token,authInfo.id, UmengRegistrar.getRegistrationId(AuthSeletorActivity.this),this);
            LogUtil.d(AuthSeletorActivity.class.getName(), "onComplete->token:%s,nickname:%s",authInfo.token,authInfo.nickname);
        }

        @Override
        public void onError() {
            LogUtil.e(AuthSeletorActivity.class.getName(),"onError");
        }

        @Override
        public void onCancel() {
            LogUtil.d(AuthSeletorActivity.class.getName(), "onCancel");
        }

        @Override
        public void start() {
            dialog=WaitDialog.show(AuthSeletorActivity.this);
        }

        @Override
        public void success(AuthResponse res, Response response) {
            if(res.isSuccess()){
                Auth data = res.getData();
                UserAuth user = new UserAuth(authInfo.nickname, authInfo.headimgurl,typeValue , data.getToken(), data.getUid(), data.getNickname(), data.getAvatar());
                user.write(AuthSeletorActivity.this);
                handlerFinsh();
            }else{
                res.showMessage(AuthSeletorActivity.this);
            }
        }

        @Override
        public void failure(HNetError hNetError) {
            ErrorUtils.checkError(AuthSeletorActivity.this, hNetError);
        }

        @Override
        public void end() {
            if(dialog!=null)dialog.dismiss();
        }
    }
}
