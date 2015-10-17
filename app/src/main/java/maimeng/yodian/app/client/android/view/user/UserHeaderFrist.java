package maimeng.yodian.app.client.android.view.user;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.databinding.ViewHeaderUserFristBinding;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.view.BaseFragment;
import maimeng.yodian.app.client.android.view.MainTab2Activity;
import maimeng.yodian.app.client.android.view.PreviewActivity;
import maimeng.yodian.app.client.android.view.deal.OrderListActivity;
import maimeng.yodian.app.client.android.view.deal.RemainderMainActivity;
import maimeng.yodian.app.client.android.view.dialog.AlertDialog;
import maimeng.yodian.app.client.android.view.skill.SkillTemplateActivity;

/**
 * Created by android on 2015/10/14.
 */
public class UserHeaderFrist extends BaseFragment implements View.OnClickListener {
    private ViewHeaderUserFristBinding mHeaderBinding;
    private User user;
    private static final int REQUEST_UPDATEINFO = 0x5005;

    public static UserHeaderFrist newInstance(User user) {
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        UserHeaderFrist fragment = new UserHeaderFrist();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mHeaderBinding = DataBindingUtil.inflate(inflater, R.layout.view_header_user_frist, container, false);
        View view = mHeaderBinding.getRoot();
        return view;
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHeaderBinding.userAvatar.setOnClickListener(this);
        //我的订单 我的余额 添加技能
        mHeaderBinding.myOrder.setOnClickListener(this);
        mHeaderBinding.myRemainder.setOnClickListener(this);
        mHeaderBinding.btnCreateskill.setOnClickListener(this);
        bind((User) getArguments().getParcelable("user"));
    }

    @Override
    public void onClick(final View clickItem) {
        if (clickItem == mHeaderBinding.userAvatar) {
            if (!(user.getUid() == User.read(getActivity()).getUid())) {
                PreviewActivity.show(getActivity(), user);
            } else {
//                ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(getContext(), R.anim.slide_in_from_right, R.anim.slide_out_to_left);
//                ActivityCompat.startActivityForResult(getActivity(), new Intent(getActivity(), SettingUserInfo.class), REQUEST_UPDATEINFO, options.toBundle());
                startActivityForResult(new Intent(getActivity(), SettingUserInfo.class), REQUEST_UPDATEINFO);
            }

        } else if (clickItem == mHeaderBinding.btnCreateskill) {
            if (TextUtils.isEmpty(User.read(getActivity()).getWechat())) {
                AlertDialog.newInstance("提示", "你未设置微信号").setPositiveListener(new AlertDialog.PositiveListener() {
                    @Override
                    public void onPositiveClick(DialogInterface dialog) {
//                        Pair<View, String> avatar = Pair.create(clickItem, "avatar");
//                        Pair<View, String> back = Pair.create((View) getButton(), "back");
//                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), avatar, back);
//                        ActivityCompat.startActivityForResult(getActivity(), new Intent(getActivity(), SettingUserInfo.class), REQUEST_UPDATEINFO, options.toBundle());
                        startActivityForResult(new Intent(getActivity(), SettingUserInfo.class), REQUEST_UPDATEINFO);
                    }

                    @Override
                    public String positiveText() {
                        return "前往";
                    }
                }).show(getActivity().getFragmentManager(), "");

            } else {

                Pair<View, String> top = Pair.create(clickItem, "top");
                Pair<View, String> floatbutton = Pair.create((View) getButton(), "floatbutton");
                SkillTemplateActivity.show(getActivity(), BaseFragment.REQUEST_CREATE_SKILL, user.getInfo(), new Pair[]{top,floatbutton});

            }

        } else if (clickItem == mHeaderBinding.myRemainder) {
            Intent intent = new Intent(getActivity(), RemainderMainActivity.class);
            startActivity(intent);

        } else if (clickItem == mHeaderBinding.myOrder) {
            Intent intent = new Intent(getActivity(), OrderListActivity.class);
            startActivity(intent);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_UPDATEINFO) {
                bind(User.read(getContext()));
            }
        }
    }

    private void bind(User user) {
        this.user = user;
        mHeaderBinding.setUser(user);
        mHeaderBinding.executePendingBindings();
        if (user.getUid() != User.read(getContext()).getUid()) {
            mHeaderBinding.btnCreateskill.setVisibility(View.GONE);
            mHeaderBinding.icEditAvatar.setVisibility(View.GONE);
            mHeaderBinding.bottom.setVisibility(View.GONE);
        } else {
            mHeaderBinding.icEditAvatar.setVisibility(View.VISIBLE);
            mHeaderBinding.bottom.setVisibility(View.VISIBLE);
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
            } else {
                mHeaderBinding.userSign.setText(String.format("来至%s", city));
            }

        }
    }

}
