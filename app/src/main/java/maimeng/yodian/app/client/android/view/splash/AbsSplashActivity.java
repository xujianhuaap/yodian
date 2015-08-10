package maimeng.yodian.app.client.android.view.splash;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;


/**
 * Created by android on 2015/7/3.
 */
public abstract class AbsSplashActivity extends AppCompatActivity {
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ImageView iv = new ImageView(this);
        iv.setBackgroundColor(0);
        iv.setScaleType(ImageView.ScaleType.MATRIX);
        iv.setBackground(splash());
        setContentView(iv);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onTimeout();
            }
        }, timeout());
    }

    protected abstract void onTimeout();

    protected abstract long timeout();

    protected abstract Drawable splash();
}
