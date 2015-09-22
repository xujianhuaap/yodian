package maimeng.yodian.app.client.android.downloads

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Looper

import java.io.File

/**
 * Created by henjue on 2015/6/8.
 */
public class DonwloadHandler(mainLooper: Looper, private val mActivity: Activity, private val dialog: ProgressDialog) : android.os.Handler(mainLooper) {
    public fun sendProgress(max: Long?, progress: Long?) {
        dialog.max = max!!.toInt()
        dialog.progress = progress!!.toInt()
    }

    public fun complite(file: File) {
        dialog.dismiss()
        val intent = Intent()
        println("安装apk ：" + file.name + " : " + file.length() + "--" + file.path + "--" + file.canRead() + "--" + file.exists())
        intent.setAction("android.intent.action.VIEW")//向用户显示数据
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)//以新压入栈
        intent.addCategory("android.intent.category.DEFAULT")
        val uri = Uri.fromFile(file)
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        mActivity.startActivity(intent)
    }
}
