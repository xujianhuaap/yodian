package maimeng.yodian.app.client.android.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.melnykov.fab.FloatingActionButton;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.common.LauncherCheck;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.loader.Circle;
import maimeng.yodian.app.client.android.network.loader.ImageLoaderManager;
import maimeng.yodian.app.client.android.network.response.FloatResponse;
import maimeng.yodian.app.client.android.network.service.CommonService;
import maimeng.yodian.app.client.android.service.ChatServiceLoginService;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.view.auth.AuthRedirect;
import maimeng.yodian.app.client.android.view.auth.AuthSeletorActivity;
import maimeng.yodian.app.client.android.view.auth.AuthSettingInfoActivity;
import maimeng.yodian.app.client.android.view.common.FloatActivity;
import maimeng.yodian.app.client.android.view.dialog.AlertDialog;
import maimeng.yodian.app.client.android.view.skill.IndexFragment;
import maimeng.yodian.app.client.android.view.user.UserHomeFragment;

/**
 * Created by android on 15-10-10.
 */
public class MainTab2Activity extends AbstractActivity implements AlertDialog.PositiveListener, Callback<FloatResponse> {
    private User user;
    private Bitmap mAvatar;

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
        if (LauncherCheck.isFirstRun(this)) {
            finish();
        } else {
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
            FragmentTransaction bt = getSupportFragmentManager().beginTransaction();
            userHomeFragment = UserHomeFragment.newInstance();
            indexFragment = IndexFragment.newInstance();
            bt.add(R.id.container, userHomeFragment, UserHomeFragment.class.getName());
            bt.add(R.id.container, indexFragment, IndexFragment.class.getName());
//            bt.addToBackStack(null);
            bt.hide(userHomeFragment).show(indexFragment);
            bt.commitAllowingStateLoss();
            initFragment();
            Network.getService(CommonService.class).getFloat(this);
            final float density =getResources().getDisplayMetrics().density;
            int width=(int)(80*density);
            new ImageLoaderManager.Loader(floatButton,Uri.parse(User.read(this).getAvatar()))
                    .width(width).height(width).callback(new ImageLoaderManager.Callback() {
                @Override
                public void onImageLoaded(Bitmap bitmap) {
                    final int dpi =getResources().getDisplayMetrics().densityDpi;
                    float rate=0;
                    if(dpi>140&&dpi<180){
                        rate=0.5f;
                    }else if(300<dpi&&dpi<320){
                        rate=0.6f;
                    }else if(dpi>=460&&dpi<500){
                        rate=0.8f;
                    }else if(dpi>640){
                        rate=0.9f;
                    }

                    mAvatar = getCircleBitmap(bitmap,rate);
                    floatButton.setImageBitmap(mAvatar);
                }

                @Override
                public void onLoadEnd() {

                }

                @Override
                public void onLoadFaild() {

                }
            }).start(this);
            if (getIntent().hasExtra("home")) {
                floatButton.callOnClick();
            }
        }
    }

    /**
     *
     * @param bitmap
     * @param rate  半径的系数
     * @return
     */
    @NonNull
    private Bitmap getCircleBitmap(Bitmap bitmap,float rate) {
        int width = bitmap.getWidth();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        Bitmap bottomBitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bottomBitmap);
        int radius=(int)(width*rate/2);
        LogUtil.d(MainTab2Activity.class.getName(),"radius"+radius);
        canvas.drawCircle(width / 2, width / 2,radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bottomBitmap;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (indexFragment.onKeyDown(keyCode, event) || userHomeFragment.onKeyDown(keyCode, event)) {
            return true;
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
            floatButton.setImageBitmap(mAvatar);

        }
        bt.commitAllowingStateLoss();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(User.read(this).getToken())) {
            AuthRedirect.toAuth(this);
        }
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onPositiveClick(DialogInterface dialog) {
        startActivityForResult(new Intent(this, AuthSettingInfoActivity.class), REQUEST_UPDATEINFO);
    }

    @Override
    public String positiveText() {
        return "是";
    }

    private void initFragment() {
        user = User.read(this);
        if (TextUtils.isEmpty(user.getToken())) {
            AuthSeletorActivity.start(this, REQUEST_AUTH);
        } else {
            startService(new Intent(this, ChatServiceLoginService.class));
            if (TextUtils.isEmpty(user.getNickname()) || TextUtils.isEmpty(user.getAvatar())) {
                AlertDialog dialog = AlertDialog.newInstance("资料补全", "你的资料不完整，请补全资料!");
                dialog.setCancelable(false);
                dialog.setPositiveListener(this);
                dialog.show(getFragmentManager(), "dialog");
            } else {
                showDefault();
            }
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
                FloatActivity.start(this, res.getData().getFloatPic());
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
}
