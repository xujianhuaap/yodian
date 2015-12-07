package maimeng.yodian.app.client.android.view.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.graphics.drawable.ClipDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.UmengRegistrar;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;
import org.henjue.library.share.AuthListener;
import org.henjue.library.share.Type;
import org.henjue.library.share.manager.AuthFactory;
import org.henjue.library.share.manager.IAuthManager;
import org.henjue.library.share.manager.WeiboAuthManager;
import org.henjue.library.share.model.AuthInfo;

import java.io.IOException;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.common.UEvent;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.AuthResponse;
import maimeng.yodian.app.client.android.network.service.AuthService;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.view.MainTab2Activity;
import maimeng.yodian.app.client.android.view.common.AbstractActivity;
import maimeng.yodian.app.client.android.view.common.WebViewActivity;
import maimeng.yodian.app.client.android.view.dialog.WaitDialog;
import maimeng.yodian.app.client.android.widget.ScrollImageView;

public class AuthSeletorActivity extends AbstractActivity implements View.OnClickListener {
    private static AuthInfo authInfo;
    private SsoHandler mSsoHandler;
    private AuthService service;
    private WaitDialog dialog;
    private static final int REQUEST_MOBILE_AUTH = 0x5001;
    private BitmapRegionDecoder decoder;
    int left, right, top, bottom;
    private Bitmap bitmap;
    boolean toRight = true;
    private static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        service = Network.getService(AuthService.class);
        setContentView(R.layout.activity_auth_selector, false);
        left = 0;
        right = getResources().getDisplayMetrics().widthPixels;
        bottom = getResources().getDisplayMetrics().heightPixels;
        top = 0;
        final ImageView image = (ImageView) findViewById(R.id.scroll_image);
        final int seek = 2;
        try {
            decoder = BitmapRegionDecoder.newInstance(getResources().openRawResource(R.raw.scroll_bitmap), false);
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                    bitmap = decoder.decodeRegion(new Rect(left, top, right, bottom), null);
                    if (toRight) {
                        if (right + seek < decoder.getWidth()) {
                            left += seek;
                            right += seek;
                        } else {
                            toRight = false;
                            left -= seek;
                            right -= seek;
                        }
                    } else {
                        if (left - seek > 0) {
                            left -= seek;
                            right -= seek;
                        } else {
                            toRight = true;
                            left += seek;
                            right += seek;
                        }
                    }
                    image.setImageBitmap(bitmap);
                    obtainMessage(0).sendToTarget();
                }
            };
            handler.sendEmptyMessage(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        findViewById(R.id.btn_loginwechat).setOnClickListener(this);
        findViewById(R.id.btn_loginweibo).setOnClickListener(this);
        findViewById(R.id.btn_loginphone).setOnClickListener(this);
        findViewById(R.id.btn_user_protocol).setOnClickListener(this);
        User.clear(AuthSeletorActivity.this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_loginphone) {
            startActivityForResult(new Intent(this, AuthActivity.class), REQUEST_MOBILE_AUTH);
        } else if (v.getId() == R.id.btn_loginwechat) {
            IAuthManager authManager = AuthFactory.create(this, Type.Platform.WEIXIN);
            authManager.login(new YDAuthListener(Type.Platform.WEIXIN));
        } else if (v.getId() == R.id.btn_loginweibo) {
            IAuthManager authManager = AuthFactory.create(this, Type.Platform.WEIBO);
            authManager.login(new YDAuthListener(Type.Platform.WEIBO));
        } else if (v.getId() == R.id.btn_user_protocol) {
            WebViewActivity.show(this, "http://www.ketie.me/ydxy.html");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MOBILE_AUTH && resultCode == RESULT_OK) {
            handlerFinsh();
        } else {
            mSsoHandler = WeiboAuthManager.getSsoHandler();
            if (mSsoHandler != null) {
                mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
        }
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, AuthSeletorActivity.class));
    }

    public static void start(Activity context, int requestCode) {
        context.startActivityForResult(new Intent(context, AuthSeletorActivity.class).putExtra("result", true), requestCode);
    }

    private void handlerFinsh() {
        Intent intent = getIntent();
        boolean result = intent.getBooleanExtra("result", false);
        setResult(RESULT_OK);
        if (result) {
            finish();
        } else {
            startActivity(new Intent(AuthSeletorActivity.this, MainTab2Activity.class));
            finish();
        }
    }

    class YDAuthListener implements AuthListener, Callback<AuthResponse> {
        private final Type.Platform type;
        private int typeValue = 0;

        YDAuthListener(Type.Platform type) {
            this.type = type;
            if (this.type == Type.Platform.WEIBO) {
                typeValue = 1;
            } else if (this.type == Type.Platform.WEIXIN) {
                typeValue = 2;
            }
        }

        @Override
        public void onComplete(AuthInfo authInfo) {
            AuthSeletorActivity.authInfo = authInfo;
            service.thirdParty(typeValue, authInfo.token, authInfo.id, UmengRegistrar.getRegistrationId(AuthSeletorActivity.this), this);
        }

        @Override
        public void onError() {
            LogUtil.e(AuthSeletorActivity.class.getName(), "onError");
        }

        @Override
        public void onCancel() {
            LogUtil.d(AuthSeletorActivity.class.getName(), "onCancel");
        }

        @Override
        public void start() {
            dialog = WaitDialog.show(AuthSeletorActivity.this);
            if (typeValue == 1) {
                MobclickAgent.onEvent(AuthSeletorActivity.this, UEvent.AUTH_WECHAT);
            } else if (typeValue == 2) {
                MobclickAgent.onEvent(AuthSeletorActivity.this, UEvent.AUTH_SINA);
            }
        }

        @Override
        public void success(AuthResponse res, Response response) {
            if (res.isSuccess()) {
                if (typeValue == 1) {
                    MobclickAgent.onEvent(AuthSeletorActivity.this, UEvent.AUTH_WECHAT_SUCESS);
                } else if (typeValue == 2) {
                    MobclickAgent.onEvent(AuthSeletorActivity.this, UEvent.AUTH_SINA_SUCESS);
                }
                User.clear(AuthSeletorActivity.this);
                User data = res.getData();
                data.setLoginType(typeValue);
                data.setT_nickname(authInfo.nickname);
                data.setT_img(authInfo.headimgurl);
                data.write(AuthSeletorActivity.this);
                handlerFinsh();
            } else {
                res.showMessage(AuthSeletorActivity.this);
            }
        }

        @Override
        public void failure(HNetError hNetError) {
            ErrorUtils.checkError(AuthSeletorActivity.this, hNetError);
        }

        @Override
        public void end() {
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }
}
