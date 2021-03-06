package maimeng.yodian.app.client.android.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.melnykov.fab.FloatingActionButton;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import java.util.ArrayList;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.model.Float;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.response.AuthResponse;
import maimeng.yodian.app.client.android.network.response.FloatResponse;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.service.AuthService;
import maimeng.yodian.app.client.android.network.service.CommonService;
import maimeng.yodian.app.client.android.network.service.UserService;
import maimeng.yodian.app.client.android.service.ChatServiceLoginService;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.view.auth.AuthRedirect;
import maimeng.yodian.app.client.android.view.auth.AuthSeletorActivity;
import maimeng.yodian.app.client.android.view.common.AbstractActivity;
import maimeng.yodian.app.client.android.view.common.CheckUpdateDelegate;
import maimeng.yodian.app.client.android.view.common.FloatActivity;
import maimeng.yodian.app.client.android.view.dialog.WaitDialog;
import maimeng.yodian.app.client.android.view.skill.IndexFragment;
import maimeng.yodian.app.client.android.view.user.UserHomeFragment;

/**
 * Created by android on 15-10-10.
 */
public class MainTab2Activity extends AbstractActivity implements Callback<FloatResponse>, BDLocationListener {
    private static final String LOG_TAG = MainTab2Activity.class.getSimpleName();
    private User user;
    private Bitmap mAvatar;
    private long exitTime;
    private WaitDialog dialog;
    private LocationClient mLocationClient;

    @Override
    public FloatingActionButton getFloatButton() {
        return floatButton;
    }

    private FloatingActionButton floatButton;
    private static final int REQUEST_AUTH = 0x1001;//登陆
    private static final int REQUEST_UPDATEINFO = 0x1002;//更新个人信息
    private UserHomeFragment userHomeFragment;
    private IndexFragment indexFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.enable();
        mPushAgent.onAppStart();
        setContentView(R.layout.activity_yodian_main2, false);
        floatButton = (FloatingActionButton) findViewById(R.id.btn_float);
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
        floatButton.setShadow(false);
        new CheckUpdateDelegate(this, false).checkUpdate();

