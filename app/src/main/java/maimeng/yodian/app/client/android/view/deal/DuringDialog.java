package maimeng.yodian.app.client.android.view.deal;

import android.app.Dialog;
import android.app.DialogFragment;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.databinding.DuringDialogBinding;

/**
 * Created by android on 2015/9/22.
 */
public class DuringDialog extends DialogFragment implements View.OnClickListener {
    private DuringDialogBinding binding;

    /**
     * @param activity
     * @param max      最多多少金额
     */
    public static void show(RemainderInfoActivity activity, double max) {
        DuringDialog dialog = new DuringDialog();
        Bundle args = new Bundle();
        args.putDouble("max", max);
        dialog.setArguments(args);
        dialog.show(activity.getFragmentManager(), "_duringDialog");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.during_dialog, container, false);
        binding.btnSubmit.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        final double during = Double.parseDouble(binding.money.getText().toString());
        if (during < 50) {
            binding.title.setText(Html.fromHtml(getString(R.string.during_dialog_title_error)));
        } else {
            binding.title.setText(R.string.during_dialog_title);
            RemainderInfoActivity activity = (RemainderInfoActivity) getActivity();
            activity.onInputDuring(Double.parseDouble(binding.money.getText().toString()));
            dismiss();
        }
    }
}
