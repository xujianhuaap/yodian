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


    public static void show(Context context, long uid, boolean isSingleTask) {
        Intent intent = new Intent(context, UserHomeActivity.class);
        if(isSingleTask){
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        intent.putExtra("uid", uid);
        context.startActivity(intent);
    }


    public static void show(Context context, long uid) {
      show(context,uid,false);
    }

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
        final View root = findViewById(R.id.home_root);
        proxy = new MainHomeProxy(this, root, (Bitmap) getIntent().getParcelableExtra("avatar"), getIntent().getStringExtra("nickname"));
        root.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(UserHomeActivity.this);
            }
        });

        root.postDelayed(new Thread() {
            @Override
            public void run() {
                service.list(getIntent().getLongExtra("uid", 0), page, UserHomeActivity.this);
            }
        }, 500);
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
            proxy.show((FloatingActionButton) findViewById(R.id.btn_back), false);
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