        Network.getService(CommonService.class).getFloat(this);
        Network.getService(AuthService.class).autologin(new Callback<AuthResponse>() {
            @Override
            public void start() {
            }

            @Override
            public void success(AuthResponse res, Response response) {

                if(res.isValidateAuth(MainTab2Activity.this) && res.isSuccess()){
                    initFragment();

                }
            }

            @Override
            public void failure(HNetError hNetError) {

            }

            @Override
            public void end() {
                FragmentTransaction bt = getSupportFragmentManager().beginTransaction();
                userHomeFragment = UserHomeFragment.newInstance();
                indexFragment = IndexFragment.newInstance();
                bt.add(R.id.container, userHomeFragment, UserHomeFragment.class.getName());
                bt.add(R.id.container, indexFragment, IndexFragment.class.getName());
//            bt.addToBackStack(null);
                bt.hide(userHomeFragment).show(indexFragment);
                bt.commitAllowingStateLoss();
                if (getIntent().hasExtra("home")) {
                    if (!userHomeFragment.isVisible()) {
                        floatButton.callOnClick();
                    }
                }
            }
        });
        updateFloatButton();
        mLocationClient=new LocationClient(getApplicationContext());
        LocationClientOption options=new LocationClientOption();
        options.setCoorType("wgs84");
        options.setIsNeedAddress(true);
        mLocationClient.setLocOption(options);
        mLocationClient.registerLocationListener(this);
        mLocationClient.start();
    }

    /***
     * 更新头像
     */
    private void updateFloatButton() {
        final int dpi = getResources().getDisplayMetrics().densityDpi;
        LogUtil.d(MainTab2Activity.class.getName(),"dpi: %d",dpi);
        final DataSource<CloseableReference<CloseableImage>> sub = Fresco.getImagePipeline().fetchDecodedImage(ImageRequest.fromUri(Uri.parse(User.read(this).getAvatar())),this);
        sub.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            protected void onNewResultImpl(final Bitmap bitmap) {
                final int dpi = getResources().getDisplayMetrics().densityDpi;
                final float[] rate = new float[1];
                if ( dpi <=160) {
                    rate[0] = 0.4f;
                } else if (dpi>160 && dpi <=320) {
                    rate[0] = 0.8f;
                } else if (dpi > 320 && dpi <=480) {
                    rate[0] = 0.8f;
                } else if (dpi > 480) {
                    rate[0] = 0.9f;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAvatar = getCircleBitmap(bitmap, rate[0]);
                        floatButton.setImageBitmap(mAvatar);
                    }
                });

                sub.close();
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                sub.close();
            }
        }, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    //悬浮广告
    private void syncFloat() {
        create(CommonService.class).getFloat(new Callback<FloatResponse>() {
            @Override
            public void start() {

            }

            @Override
            public void success(FloatResponse res, Response response) {
                if (res.isSuccess()) {
                    FloatResponse.Data list = res.getData();

                    if (list != null) {
                        ArrayList<Float> floats = list.getFloatPic();
                        if (floats != null && floats.size() > 0) {
                            FloatActivity.start(MainTab2Activity.this, floats);
                        }
                    }
                }
            }

            @Override
            public void failure(HNetError hNetError) {

            }

            @Override
            public void end() {

            }
        });
    }

    /**
     * @param bitmap
     * @param rate   半径的系数
     * @return
     */
    @NonNull
    private Bitmap getCircleBitmap(Bitmap bitmap, float rate) {
        int width = bitmap.getWidth();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        Bitmap bottomBitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bottomBitmap);
        int radius = (int) (width * rate / 2);
        canvas.drawCircle(width / 2, width / 2, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bottomBitmap;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (indexFragment.onKeyDown(keyCode, event) || userHomeFragment.onKeyDown(keyCode, event)) {
            return true;
        }
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            // 将系统当前的时间赋值给exitTime
            exitTime = System.currentTimeMillis();
            return true;
        } else {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void toggle() {
        FragmentTransaction bt = getSupportFragmentManager().beginTransaction();
        if (userHomeFragment.isHidden()) {
            if (indexFragment.isShowPop()) {
                indexFragment.toggleTypePop(null);
            }
            bt.setCustomAnimations(R.anim.translation_to_bottom_in, R.anim.translation_to_bottom_out);
            bt.show(userHomeFragment).hide(indexFragment);
            floatButton.setImageResource(R.mipmap.btn_home_change_normal);

        } else {
            bt.setCustomAnimations(R.anim.translation_to_top_in, R.anim.translation_to_top_out);
            bt.show(indexFragment).hide(userHomeFragment);
            if (mAvatar == null) {
                updateFloatButton();
            } else {
                floatButton.setImageBitmap(mAvatar);
            }


        }
        bt.commitAllowingStateLoss();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (TextUtils.isEmpty(User.read(this).getToken())) {
            AuthRedirect.toAuth(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    private void initFragment() {
        user = User.read(this);
        if (TextUtils.isEmpty(user.getToken())) {
            AuthSeletorActivity.start(this, REQUEST_AUTH);
        } else {
            startService(new Intent(this, ChatServiceLoginService.class));
            showDefault();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    syncFloat();
                }
            }, 500);
        }
    }

    private void showDefault() {

    }

    @Override
    public void start() {

    }

    @Override
    public void success(FloatResponse res, Response response) {
        if (res.isSuccess()) {
            if (res.getData().getFloatPic().size() > 0) {
//                FloatActivity.start(this, res.getData().getFloatPic());
            }
        }
    }

    @Override
    public void failure(HNetError hNetError) {
        checkError(hNetError);
    }

    @Override
    public void end() {

    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        StringBuffer sb = new StringBuffer(256);
        sb.append("time : ");
        sb.append(location.getTime());
        sb.append("\nerror code : ");
        int locType = location.getLocType();
        sb.append(locType);
        sb.append("\nlatitude : ");
        sb.append(location.getLatitude());
        sb.append("\nlontitude : ");
        sb.append(location.getLongitude());
        sb.append("\nradius : ");
        sb.append(location.getRadius());
        sb.append("\naddr : ");
        sb.append(location.getAddrStr());
        LogUtil.i(LOG_TAG,sb.toString());
        if(locType==BDLocation.TypeNetWorkLocation || locType== BDLocation.TypeGpsLocation || locType==BDLocation.TypeOffLineLocation){
            mLocationClient.stop();
            Network.getService(UserService.class).userLocation(location.getLongitude(),location.getLatitude(),new ToastCallback(this){
                @Override
                public void success(ToastResponse toastResponse, Response response) {
                }
            });
        }
    }
}
