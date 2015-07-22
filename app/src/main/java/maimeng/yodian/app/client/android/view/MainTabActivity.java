package maimeng.yodian.app.client.android.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.melnykov.fab.FloatingActionButton;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.model.User;
import maimeng.yodian.app.client.android.service.ChatServiceLoginService;
import maimeng.yodian.app.client.android.service.UmengPushMessageService;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.view.auth.AuthActivity;
import maimeng.yodian.app.client.android.view.auth.AuthSeletorActivity;
import maimeng.yodian.app.client.android.view.auth.AuthSettingInfoActivity;
import maimeng.yodian.app.client.android.view.dialog.AlertDialog;
import maimeng.yodian.app.client.android.view.skill.proxy.ActivityProxyController;
import maimeng.yodian.app.client.android.view.skill.proxy.MainHomeProxy;
import maimeng.yodian.app.client.android.view.skill.proxy.MainListProxy;


public class MainTabActivity extends AbstractActivity implements AlertDialog.PositiveListener {
    private ActivityProxyController controller;
    private static final int REQUEST_AUTH=0x1001;//登陆
    private static final int REQUEST_UPDATEINFO = 0x1002;//更新个人信息

    private MainListProxy mListProxy;
    private MainHomeProxy mHomeProxy;
    private User user;
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
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setPushIntentServiceClass(UmengPushMessageService.class);
        mPushAgent.enable();
        mPushAgent.onAppStart();
        setContentView(R.layout.activity_main, false);
        mListProxy = new MainListProxy(this, findViewById(R.id.list_root));
        mHomeProxy = new MainHomeProxy(this, findViewById(R.id.home_root));
        controller=new ActivityProxyController(mListProxy,mHomeProxy);
        floatButton = (FloatingActionButton)findViewById(R.id.btn_float);
        floatButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mListProxy.isInited())mListProxy.init();
                if(!mHomeProxy.isInited())mHomeProxy.init();
                controller.onFloatClick((FloatingActionButton) v);
            }
        });
        onNewIntent(getIntent());
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
                user = User.read(this);
                if (TextUtils.isEmpty(user.getNickname()) || TextUtils.isEmpty(user.getAvatar())) {
                    finish();
                } else {
                    showDefault();
                }
            }else{
                finish();
            }
        }else {
            controller.onActivityResult(requestCode,resultCode,data);
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        user = User.read(this);
        if(TextUtils.isEmpty(user.getToken())){
            startActivityForResult(new Intent(this, AuthSeletorActivity.class), REQUEST_AUTH);
        }else {
            startService(new Intent(this, ChatServiceLoginService.class));
            if(TextUtils.isEmpty(user.getNickname())||TextUtils.isEmpty(user.getAvatar())){
                AlertDialog dialog = AlertDialog.newInstance("资料补全", "你的资料不完整，请补全资料!");
                dialog.setCancelable(false);
                dialog.setPositiveListener(this);
                dialog.show(getFragmentManager(), "dialog");
            }else {
                showDefault();
            }
        }
        LogUtil.i(MainTabActivity.class.getName(),"onNewIntent");
    }
}
