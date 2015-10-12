package maimeng.yodian.app.client.android.network;

import android.content.Context;
import android.widget.Toast;

import com.tencent.bugly.crashreport.CrashReport;

import org.henjue.library.hnet.exception.HNetError;

import maimeng.yodian.app.client.android.BuildConfig;
import maimeng.yodian.app.client.android.utils.LogUtil;


/**
 * Created by android on 15-6-29.
 */
public class ErrorUtils {
    public static void checkError(Context context, HNetError error) {
        if (error != null) {
            if (error.getKind() == HNetError.Kind.HTTP) {
                postCatchedException(error);
                LogUtil.e(ErrorUtils.class.getName(), error, "%s\n%d\n", error.getUrl(), error.getResponse().getStatus());
                Toast.makeText(context, "连接服务器出错了...", Toast.LENGTH_SHORT).show();
            } else if (error.getKind() == HNetError.Kind.NETWORK) {
                if(error.getResponse()!=null){
                    LogUtil.e(ErrorUtils.class.getName(), "%s\n%d\n", error.getUrl(), error.getResponse().getStatus(), error);
                }
                Toast.makeText(context, "和服务器通讯失败,请检查网络...", Toast.LENGTH_SHORT).show();
            } else if (error.getKind() == HNetError.Kind.CONVERSION) {
                postCatchedException(error);
                LogUtil.e(ErrorUtils.class.getName(), error, "%s\n%d\n", error.getUrl(), error.getResponse().getStatus());
                Toast.makeText(context, "不能处理服务器返回给我的数据....", Toast.LENGTH_SHORT).show();
            } else {
                postCatchedException(error);
                if(error.getResponse()!=null){
                    LogUtil.e(ErrorUtils.class.getName(), error, "%s\n%d\n", error.getUrl(), error.getResponse().getStatus());
                }

                Toast.makeText(context, "运行出错啦...", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private static void postCatchedException(HNetError error) {
        if (!BuildConfig.DEBUG) {
            CrashReport.postCatchedException(new Throwable(error.getUrl(), error));
        }
    }
}
