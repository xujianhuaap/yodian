package maimeng.yodian.app.client.android.view.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sina.weibo.sdk.auth.sso.SsoHandler;

import org.henjue.library.share.AuthListener;
import org.henjue.library.share.Type;
import org.henjue.library.share.manager.AuthFactory;
import org.henjue.library.share.manager.IAuthManager;
import org.henjue.library.share.manager.WeiboAuthManager;
import org.henjue.library.share.model.AuthInfo;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.utils.LogUtil;

public class AuthSeletorActivity extends AppCompatActivity implements View.OnClickListener {
    private SsoHandler mSsoHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_selector);
        findViewById(R.id.btn_loginwechat).setOnClickListener(this);
        findViewById(R.id.btn_loginweibo).setOnClickListener(this);
        findViewById(R.id.btn_loginphone).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_loginphone){
            startActivity(new Intent(this,AuthActivity.class));
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
        mSsoHandler = WeiboAuthManager.getSsoHandler();
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    class YDAuthListener implements AuthListener{
        private final Type.Platform type;

        YDAuthListener(Type.Platform type){
            this.type=type;
        }

        @Override
        public void onComplete(AuthInfo authInfo) {
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
    }
}
