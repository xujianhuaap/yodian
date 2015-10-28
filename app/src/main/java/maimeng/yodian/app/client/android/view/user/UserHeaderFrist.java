package maimeng.yodian.app.client.android.view.user;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
public class UserHeaderFrist extends BaseFragment {
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
        //我的订单 我的余额 添加技能
        mHeaderBinding.myOrder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    OrderListActivity.show(getActivity());
                    return true;
                }
                return false;
            }
        });
        mHeaderBinding.myRemainder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Intent intent = new Intent(getActivity(), RemainderMainActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        mHeaderBinding.btnCreateskill.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    User user=User.read(getActivity());
                    String weChat=user.getInfo().getWechat();
                    String qq=user.getInfo().getQq();
                    String contact=user.getInfo().getContact();
                    boolean weChatIsEmpty=TextUtils.isEmpty(weChat);
                    boolean qqIsEmpty=TextUtils.isEmpty(weChat);
                    boolean contactIsEmpty=TextUtils.isEmpty(contact);
                    if (weChatIsEmpty&&qqIsEmpty&&contactIsEmpty) {
                        AlertDialog.newInstance("提示", "请完善个人信息").setPositiveListener(new AlertDialog.PositiveListener() {
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

                        Pair<View, String> top = Pair.create(v, "top");
                        Pair<View, String> floatbutton = Pair.create((View) getButton(), "floatbutton");
                        SkillTemplateActivity.show(getActivity(), BaseFragment.REQUEST_CREATE_SKILL, new Pair[]{top, floatbutton});

                    }
                }
                return false;
            }
        });
        bind((User) getArguments().getParcelable("user"));
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
            } else if (!"请选择".equals(city)) {
                mHeaderBinding.userSign.setText(String.format("来至%s", city));
            }

        }
    }

}
