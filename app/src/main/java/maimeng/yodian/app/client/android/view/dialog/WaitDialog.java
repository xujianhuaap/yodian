package maimeng.yodian.app.client.android.view.dialog;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.UUID;

import maimeng.yodian.app.client.android.R;


/**
 * Created by henjue on 2015/4/7.
 */
public class WaitDialog extends android.app.DialogFragment {
    private ImageView imageView;

    @Deprecated
    public static WaitDialog newInstance(String message) {
        WaitDialog adf = new WaitDialog();
        Bundle bundle = new Bundle();
        bundle.putString("alert-message", message);
        adf.setArguments(bundle);
        return adf;
    }

    public static WaitDialog show(Activity activity, String message) {
        WaitDialog waitDialog = newInstance(message);
        FragmentManager fragmentManager = activity.getFragmentManager();
        waitDialog.show(fragmentManager, UUID.randomUUID().toString());
        return waitDialog;
    }

    public static WaitDialog show(Activity activity) {
        return show(activity,"请稍等...");
    }

    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity(), R.style.WaitDialogStyle);
        builder.setCancelable(false);
        View inflate = getActivity().getLayoutInflater().inflate(R.layout.dialog_wait, null, false);
        builder.setView(inflate);
        imageView = (ImageView) inflate.findViewById(R.id.loading);
        Drawable image = imageView.getDrawable();
        android.graphics.drawable.AnimationDrawable drawable = (android.graphics.drawable.AnimationDrawable) image;
        drawable.start();
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
        TextView tv=(TextView)inflate.findViewById(R.id.title);
        String string = getArguments().getString("alert-message");
        if(string !=null){
            tv.setText(string);
        }else{
            tv.setVisibility(View.GONE);
        }
        android.app.AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }
}
