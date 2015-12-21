package maimeng.yodian.app.client.android.downloads;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.os.Message;

import java.io.File;
import java.net.MalformedURLException;

import maimeng.yodian.app.client.android.utils.LogUtil;

/**
 * Created by android on 2015/10/12.
 */
public final class DownloadHandler extends android.os.Handler {
    private static final String LOG_TAG = DownloadHandler.class.getSimpleName();
    private final Activity mActivity;
    private final ProgressDialog dialog;

    public DownloadHandler(Looper looper, Activity mActivity, ProgressDialog dialog) {
        super(looper);
        this.mActivity = mActivity;
        this.dialog = dialog;
    }
    public static final int MSG_UPDATE_PROGRESS=0x1001;
    public static final int MSG_CLOSE_DIALOG=0x1002;
    public static final int MSG_COMPLITE=0x1003;
    public static final int MSG_ERROR=0x1004;
    public void sendProgress(int max, int progress) {
        obtainMessage(MSG_UPDATE_PROGRESS,max,progress).sendToTarget();
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what){
            case MSG_UPDATE_PROGRESS:
                dialog.setMax(msg.arg1/1014);
                dialog.setProgress(msg.arg2/1024);
                break;
            case MSG_CLOSE_DIALOG:
                LogUtil.d(LOG_TAG,"close dialog");
                dialog.dismiss();
                break;
            case MSG_ERROR:
                ((Throwable)msg.obj).printStackTrace();
                mActivity.finish();
                break;
            case MSG_COMPLITE:
                LogUtil.d(LOG_TAG,"Download Complite");
                File file=(File)msg.obj;
                if(file.exists()){
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");//向用户显示数据
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//以新压入栈
                    intent.addCategory("android.intent.category.DEFAULT");
                    Uri uri = Uri.fromFile(file);
                    intent.setDataAndType(uri, "application/vnd.android.package-archive");
                    mActivity.startActivity(intent);
                    LogUtil.d(LOG_TAG,"Install Apk:%s",file.toString());
                }
                break;
        }
    }

    public void complite(File file) {
        obtainMessage(MSG_CLOSE_DIALOG).sendToTarget();
        sendMessageDelayed(obtainMessage(MSG_COMPLITE,file),1000);
    }

    public void error(Throwable e) {
        obtainMessage(MSG_ERROR,e).sendToTarget();
    }
}
