package maimeng.yodian.app.client.android;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import maimeng.yodian.app.client.android.model.User;
import maimeng.yodian.app.client.android.view.auth.AuthSeletorActivity;

/**
 * Created by android on 2015/8/16.
 */
public class ConnectionConflictBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        User.clear(context);
        new AlertDialog.Builder(context).setTitle("提示").setMessage(maimeng.yodian.app.client.android.R.string.connect_conflict).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, AuthSeletorActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                dialog.dismiss();
                YApplication.getInstance().logout();
            }
        }).show();
    }
}
