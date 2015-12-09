
package maimeng.yodian.app.client.android;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import com.easemob.chat.EMChat;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.push.FeedbackPush;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import org.henjue.library.share.ShareSDK;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import maimeng.yodian.app.client.android.chat.DemoApplication;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.view.auth.AuthSeletorActivity;
import maimeng.yodian.app.client.android.view.deal.OrderDetailActivity;
import maimeng.yodian.app.client.android.view.user.UserHomeActivity;

/**
 * Created by android on 15-7-13.
 */
public class YApplication extends DemoApplication {
    private final Object lock = new Object();
    public static int channelId = -1;
    public static String channelName;
    public static int versionCode;
    public static String versionName = "";
    private List<Activity> activityList = new ArrayList<>();
    private static YApplication instance;


    public static YApplication getInstance() {
        return instance;
    }

    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void logout() {
        Intent intent = new Intent(this, AuthSeletorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        for (Activity activity : activityList) {
            activity.finish();
            if (activity instanceof AuthSeletorActivity) {

            } else {
                activity.finish();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                activityList.add(activity);
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                activityList.add(activity);
            }
        });
        try {
            ApplicationInfo appInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            versionCode = this.getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            versionName = this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            channelId = appInfo.metaData.getInt("CHANNEL_ID");
            channelName = appInfo.metaData.getString("UMENG_CHANNEL", "unspecified");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (!BuildConfig.DEBUG) {
            MobclickAgent.setCatchUncaughtExceptions(false);
            CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);
            strategy.setAppChannel(channelName);
            strategy.setAppReportDelay(5000);
            strategy.setCrashHandleCallback(new CrashReport.CrashHandleCallback() {
                @Override
                public synchronized Map<String, String> onCrashHandleStart(int crashType, String errorType, String errorMessage, String errorStack) {
                    exit();
                    return super.onCrashHandleStart(crashType, errorType, errorMessage, errorStack);
                }

                @Override
                public synchronized byte[] onCrashHandleStart2GetExtraDatas(int crashType, String errorType, String errorMessage, String errorStack) {
                    return super.onCrashHandleStart2GetExtraDatas(crashType, errorType, errorMessage, errorStack);
                }
            });
            CrashReport.initCrashReport(this, "900004839", BuildConfig.DEBUG, strategy);  //初始化SDK
        }
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setNotificationClickHandler(new UmengNotificationClickHandler() {

            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                try {
                    JSONObject custom = msg.getRaw().getJSONObject("extra").getJSONObject("custom");
                    int type = custom.getInt("yd_type");//1技能;2人;3聊天;4订单
                    long id = custom.getLong("yd_yid");
                    LogUtil.i("YApplication", custom.toString());
                    switch (type) {
                        case 2:
                            startActivity(new Intent(context, UserHomeActivity.class).putExtra("uid", id).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            break;
                        case 4:
                            startActivity(new Intent(context, OrderDetailActivity.class).putExtra("oid", id).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        mPushAgent.setMessageHandler(new UmengMessageHandler() {
            @Override
            public Notification getNotification(Context context, UMessage msg) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                builder.setAutoCancel(true);
                builder.setSmallIcon(R.mipmap.icon_app);
                builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_app));
                builder.setWhen(System.currentTimeMillis());
                builder.setTicker("msg.text");
                builder.setContentTitle(msg.title);
                builder.setContentText(msg.text);
                Notification build = builder.build();
                build.defaults = Notification.DEFAULT_SOUND;
                return build;
            }
        });
        FeedbackPush.getInstance(this).init(true);
        Network.getOne().init(this);
        ShareSDK.getInstance().initShare(BuildConfig.WEIXIN_APP_KEY, BuildConfig.WEIBO_APP_KEY, BuildConfig.QQ_APP_KEY, BuildConfig.WEIXIN_APP_SECRET, BuildConfig.REDIRECT_URL);
        EMChat.getInstance().setDebugMode(BuildConfig.DEBUG);
        MobclickAgent.setDebugMode(BuildConfig.DEBUG);
//        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
//                .setMainDiskCacheConfig(DiskCacheConfig.newBuilder().build())
//                .build();
//        Fresco.initialize(this, config);
        Fresco.initialize(this);

    }

    public User getAuthUser() {
        return authUser;
    }

    public void setAuthUser(User authUser) {
        this.authUser = authUser;
        if (!BuildConfig.DEBUG) {
            if (authUser != null) {
                final String nickname = authUser.getNickname();
                if (!TextUtils.isEmpty(nickname)) {
                    CrashReport.setUserId(nickname);
                }
            }
        }
    }

    private User authUser;

    @Override
    public String getAppKey() {
        String s = BuildConfig.DEBUG ? "maimengkeji#youdiantest" : "maimengkeji#youdian";
        return s;
//        return "maimengkeji#youdian";
    }
}
