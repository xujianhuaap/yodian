package maimeng.yodian.app.client.android.view.user;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.view.BaseFragment;

/**
 * Created by android on 2015/10/14.
 */
public class UserHeaderSecond extends BaseFragment {
    public static UserHeaderSecond newInstance(User user) {
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        UserHeaderSecond fragment = new UserHeaderSecond();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView textView = new TextView(getContext());
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(16);
        textView.setGravity(Gravity.CENTER);
        User user = getArguments().getParcelable("user");
        if (user == null) {
            textView.setText("无签名");
        } else {
            if (user.getInfo() == null) {
                textView.setText("无签名");
            } else if (TextUtils.isEmpty(user.getInfo().getSignature())) {
                textView.setText("无签名");
            } else {
                textView.setText(user.getInfo().getSignature());
            }
        }
        return textView;
    }
}
