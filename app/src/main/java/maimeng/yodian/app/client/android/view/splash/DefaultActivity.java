package maimeng.yodian.app.client.android.view.splash;

import android.graphics.drawable.Drawable;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.common.LauncherCheck;


/**
 * Created by android on 2015/7/3.
 */
public class DefaultActivity extends AbsSplashActivity {
    @Override
    protected void onTimeout() {
        LauncherCheck.updateFirstRun(this, false);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected long timeout() {
        return getResources().getInteger(R.integer.splash_duration);
    }

    @Override
    protected Drawable splash() {
        return getResources().getDrawable(R.drawable.splash_baidu);
    }
}
