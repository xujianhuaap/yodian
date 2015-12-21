package maimeng.yodian.app.client.android.view.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.view.common.AbstractActivity;

/**
 * Created by android on 2015/8/13.
 */
public class UserHomeActivity extends AbstractActivity {

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

    public static void show(Activity activity, long uid) {
        UserIntent intent = create(activity, uid);
        activity.startActivity(intent);
    }

    @Deprecated
    public static UserIntent create(Context context, long uid) {
        return new UserIntent(context, uid);
    }


    @SuppressLint("ParcelCreator")
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
        UserHomeFragment fragment = UserHomeFragment.newInstance(getIntent().getLongExtra("uid", 0));
        getSupportFragmentManager().beginTransaction().add(R.id.content, fragment).commitAllowingStateLoss();

    }
}
