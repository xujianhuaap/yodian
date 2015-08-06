package maimeng.yodian.app.client.android.network;

import android.content.Context;
import android.widget.Toast;


import com.tencent.bugly.crashreport.CrashReport;

import org.henjue.library.hnet.exception.HNetError;

import maimeng.yodian.app.client.android.BuildConfig;


/**
 * Created by android on 15-6-29.
 */
public class ErrorUtils {
    public static void checkError(Context context,HNetError error){
        if(error!=null){
            if(error.getKind()== HNetError.Kind.HTTP ){
                if(!BuildConfig.DEBUG) {
                    CrashReport.postCatchedException(error);
                }else{
                    error.printStackTrace();
                }
                Toast.makeText(context, "连接服务器出错了...", Toast.LENGTH_SHORT).show();
            }else if(error.getKind()== HNetError.Kind.NETWORK){
                error.printStackTrace();
                Toast.makeText(context, "和服务器通讯失败...", Toast.LENGTH_SHORT).show();
            }else if(error.getKind()== HNetError.Kind.CONVERSION){
                if(!BuildConfig.DEBUG) {
                    CrashReport.postCatchedException(error);
                }else{
                    error.printStackTrace();
                }
                Toast.makeText(context, "不能处理服务器返回给我的数据....", Toast.LENGTH_SHORT).show();
            }else{
                if(!BuildConfig.DEBUG) {
                    CrashReport.postCatchedException(error);
                }else{
                    error.printStackTrace();
                }
                Toast.makeText(context, "运行出错啦...", Toast.LENGTH_SHORT).show();
            }

        }

    }
}
