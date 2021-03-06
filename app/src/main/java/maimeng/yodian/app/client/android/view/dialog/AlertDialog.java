package maimeng.yodian.app.client.android.view.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;


/**
 * Created by henjue on 2015/4/7.
 */
public class AlertDialog extends DialogFragment {
    private NegativeListener nListener;
    private PositiveListener pListener;

    public static AlertDialog newInstance(String title, String message) {
        AlertDialog adf = new AlertDialog();
        Bundle bundle = new Bundle();
        if (!TextUtils.isEmpty(title)){
            bundle.putString("alert-title", title);
        }
        bundle.putString("alert-message", message);
        adf.setArguments(bundle);
        return adf;
    }

    public AlertDialog setNegativeListener(NegativeListener nListener) {
        this.nListener = nListener;
        return this;
    }

    public AlertDialog setPositiveListener(PositiveListener pListener) {
        this.pListener = pListener;
        return this;
    }

    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setMessage(Html.fromHtml(getArguments().getString("alert-message")));
        builder.setTitle(getArguments().getString("alert-title"));
        if (this.pListener != null) {
            builder.setPositiveButton(pListener.positiveText(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    pListener.onPositiveClick(dialog);
                }
            });
        }
        if (nListener != null) {
            builder.setNegativeButton(nListener.negativeText(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    nListener.onNegativeClick(dialog);
                }
            });
        }

        return builder.create();
    }

    public interface NegativeListener {
        void onNegativeClick(DialogInterface dialog);

        String negativeText();
    }

    public interface PositiveListener {
        void onPositiveClick(DialogInterface dialog);

        String positiveText();
    }


}
