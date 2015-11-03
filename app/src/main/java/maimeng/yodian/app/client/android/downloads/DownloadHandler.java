package maimeng.yodian.app.client.android.downloads;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;

import java.io.File;

/**
 * Created by android on 2015/10/12.
 */
public final class DownloadHandler extends android.os.Handler {
    private final Activity mActivity;
    private final ProgressDialog dialog;

    public DownloadHandler(Looper looper, Activity mActivity, ProgressDialog dialog) {
        super(looper);
        this.mActivity = mActivity;
        this.dialog = dialog;
    }

    public void sendProgress(int max, int progress) {
        dialog.setMax(max);
        dialog.setProgress(progress);
    }

    public void complite(File file) {
        dialog.dismiss();
        if(file.exists()){
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");//向用户显示数据
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//以新压入栈
            intent.addCategory("android.intent.category.DEFAULT");
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            mActivity.startActivity(intent);
        }

    }
}
