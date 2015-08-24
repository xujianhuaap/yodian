package maimeng.yodian.app.client.android.downloads;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;

import java.io.File;

/**
 * Created by henjue on 2015/6/8.
 */
public class DonwloadHandler extends android.os.Handler {
    private final ProgressDialog dialog;
    private final Activity mActivity;

    public DonwloadHandler(Looper mainLooper, Activity mActivity, ProgressDialog dialog) {
        super(mainLooper);
        this.dialog = dialog;
        this.mActivity = mActivity;
    }

    public void sendProgress(Long max, Long progress) {
        dialog.setMax(max.intValue());
        dialog.setProgress(progress.intValue());
    }

    public void complite(File file) {
        dialog.dismiss();
        Intent intent = new Intent();
        System.out.println("安装apk ：" + file.getName() + " : " + file.length() + "--" + file.getPath() + "--" + file.canRead() + "--" + file.exists());
        intent.setAction("android.intent.action.VIEW");//向用户显示数据
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//以新压入栈
        intent.addCategory("android.intent.category.DEFAULT");
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        mActivity.startActivity(intent);
    }
}
