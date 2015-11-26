package maimeng.yodian.app.client.android.view.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Looper;
import android.view.KeyEvent;
import android.widget.Toast;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.YApplication;
import maimeng.yodian.app.client.android.downloads.DownloadAsyncTask;
import maimeng.yodian.app.client.android.downloads.DownloadHandler;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.VersionResponse;
import maimeng.yodian.app.client.android.network.service.CommonService;
import maimeng.yodian.app.client.android.view.dialog.AlertDialog;

/**
 * Created by android on 2015/8/24.
 */
public class CheckUpdateDelegate implements Callback<VersionResponse>, AlertDialog.PositiveListener {
    private final Activity mContext;
    private final boolean showMsg;
    private final CommonService service;
    public static boolean checking = false;

    public CheckUpdateDelegate(Activity activity, boolean showMsg) {
        this.mContext = activity;
        this.showMsg = showMsg;
        this.service = Network.getService(CommonService.class);
    }

    public void checkUpdate() {
        service.checkVersion(this);
    }

    @Override
    public void start() {
        checking = true;
    }

    private void showFaild() {
        if (showMsg)
            Toast.makeText(mContext, R.string.check_newversion_faild, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void success(VersionResponse res, Response response) {
        if (res.isSuccess()) {
            final String version = res.getData().getVersion();
            if (version == null) {
                if (showMsg) {
                    Toast.makeText(mContext, R.string.no_newversion, Toast.LENGTH_SHORT).show();
                }
                return;

            }
            if (version.compareTo(YApplication.versionName) > 0) {
                final String string = res.getData().getUrl();
                final AlertDialog dialog;
                final StringBuffer sb;
                if (res.getData().getType() != 1) {
                    sb = new StringBuffer("<b>有新版本是否更新:</b>");
                } else {
                    sb = new StringBuffer("<b>发现新版本:</b>");
                }
                sb.append("<br/><br/>");
                sb.append(res.getData().getContent());
                dialog = AlertDialog.newInstance("更新提示", sb.toString());
                dialog.setNegativeListener(new AlertDialog.NegativeListener() {
                    @Override
                    public void onNegativeClick(DialogInterface dialog) {
                        ProgressDialog dialog1 = new ProgressDialog(mContext);
                        dialog1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        dialog1.setTitle("下载中...");
                        dialog1.setMax(100);
                        dialog1.setProgress(0);
                        dialog1.show();
                        dialog1.setCancelable(false);
                        dialog1.setOnKeyListener(new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    return true;
                                }
                                return false;
                            }
                        });
                        DownloadHandler handler = new DownloadHandler(Looper.getMainLooper(), mContext, dialog1);
                        new DownloadAsyncTask(handler).execute(string);
                    }

                    @Override
                    public String negativeText() {
                        return "是";
                    }
                });
                if (res.getData().getType() != 1) {
                    dialog.setPositiveListener(this);
                } else {
                    dialog.setCancelable(false);
                }
                dialog.show(mContext.getFragmentManager(), "updateDialog");
            } else {
                if (showMsg)
                    Toast.makeText(mContext, R.string.no_newversion, Toast.LENGTH_SHORT).show();
            }
        } else {
            showFaild();
        }
    }

    @Override
    public void failure(HNetError hNetError) {

    }

    @Override
    public void end() {
        checking = false;
    }

    @Override
    public void onPositiveClick(DialogInterface dialog) {
        dialog.dismiss();
    }

    @Override
    public String positiveText() {
        return "否";
    }
}
