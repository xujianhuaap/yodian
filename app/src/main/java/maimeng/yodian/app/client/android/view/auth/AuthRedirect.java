package maimeng.yodian.app.client.android.view.auth;

import android.content.Context;
import android.content.Intent;

import maimeng.yodian.app.client.android.view.MainTab2Activity;

/**
 * Created by xujianhua on 9/2/15.
 */
public class AuthRedirect {
    public static void toAuth(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, AuthSeletorActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void toHome(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, MainTab2Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
