
package maimeng.yodian.app.client.android;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.easemob.chat.EMChat;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.push.FeedbackPush;

import org.henjue.library.share.ShareSDK;

import maimeng.yodian.app.client.android.chat.DemoApplication;
import maimeng.yodian.app.client.android.constants.ApiConfig;
import maimeng.yodian.app.client.android.network.Network;

/**
 * Created by android on 15-7-13.
 */
public class YApplication extends DemoApplication {
    public static int channelId = -1;
    public static String channelName;
    public static int versionCode;

    @Override
    public void onCreate() {
        super.onCreate();
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
}
