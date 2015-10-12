package maimeng.yodian.app.client.android.view.auth;

import android.content.Context;
import android.content.Intent;

/**
 * Created by xujianhua on 9/2/15.
 */
public class AuthRedirect {
    public static void toAuth(Context context){
        Intent intent=new Intent();
        intent.setClass(context,AuthSeletorActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void toHome(Context context){
        Intent intent=new Intent();
        intent.setClass(context,AuthSeletorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
