package maimeng.yodian.app.client.android.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import maimeng.yodian.app.client.android.R;


/**
 * Created by henjue on 2015/4/7.
 */
public class WaitDialog extends DialogFragment {
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
        if(!activity.isFinishing()){
            FragmentManager fragmentManager = activity.getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            WaitDialog waitDialog = (WaitDialog) fragmentManager.findFragmentByTag(activity.hashCode() + "");
            if (waitDialog == null) {
                waitDialog = newInstance(message);
            }
            if (!waitDialog.isVisible()) {
                if(!activity.isFinishing()){
                    waitDialog.show(transaction, activity.hashCode() + "");
                }

            }
            return waitDialog;
        }
        return null;

    }


    public static WaitDialog show(Activity activity) {
        return show(activity, "请稍等...");
    }

    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {

        View inflate = getActivity().getLayoutInflater().inflate(R.layout.dialog_wait, null, false);
        imageView = (ImageView) inflate.findViewById(R.id.loading);
        Drawable image = imageView.getDrawable();
        android.graphics.drawable.AnimationDrawable drawable = (android.graphics.drawable.AnimationDrawable) image;
        drawable.start();

        TextView tv = (TextView) inflate.findViewById(R.id.title);
        String string = getArguments().getString("alert-message");
        if (string != null) {
            tv.setText(string);
        } else {
            tv.setVisibility(View.GONE);
        }
        Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(inflate);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void dismiss() {
        if(!getActivity().isFinishing()){
            super.dismiss();
        }

    }
}
