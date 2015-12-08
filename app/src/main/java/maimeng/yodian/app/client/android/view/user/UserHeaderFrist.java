package maimeng.yodian.app.client.android.view.user;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.umeng.analytics.MobclickAgent;

import org.parceler.Parcels;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.common.UEvent;
import maimeng.yodian.app.client.android.databinding.ViewHeaderUserFristBinding;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.view.common.BaseFragment;
import maimeng.yodian.app.client.android.view.MainTab2Activity;
import maimeng.yodian.app.client.android.view.common.PreviewActivity;

/**
 * Created by android on 2015/10/14.
 */
public class UserHeaderFrist extends BaseFragment {
    private ViewHeaderUserFristBinding mHeaderBinding;
    private User user;
    private static final int REQUEST_UPDATEINFO = 0x5005;

    public static UserHeaderFrist newInstance(User user) {
        Bundle args = new Bundle();
        args.putParcelable("user", Parcels.wrap(user));
        UserHeaderFrist fragment = new UserHeaderFrist();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, boolean showTitle) {
        setShowTitle(false);
        mHeaderBinding = DataBindingUtil.inflate(inflater, R.layout.view_header_user_frist, null, false);
        return mHeaderBinding.getRoot();
    }

    public View getButton() {
        if (getActivity() instanceof MainTab2Activity) {
            return ((MainTab2Activity) getActivity()).getFloatButton();
        } else if (getActivity() instanceof UserHomeActivity) {
            return ((UserHomeActivity) getActivity()).getFloatButton();
        }
        return null;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHeaderBinding.userAvatar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (!(user.getUid() == User.read(getActivity()).getUid())) {
                        PreviewActivity.show(getActivity(), user);
                        MobclickAgent.onEvent(getActivity(), UEvent.EDIT_BASIC_INFO_FROM_HOME);
                    } else {
//                ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(getContext(), R.anim.slide_in_from_right, R.anim.slide_out_to_left);
//                ActivityCompat.startActivityForResult(getActivity(), new Intent(getActivity(), SettingUserInfo.class), REQUEST_UPDATEINFO, options.toBundle());
                        startActivityForResult(new Intent(getActivity(), SettingUserInfo.class), REQUEST_UPDATEINFO);
                    }

                    return true;
                }
                return false;
            }
        });
        bind((User) get("user"));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_UPDATEINFO) {
                bind(User.read(getContext()));
                getActivity().sendBroadcast(new Intent(UserHomeFragment.ACTION_UPDATE_INFO));
            }
        }
    }

    private void bind(User user) {
        this.user = user;
        mHeaderBinding.setUser(user);
        mHeaderBinding.executePendingBindings();
        if (user.getUid() != User.read(getContext()).getUid()) {
            mHeaderBinding.icEditAvatar.setVisibility(View.GONE);
        } else {
            mHeaderBinding.icEditAvatar.setVisibility(View.VISIBLE);
        }
        User.Info info = user.getInfo();
        if (info == null) return;
        String city = info.getCity();
        String job = info.getJob();
        if (TextUtils.isEmpty(city) && TextUtils.isEmpty(job)) {

        } else {
            if (!TextUtils.isEmpty(city) && !TextUtils.isEmpty(job)) {
                mHeaderBinding.userSign.setText(String.format("来至%s的%s", city, job));
            } else if (TextUtils.isEmpty(city)) {
                mHeaderBinding.userSign.setText(job);
            } else if(TextUtils.isEmpty(job)){
                mHeaderBinding.userSign.setText(String.format("来至%s", city));
            }else if (!"请选择".equals(city)) {
                mHeaderBinding.userSign.setText(String.format("来至%s", city));
            }

        }
    }

}
