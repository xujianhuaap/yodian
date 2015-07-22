package maimeng.yodian.app.client.android.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by android on 15-4-28.
 */
public class LauncherCheck {
    private static final String PREFERENCES_NAME = "LauncherCheck";

    public static boolean isFirstRun(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        return pref.getBoolean("_fristRun", true);
    }

    public static void updateFirstRun(Context context, boolean frist) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        pref.edit().putBoolean("_fristRun", frist).apply();
    }
}
