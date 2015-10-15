package maimeng.yodian.app.client.android.view.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;

import com.melnykov.fab.FloatingActionButton;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.view.AbstractActivity;
import maimeng.yodian.app.client.android.view.skill.UserHomeFragment;

/**
 * Created by android on 2015/8/13.
 */
public class UserHomeActivity extends AbstractActivity {
    private View mBtnSettings;
    private User user;


    public static void show(Context context, long uid, boolean isSingleTask) {
        Intent intent = new Intent(context, UserHomeActivity.class);
        if (isSingleTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        intent.putExtra("uid", uid);
        context.startActivity(intent);
    }


    public static void show(Context context, long uid) {
        show(context, uid, false);
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

    @Override
    public FloatingActionButton getFloatButton() {
        return (FloatingActionButton) findViewById(R.id.btn_back);
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
        setContentView(R.layout.activity_user_home, false);
        final View btnBack = findViewById(R.id.btn_back);
        UserHomeFragment fragment = UserHomeFragment.newInstance((Bitmap) getIntent().getParcelableExtra("avatar"), getIntent().getStringExtra("nickname"), getIntent().getLongExtra("uid", 0));
        getSupportFragmentManager().beginTransaction().add(R.id.content, fragment).commitAllowingStateLoss();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(UserHomeActivity.this);
            }
        });
    }
}
