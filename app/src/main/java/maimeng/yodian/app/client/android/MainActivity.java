package maimeng.yodian.app.client.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.View;

import com.umeng.analytics.MobclickAgent;

import maimeng.yodian.app.client.android.common.UserAuth;
import maimeng.yodian.app.client.android.view.AbstractActivity;
import maimeng.yodian.app.client.android.view.auth.AuthActivity;
import maimeng.yodian.app.client.android.view.proxy.ActivityProxyController;
import maimeng.yodian.app.client.android.view.proxy.MainHomeProxy;
import maimeng.yodian.app.client.android.view.proxy.MainListProxy;


public class MainActivity extends AbstractActivity  {
    private ActivityProxyController controller;
    public static final int REQUEST_AUTH=0x1001;
    private MainListProxy mListProxy;
    private MainHomeProxy mHomeProxy;

    public MainListProxy getProxyList() {
        return mListProxy;
    }

    public MainHomeProxy getProxyHome() {
        return mHomeProxy;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main, false);
        mListProxy = new MainListProxy(this, findViewById(R.id.list_root));
        mHomeProxy = new MainHomeProxy(this, findViewById(R.id.home_root));
        controller=new ActivityProxyController(mListProxy,mHomeProxy);
        final FloatingActionButton floatButton = (FloatingActionButton)findViewById(R.id.btn_float);
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mListProxy.isInited())mListProxy.init();
                if(!mHomeProxy.isInited())mHomeProxy.init();
                controller.onFloatClick((FloatingActionButton) v);
            }
        });
        if(TextUtils.isEmpty(UserAuth.read(this).token)){
            startActivityForResult(new Intent(this, AuthActivity.class), REQUEST_AUTH);
        }else {
            mListProxy.init();
            mListProxy.hide(floatButton);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_AUTH){
            if(resultCode==RESULT_OK){
                mListProxy.init();
            }else{
                finish();
            }
        }
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        controller.onTitleChanged(title,color);
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
