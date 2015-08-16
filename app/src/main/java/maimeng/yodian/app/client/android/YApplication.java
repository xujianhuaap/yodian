
package maimeng.yodian.app.client.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.easemob.chat.EMChat;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.push.FeedbackPush;

import org.henjue.library.share.ShareSDK;

import java.util.ArrayList;
import java.util.List;

import maimeng.yodian.app.client.android.chat.DemoApplication;
import maimeng.yodian.app.client.android.common.Action;
import maimeng.yodian.app.client.android.constants.ApiConfig;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.view.auth.AuthSeletorActivity;

/**
 * Created by android on 15-7-13.
 */
public class YApplication extends DemoApplication {
    public static int channelId = -1;
    public static String channelName;
    public static int versionCode;
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
        IntentFilter filter = new IntentFilter(Action.ACTION_LOGOUT);
        registerReceiver(new ConnectionConflictBroadcastReceiver(), filter);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                activityList.add(activity);
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
            CrashReport.initCrashReport(this, "900004839", BuildConfig.DEBUG, strategy);  //初始化SDK
        }
        FeedbackPush.getInstance(this).init(true);
        Network.getOne().init(this);
        ShareSDK.getInstance().initShare(ApiConfig.WEIXIN_APP_KEY, ApiConfig.WEIBO_APP_KEY, ApiConfig.QQ_APP_KEY, ApiConfig.WEIXIN_APP_SECRET, ApiConfig.REDIRECT_URL);
//        EMChat.getInstance().init(this);
        EMChat.getInstance().setDebugMode(BuildConfig.DEBUG);
    }

    @Override
    public String getAppKey() {
        return BuildConfig.DEBUG ? "maimengkeji#youdiantest" : "maimengkeji#youdian";
    }


}
