package maimeng.yodian.app.client.android.view.splash;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.common.LauncherCheck;
import maimeng.yodian.app.client.android.view.user.LauncherGuideActivity;


/**
 * Created by android on 2015/7/3.
 */
public class DefaultActivity extends AbsSplashActivity {
    private static final Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onTimeout() {
        if(LauncherCheck.isFirstRun(this)) {
            startActivity(new Intent(DefaultActivity.this, LauncherGuideActivity.class));
        }
        finish();
    }

    @Override
    protected long timeout() {
        return getResources().getInteger(R.integer.splash_duration);
    }

    @Override
    protected Drawable splash() {
        return getResources().getDrawable(R.mipmap.splash_default);
    }
}
