package maimeng.yodian.app.client.android.view.splash;

import android.graphics.drawable.Drawable;

import maimeng.yodian.app.client.android.R;


/**
 * Created by android on 2015/7/3.
 */
public class DefaultActivity extends AbsSplashActivity {

    @Override
    protected void onTimeout() {
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
