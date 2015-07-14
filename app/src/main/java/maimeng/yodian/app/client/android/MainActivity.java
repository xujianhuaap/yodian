package maimeng.yodian.app.client.android;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.melnykov.fab.FloatingActionButton;
import com.umeng.analytics.MobclickAgent;

import maimeng.yodian.app.client.android.common.UserAuth;
import maimeng.yodian.app.client.android.view.AbstractActivity;
import maimeng.yodian.app.client.android.view.auth.AuthActivity;
import maimeng.yodian.app.client.android.view.auth.AuthSettingInfoActivity;
import maimeng.yodian.app.client.android.view.dialog.AlertDialog;
import maimeng.yodian.app.client.android.view.proxy.ActivityProxyController;
import maimeng.yodian.app.client.android.view.proxy.MainHomeProxy;
import maimeng.yodian.app.client.android.view.proxy.MainListProxy;


public class MainActivity extends AbstractActivity implements AlertDialog.PositiveListener {
    private ActivityProxyController controller;
    public static final int REQUEST_AUTH=0x1001;
    private static final int REQUEST_UPDATEINFO = 0x1002;
    private MainListProxy mListProxy;
    private MainHomeProxy mHomeProxy;
    private UserAuth user;
    private FloatingActionButton floatButton;

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
        floatButton = (FloatingActionButton)findViewById(R.id.btn_float);
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mListProxy.isInited())mListProxy.init();
                if(!mHomeProxy.isInited())mHomeProxy.init();
                controller.onFloatClick((FloatingActionButton) v);
            }
        });
        user = UserAuth.read(this);
        if(TextUtils.isEmpty(user.token)){
            startActivityForResult(new Intent(this, AuthActivity.class), REQUEST_AUTH);
        }else {
            if(TextUtils.isEmpty(user.nickname)||TextUtils.isEmpty(user.img)){
                AlertDialog dialog = AlertDialog.newInstance("资料补全", "你的资料不完整，请补全资料!");
                dialog.setCancelable(false);
                dialog.setPositiveListener(this);
                dialog.show(getFragmentManager(), "dialog");
            }else {
                showDefault();
            }
        }
    }
    private void showDefault(){
        mListProxy.init();
//      mListProxy.hide(floatButton);
        mListProxy.show(floatButton);
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
        }else if(requestCode==REQUEST_UPDATEINFO){
            if(resultCode==RESULT_OK) {
                user = UserAuth.read(this);
                if (TextUtils.isEmpty(user.nickname) || TextUtils.isEmpty(user.img)) {
                    finish();
                } else {
                    showDefault();
                }
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

    @Override
    public void onPositiveClick(DialogInterface dialog) {
            startActivityForResult(new Intent(this, AuthSettingInfoActivity.class),REQUEST_UPDATEINFO);
    }

    @Override
    public String positiveText() {
        return "是";
    }
}
