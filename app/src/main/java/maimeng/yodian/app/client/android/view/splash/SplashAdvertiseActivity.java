package maimeng.yodian.app.client.android.view.splash;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.ViewTarget;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.network.loader.ImageLoaderManager;

/**
 * Created by android on 2015/11/18.
 */
public class SplashAdvertiseActivity extends AppCompatActivity implements Runnable {
    public static final String UPDATE_ADVERTISE_CLOSE = "maimeng.yodian.app.client.android.UPDATE_ADVERTISE_CLOSE";
    private final Handler handler = new Handler();
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final ImageView iv = new ImageView(this);
        iv.setScaleType(ImageView.ScaleType.MATRIX);
        setContentView(iv);
        registerReceiver(receiver, new IntentFilter(UPDATE_ADVERTISE_CLOSE));
        handler.postDelayed(this, getResources().getInteger(R.integer.splash_duration));
        String pic = getIntent().getStringExtra("pic");
        DrawableTypeRequest<String> load = Glide.with(this).load(pic);
        load.diskCacheStrategy(DiskCacheStrategy.ALL);
        load.into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                iv.setBackground(resource);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(this);
        unregisterReceiver(receiver);
    }

    @Override
    public void run() {
        setResult(RESULT_OK);
        finish();
    }
}
