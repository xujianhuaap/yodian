package maimeng.yodian.app.client.android.view.splash;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.ImageView;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import maimeng.yodian.app.client.android.common.AdvertiseStatus;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.service.CommonService;


/**
 * Created by android on 2015/7/3.
 */
public abstract class AbsSplashActivity extends AppCompatActivity implements Callback<String> {
    private final Handler handler = new Handler();
    private boolean show =true;

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
                Realm instance = Realm.getInstance(AbsSplashActivity.this);
                AdvertiseStatus obj = instance.where(AdvertiseStatus.class).findFirst();
                if(obj!=null && !TextUtils.isEmpty(obj.getPic())&& obj.isShow() && show){
                    startActivity(new Intent(AbsSplashActivity.this,SplashAdvertiseActivity.class).putExtra("pic",obj.getPic()));
                }
                instance.close();
                onTimeout();
            }
        }, timeout());
        updateAdvertise();
    }

    private void updateAdvertise() {
        Network.getService(CommonService.class).getFloatADV(this);
    }

    protected abstract void onTimeout();

    protected abstract long timeout();

    protected abstract Drawable splash();

    @Override
    public final void start() {

    }

    @Override
    public final void success(String s, Response response) {
        try {
            JSONObject json = new JSONObject(s);
            if(json.getInt("code")==20000 && json.has("data")){
                JSONObject splash = json.getJSONObject("data").getJSONObject("splash");
                String pic = splash.getString("pic");
                int flg = splash.getInt("flag");
                Realm realm = Realm.getInstance(this);
                realm.beginTransaction();
                AdvertiseStatus first = realm.where(AdvertiseStatus.class).findFirst();
                if(first!=null)first.removeFromRealm();
                AdvertiseStatus obj = realm.createObject(AdvertiseStatus.class);
                obj.setPic(pic);
                obj.setShow(flg == 1);
                System.out.println(obj.isShow());
                show =obj.isShow();
                realm.commitTransaction();
                if(obj.isShow()){
                    sendBroadcast(new Intent(SplashAdvertiseActivity.UPDATE_ADVERTISE_CLOSE));
                }
                realm.close();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public final void failure(HNetError hNetError) {

    }

    @Override
    public final void end() {

    }
}
