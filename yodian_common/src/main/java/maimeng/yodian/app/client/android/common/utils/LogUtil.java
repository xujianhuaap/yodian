package maimeng.yodian.app.client.android.common.utils;

import android.content.Context;
import android.widget.Toast;

import maimeng.yodian.app.client.android.common.BuildConfig;


/**
 * 这个是日志打印工具类，在项目正式发布时，将isPrint设置为false则所有的日志不会打印在控制台
 *
 * @author 朱成
 */
public class LogUtil {
    // TODO ***********************SDK发布时请将此变量设置为私有的 **********************************
    private final static boolean isPrint = BuildConfig.DEBUG;
    // 增加丿تtest属瀧Ԩ于防止测试代码因疏忽导致没有关闿
    public final static boolean test = isPrint;

    // TODO ***********************SDK发布时请将上面变量设置为私有皿**********************************
    public static void i(String tag, String message) {
        if (isPrint) {
            if (tag != null && message != null && !"".equals(tag.trim())
                    && !"".equals(message.trim())) {
                android.util.Log.i(tag, message);
            }
        }
    }

    public static void i(String tag, String message, Object... args) {
        i(tag, String.format(message, args));
    }

    public static void d(String tag, String message) {
        if (isPrint) {
            if (tag != null && message != null && !"".equals(tag.trim())
                    && !"".equals(message.trim())) {
                android.util.Log.d(tag, message);
            }
        }
    }

    public static void d(String tag, String message, Object... args) {
        d(tag, String.format(message, args));
    }

    public static void e(String tag, Throwable e, String message) {
        if (isPrint) {
            if (tag != null && message != null && !"".equals(tag.trim())
                    && !"".equals(message.trim())) {
                android.util.Log.e(tag, message, e);
            }
        }
    }

    public static void e(String tag, String message, Object... args) {
        e(tag, null, String.format(message, args));
    }

    public static void e(String tag, Throwable e, String message, Object... args) {
        e(tag, e, String.format(message, args));
    }

    public static void w(String tag, String message) {
        if (isPrint) {
            if (tag != null && message != null && !"".equals(tag.trim())
                    && !"".equals(message.trim())) {
                android.util.Log.w(tag, message);
            }
        }
    }

    public static void w(String tag, String message, Object... args) {
        w(tag, String.format(message, args));
    }

    public static void e(Exception e) {
        if (isPrint) {
            if (e != null) {
                e.printStackTrace();
            }
        }
    }

    public static void showToast(Context context, String content) {
        if (isPrint) {
            if (context != null && content != null)
                Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
        }
    }
}
