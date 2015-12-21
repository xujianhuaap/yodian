package maimeng.yodian.app.client.android.view.splash;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.view.WindowManager;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.widget.YDView;

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
        final YDView iv = new YDView(this);
        setContentView(iv,new ViewGroup.LayoutParams(getResources().getDisplayMetrics().widthPixels,getResources().getDisplayMetrics().heightPixels));
        registerReceiver(receiver, new IntentFilter(UPDATE_ADVERTISE_CLOSE));
        handler.postDelayed(this, getResources().getInteger(R.integer.splash_duration));
        String pic = getIntent().getStringExtra("pic");
        iv.setImageURI(Uri.parse(pic));
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
