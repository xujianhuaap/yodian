package maimeng.yodian.app.client.android.view.deal;

import android.app.Dialog;
import android.app.DialogFragment;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.databinding.DuringDialogBinding;
import maimeng.yodian.app.client.android.utils.LogUtil;

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
        String money=binding.money.getText().toString();
        if(TextUtils.isEmpty(money)){
            Toast.makeText(v.getContext(),"请填写提款金额",Toast.LENGTH_SHORT).show();
            return;
        }
        Pattern pattern=Pattern.compile("[0-9]*.[0-9]*");
        Matcher m=pattern.matcher(money);
        if(!m.matches()){
            Toast.makeText(v.getContext(),"填写格式不正确",Toast.LENGTH_SHORT).show();
            return;
        }
        final double during = Double.parseDouble(money);
        if (during < 50) {
            binding.title.setText(Html.fromHtml(getString(R.string.during_dialog_title_error)));
        } else {
            binding.title.setText(R.string.during_dialog_title);
            RemainderInfoActivity activity = (RemainderInfoActivity) getActivity();
            activity.onInputDuring(Float.parseFloat(binding.money.getText().toString()));
            dismiss();
        }
    }
}
