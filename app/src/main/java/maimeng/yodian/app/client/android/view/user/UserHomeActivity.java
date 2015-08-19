package maimeng.yodian.app.client.android.view.user;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.view.View;

import com.melnykov.fab.FloatingActionButton;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.model.User;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.response.SkillResponse;
import maimeng.yodian.app.client.android.network.response.SkillUserResponse;
import maimeng.yodian.app.client.android.network.service.CommonService;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.network.service.UserService;
import maimeng.yodian.app.client.android.view.AbstractActivity;
import maimeng.yodian.app.client.android.view.dialog.AlertDialog;
import maimeng.yodian.app.client.android.view.dialog.WaitDialog;
import maimeng.yodian.app.client.android.view.skill.proxy.MainHomeProxy;

/**
 * Created by android on 2015/8/13.
 */
public class UserHomeActivity extends AbstractActivity implements Callback<SkillUserResponse> {
    private SkillService service;
    private MainHomeProxy proxy;
    private int page = 1;
    private View mBtnSettings;
    private User user;

    public static void show(Activity activity, long uid, View btnBack, View avatar, View nickname) {
        show(activity, uid, null, "", btnBack, avatar, nickname);
    }

    public static void show(Activity activity, long uid, Bitmap avatar, String nickname, View btnBackView, View avatarView, View nicknameView) {
        UserIntent intent = create(activity, uid);
        intent.putExtra("avatar", avatar);
        intent.putExtra("nickname", nickname);
        ActivityOptionsCompat activityOptionsCompat = null;
        if (btnBackView != null) {
            activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, Pair.create(btnBackView, "back"));
        }
        if (avatarView != null) {
            if (activityOptionsCompat != null) {
                activityOptionsCompat.update(ActivityOptionsCompat.makeSceneTransitionAnimation(activity, Pair.create(avatarView, "avatar")));
            } else {
                activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, Pair.create(avatarView, "avatar"));
            }
        }
        if (nicknameView != null) {
            if (activityOptionsCompat != null) {
                activityOptionsCompat.update(ActivityOptionsCompat.makeSceneTransitionAnimation(activity, Pair.create(nicknameView, "nickname")));
            } else {
                activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, Pair.create(nicknameView, "nickname"));
            }
        }
        if (activityOptionsCompat != null) {
            final Bundle options = activityOptionsCompat.toBundle();
            ActivityCompat.startActivity(activity, intent, options);
        } else {
            activity.startActivity(intent);
        }
    }

    @Deprecated
    public static UserIntent create(Context context, long uid) {
        return new UserIntent(context, uid);
    }

    public static class UserIntent extends Intent {
        public UserIntent(Context context, long uid) {
            super(context, UserHomeActivity.class);
            putExtra("uid", uid);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = Network.getService(SkillService.class);
        setContentView(R.layout.activity_user_home, false);
        final View btnBack = findViewById(R.id.btn_back);
        final View avatar = findViewById(R.id.user_avatar);
        final View nickname = findViewById(R.id.user_nickname);
        ViewCompat.setTransitionName(btnBack, "back");
        ViewCompat.setTransitionName(avatar, "avatar");
        ViewCompat.setTransitionName(nickname, "nickname");
        final View root = findViewById(R.id.home_root);
        proxy = new MainHomeProxy(this, root, (Bitmap) getIntent().getParcelableExtra("avatar"), getIntent().getStringExtra("nickname"));
        ((View) findViewById(R.id.btn_createskill).getParent()).setVisibility(View.GONE);
        findViewById(R.id.btn_chat).setVisibility(View.GONE);
        findViewById(R.id.btn_settings).setVisibility(View.GONE);
        findViewById(R.id.user_avatar).setOnClickListener(null);
        root.setVisibility(View.VISIBLE);
        final View btnReport = findViewById(R.id.btn_report);
        btnReport.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(UserHomeActivity.this);
            }
        });
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                report();
            }
        });
        root.postDelayed(new Thread() {
            @Override
            public void run() {
                service.list(getIntent().getLongExtra("uid", 0), page, UserHomeActivity.this);
            }
        }, 500);
    }

    private void report() {
        AlertDialog alert = AlertDialog.newInstance("提示", getString(R.string.lable_alert_report_user));
        alert.setNegativeListener(new AlertDialog.NegativeListener() {
            @Override
            public void onNegativeClick(DialogInterface dialog) {
                dialog.dismiss();
            }

            @Override
            public String negativeText() {
                return getString(android.R.string.cancel);
            }
        });
        alert.setPositiveListener(new AlertDialog.PositiveListener() {
            @Override
            public void onPositiveClick(DialogInterface dialog) {
                Network.getService(CommonService.class).report(3, 0, 0, user.getUid(), new ToastCallback(UserHomeActivity.this));
            }

            @Override
            public String positiveText() {
                return getString(R.string.lable_report);
            }
        });
        alert.show(getFragmentManager(), "alert");
    }

    @Override
    public void start() {
    }

    @Override
    public void success(SkillUserResponse res, Response response) {
        if (res.isSuccess()) {
            final SkillUserResponse.DataNode data = res.getData();
            this.user = data.getUser();
            proxy.init(user);
            proxy.show((FloatingActionButton) findViewById(R.id.btn_back),false);
        } else {
            res.showMessage(this);
        }
    }

    @Override
    public void failure(HNetError hNetError) {
        ErrorUtils.checkError(this, hNetError);
    }

    @Override
    public void end() {
    }
}
