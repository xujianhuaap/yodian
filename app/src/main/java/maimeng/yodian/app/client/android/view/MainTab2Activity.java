package maimeng.yodian.app.client.android.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;

import com.melnykov.fab.FloatingActionButton;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.common.LauncherCheck;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.service.ChatServiceLoginService;
import maimeng.yodian.app.client.android.view.auth.AuthRedirect;
import maimeng.yodian.app.client.android.view.auth.AuthSeletorActivity;
import maimeng.yodian.app.client.android.view.auth.AuthSettingInfoActivity;
import maimeng.yodian.app.client.android.view.dialog.AlertDialog;
import maimeng.yodian.app.client.android.view.skill.IndexFragment;
import maimeng.yodian.app.client.android.view.skill.UserHomeFragment;

/**
 * Created by android on 15-10-10.
 */
public class MainTab2Activity extends AbstractActivity implements AlertDialog.PositiveListener {
    private User user;

    public FloatingActionButton getFloatButton() {
        return floatButton;
    }

    private FloatingActionButton floatButton;
    private static final int REQUEST_AUTH = 0x1001;//登陆
    private static final int REQUEST_UPDATEINFO = 0x1002;//更新个人信息
    private UserHomeFragment userHomeFragment;
    private IndexFragment indexFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LauncherCheck.isFirstRun(this)) {
            finish();
        } else {
            PushAgent mPushAgent = PushAgent.getInstance(this);
            mPushAgent.enable();
            mPushAgent.onAppStart();
            setContentView(R.layout.activity_yodian_main2, false);

            floatButton = (FloatingActionButton) findViewById(R.id.btn_float);
            floatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggle();
                }
            });
            new CheckUpdateDelegate(this, false).checkUpdate();
            if (getIntent().hasExtra("home")) {
                floatButton.callOnClick();
            }
            FragmentTransaction bt = getSupportFragmentManager().beginTransaction();
            userHomeFragment = UserHomeFragment.newInstance();
            indexFragment = IndexFragment.newInstance();
            bt.add(R.id.container, userHomeFragment, UserHomeFragment.class.getName());
            bt.add(R.id.container, indexFragment, IndexFragment.class.getName());
            bt.addToBackStack(null);
            bt.hide(userHomeFragment).show(indexFragment);
            bt.commitAllowingStateLoss();
            initProxy();
        }
    }

    private void toggle() {
        FragmentTransaction bt = getSupportFragmentManager().beginTransaction();
        if (userHomeFragment.isHidden()) {
            bt.setCustomAnimations(R.anim.translation_to_bottom_in, R.anim.translation_to_bottom_out);
            bt.show(userHomeFragment).hide(indexFragment);
        } else {
            bt.setCustomAnimations(R.anim.translation_to_top_in, R.anim.translation_to_top_out);
            bt.show(indexFragment).hide(userHomeFragment);
        }
        bt.commitAllowingStateLoss();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(User.read(this).getToken())) {
            AuthRedirect.toAuth(this);
        }
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onPositiveClick(DialogInterface dialog) {
        startActivityForResult(new Intent(this, AuthSettingInfoActivity.class), REQUEST_UPDATEINFO);
    }

    @Override
    public String positiveText() {
        return "是";
    }

    private void initProxy() {
        user = User.read(this);
        if (TextUtils.isEmpty(user.getToken())) {
            AuthSeletorActivity.start(this, REQUEST_AUTH);
        } else {
            startService(new Intent(this, ChatServiceLoginService.class));
            if (TextUtils.isEmpty(user.getNickname()) || TextUtils.isEmpty(user.getAvatar())) {
                AlertDialog dialog = AlertDialog.newInstance("资料补全", "你的资料不完整，请补全资料!");
                dialog.setCancelable(false);
                dialog.setPositiveListener(this);
                dialog.show(getFragmentManager(), "dialog");
            } else {
                showDefault();
            }
        }
    }

    private void showDefault() {

    }
}
