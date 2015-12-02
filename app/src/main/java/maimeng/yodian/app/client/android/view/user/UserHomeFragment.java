package maimeng.yodian.app.client.android.view.user;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.umeng.analytics.MobclickAgent;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.adapter.AbstractAdapter;
import maimeng.yodian.app.client.android.adapter.SkillListHomeAdapter;
import maimeng.yodian.app.client.android.chat.DemoHXSDKHelper;
import maimeng.yodian.app.client.android.chat.activity.ChatActivity;
import maimeng.yodian.app.client.android.chat.domain.RobotUser;
import maimeng.yodian.app.client.android.common.PullHeadView;
import maimeng.yodian.app.client.android.common.UEvent;
import maimeng.yodian.app.client.android.databinding.UserHomeHeaderBinding;
import maimeng.yodian.app.client.android.entry.skillhome.HeaderViewEntry;
import maimeng.yodian.app.client.android.entry.skillhome.ItemViewEntry;
import maimeng.yodian.app.client.android.entry.skillhome.ViewEntry;
import maimeng.yodian.app.client.android.model.chat.ChatUser;
import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.response.SkillUserResponse;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.response.UserInfoResponse;
import maimeng.yodian.app.client.android.network.service.ChatService;
import maimeng.yodian.app.client.android.network.service.CommonService;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.network.service.UserService;
import maimeng.yodian.app.client.android.service.ChatServiceLoginService;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.view.common.AbstractActivity;
import maimeng.yodian.app.client.android.view.common.BaseFragment;
import maimeng.yodian.app.client.android.view.common.SettingsActivity;
import maimeng.yodian.app.client.android.view.chat.ChatMainActivity;
import maimeng.yodian.app.client.android.view.deal.OrderListActivity;
import maimeng.yodian.app.client.android.view.deal.RemainderMainActivity;
import maimeng.yodian.app.client.android.view.dialog.AlertDialog;
import maimeng.yodian.app.client.android.view.dialog.ShareDialog;
import maimeng.yodian.app.client.android.view.skill.CreateOrEditSkillActivity;
import maimeng.yodian.app.client.android.view.skill.SkillDetailsActivity;
import maimeng.yodian.app.client.android.view.skill.SkillTemplateActivity;
import maimeng.yodian.app.client.android.widget.EndlessRecyclerOnScrollListener;
import maimeng.yodian.app.client.android.widget.ListLayoutManager;

/**
 * Created by android on 15-10-10.
 */
public class UserHomeFragment extends BaseFragment implements EMEventListener, PtrHandler, AbstractAdapter.ViewHolderClickListener<SkillListHomeAdapter.ViewHolder>, Callback<SkillUserResponse> {
    private AbstractActivity mActivity;
    private int unread;

    public static UserHomeFragment newInstance() {
        UserHomeFragment userHomeFragment = new UserHomeFragment();
        Bundle args = new Bundle();
        userHomeFragment.setArguments(args);
        return userHomeFragment;
    }

    public static UserHomeFragment newInstance(Bitmap avatar, String nickname, long uid) {
        UserHomeFragment userHomeFragment = new UserHomeFragment();
        Bundle args = new Bundle();
        args.putParcelable("avatar", avatar);
        args.putString("nickname", nickname);
        args.putLong("uid", uid);
        userHomeFragment.setArguments(args);
        return userHomeFragment;
    }

    private static final String LOG_TAG = UserHomeFragment.class.getSimpleName();
    private int page = 1;
    private static final int REQUEST_UPDATEINFO = 0x5005;
    private static final int REQUEST_AUTH = 0x8001;
    private User user;
    private int mEditPostion;
    private UserHomeHeaderBinding headerMainHomeBinding;
    private RecyclerView mRecyclerView;
    private PtrFrameLayout mRefreshLayout;
    private SkillService service;
    private SkillListHomeAdapter adapter;
    private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    private Handler handler;
    private boolean isMe;

    @Override
    public View onCreateView(LayoutInflater inflater, boolean showTitle) {
        setShowTitle(false);
        View view = inflater.inflate(R.layout.fragment_home, null, false);
        mActivity = (AbstractActivity) getActivity();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handler = new Handler(Looper.getMainLooper());

        service = Network.getService(SkillService.class);
        mRefreshLayout = (PtrFrameLayout) view.findViewById(R.id.refresh_layout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRefreshLayout.setPtrHandler(this);
        StoreHouseHeader header = PullHeadView.create(getActivity());

        mRefreshLayout.addPtrUIHandler(header);
        mRefreshLayout.setHeaderView(header);
        ListLayoutManager layout = new ListLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layout);
        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(layout) {
            @Override
            public void onLoadMore() {
                page++;
                syncRequest();
            }
        };
        mRecyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
        adapter = new SkillListHomeAdapter(mActivity, this, mRefreshLayout);
        mRecyclerView.setAdapter(adapter);
        if (User.read(mActivity).isPushOn()) {
            EMChatManager.getInstance().registerEventListener(this);
        } else {
            EMChatManager.getInstance().unregisterEventListener(this);
        }
        Bundle args = getArguments();
        long uid = args.getLong("uid", 0);
        if (uid > 0) {
            isMe = false;
            service.list(uid, page, this);
        } else {
            isMe = true;
            user = User.read(getActivity());
            init();
        }

        refreshMissMsgIcon();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CREATE_SKILL) {
                init();
                mActivity.startActivity(new Intent(mActivity, SkillDetailsActivity.class).putExtras(data));
            } else if (requestCode == REQUEST_AUTH) {
                init();
            } else if (requestCode == REQUEST_EDIT_SKILL) {
                Skill skill = Parcels.unwrap(data.getParcelableExtra("skill"));
                if (mEditPostion != -1 && skill != null) {
                    ViewEntry entry = adapter.getItem(mEditPostion);
                    if (entry instanceof ItemViewEntry) {
                        ItemViewEntry itemViewEntry = (ItemViewEntry) entry;
                        itemViewEntry.skill.update(skill);
                    }

                    adapter.notifyItemChanged(mEditPostion);
                    mEditPostion = -1;
                }
            } else if (requestCode == REQUEST_UPDATEINFO) {
                user = User.read(this.mActivity);
                initUsrInfo();
            }
        }
    }

    private boolean inited = true;

    public void init() {
        final User read = User.read(this.mActivity);
        init(read);
        final SharedPreferences sendservice = getActivity().getSharedPreferences("sendservice", Context.MODE_PRIVATE);
        boolean aBoolean = sendservice.getBoolean("flag" + read.getUid(), false);
        if (!aBoolean) {
            Network.getService(ChatService.class).sendService(new Callback.SimpleCallBack<maimeng.yodian.app.client.android.network.response.Response>() {
                @Override
                public void success(maimeng.yodian.app.client.android.network.response.Response res, Response response) {
                    if (response.getStatus() == 200) {

                        LogUtil.i(UserHomeFragment.class.getName(), "Send Admin Msg Success url:%s", response.getUrl());
                        sendservice.edit().putBoolean("flag" + read.getUid(), true).apply();
                    }
                }

                @Override
                public void failure(HNetError hNetError) {
                    hNetError.printStackTrace();
                }
            });
        }
    }

    private void initSkillInfo() {
        page = 1;
        mRefreshLayout.autoRefresh();
    }

    public static final String ACTION_UPDATE_INFO = "maimeng.yodian.client.app.android.UPDATE_INFO";

    public void init(User user) {
        if (this.user == null) {
            this.user = user;
        }
//        if (user != null) {
//            adapter.reload(new HeaderViewEntry(user));
//            adapter.notifyDataSetChanged();
//        }

        LogUtil.i(LOG_TAG, "init().uid:%d,token:%s", user.getId(), user.getToken());
        inited = true;
        initSkillInfo();


    }

    private void initUsrInfo() {
        adapter.update(0, new HeaderViewEntry(user));
    }

    @Override
    public void onEvent(EMNotifierEvent event) {
        LogUtil.d(LOG_TAG, "isPushON" + User.read(mActivity).isPushOn());
        if (User.read(mActivity).isPushOn()) {
            switch (event.getEvent()) {
                case EventNewMessage:
                case EventReadAck:
                    refreshMissMsgIcon();
                    break;
            }
        }

    }

    public void syncRequest() {
        LogUtil.i(LOG_TAG, "syncRequest().user.id:%d,user.id:%d", user.getId(), user.getId());
        service.list(user.getUid(), page, this);
    }

    @Override
    public void start() {

    }

    @Override
    public void success(SkillUserResponse res, Response response) {
        if (res.isSuccess()) {
            User.Info user = res.getData().getUser();
            List<Skill> list = res.getData().getList();
            List<ViewEntry> entries = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                entries.add(new ItemViewEntry(list.get(i)));
            }
            adapter.reload(entries, page != 1);
            adapter.notifyDataSetChanged();
            if (user != null) {
                if (!isMe) {
                    //他人信息页面
                    inited = false;
                    this.user = user;
                } else {
                    //个人主页

                    this.user.update(user);//更新登录信息——个人的部分信息
                    this.user.setInfo(user);
                    this.user.writeInfo(getActivity());
                    if (user.getInfo().getMoneyMsg() > 0) {
                        headerMainHomeBinding.msgMoneyTopic.setVisibility(View.VISIBLE);
                    }
                }
            }
            showUserInfo();
        } else {
            res.showMessage(mActivity);
            if (!res.isValidateAuth(mActivity, REQUEST_AUTH)) {
            }
        }

    }

    @Override
    public void failure(HNetError hNetError) {
        ErrorUtils.checkError(mActivity, hNetError);
    }

    @Override
    public void end() {
        mRefreshLayout.refreshComplete();
    }

    /***
     * 数据本地化 开启了聊天服务
     */
    private void showUserInfo() {
        final User read = User.read(mActivity);
        LogUtil.i(LOG_TAG, "showUserInfo().read.id:%d,user.id:%d", read.getId(), user.getId());
        LogUtil.i(LOG_TAG, "showUserInfo().read.token:%s,user.token:%s", read.getToken(), user.getToken());

        if (read.getUid() == user.getUid()) {
            Network.getService(UserService.class).getInfo(user.getUid(), new Callback<UserInfoResponse>() {
                @Override
                public void start() {

                }

                @Override
                public void success(UserInfoResponse res, Response response) {
                    if (res.isSuccess()) {
                        final User.Info data = res.getData().getUser();
                        if (UserHomeFragment.this.user.getUid() == data.getUid()) {
                            UserHomeFragment.this.user.write(mActivity);
                            mActivity.startService(new Intent(mActivity, ChatServiceLoginService.class));
                        }
                        initUsrInfo();
                    }
                }

                @Override
                public void failure(HNetError hNetError) {
                    ErrorUtils.checkError(mActivity, hNetError);
                }

                @Override
                public void end() {

                }
            });
        } else {
            initUsrInfo();
        }
    }


    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View content, View header) {
        // 默认实现，根据实际情况做改动
        return PtrDefaultHandler.checkContentCanBePulledDown(ptrFrameLayout, content, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
        page = 1;
        syncRequest();
    }

    @Override
    public void onItemClick(final SkillListHomeAdapter.ViewHolder h, int postion) {
        if (h instanceof SkillListHomeAdapter.ItemViewHolder) {
            Skill skill = ((SkillListHomeAdapter.ItemViewHolder) h).getData();
            if (!skill.isXiajia()) {
                ((SkillListHomeAdapter.ItemViewHolder) h).itemView.setClickable(false);
                startActivity(new Intent(getContext(), SkillDetailsActivity.class).putExtra("skill", Parcels.wrap(skill)));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((SkillListHomeAdapter.ItemViewHolder) h).itemView.setClickable(true);
                    }
                }, 1000);
            }
        }


    }

    @Override
    public void onClick(final SkillListHomeAdapter.ViewHolder holder, final View clickItem, final int postion) {

        if (holder instanceof SkillListHomeAdapter.ItemViewHolder) {
            final SkillListHomeAdapter.ItemViewHolder itemViewHolder = (SkillListHomeAdapter.ItemViewHolder) holder;
            final Skill skill = itemViewHolder.getData();
            if (clickItem == itemViewHolder.getBinding().btnShare) {
                Skill data = skill;
                ShareDialog.show(mActivity, new ShareDialog.ShareParams(data, data.getQrcodeUrl(), data.getUid(), data.getNickname(), ""), 1);
            } else if (clickItem == itemViewHolder.getBinding().btnUpdate) {
                MobclickAgent.onEvent(getActivity(), UEvent.EDIT_SKILL);
                CreateOrEditSkillActivity.show(getActivity(), REQUEST_EDIT_SKILL, skill);
                mEditPostion = postion;
                itemViewHolder.closeWithAnim();
            } else if (clickItem == itemViewHolder.getBinding().btnDelete) {
                MobclickAgent.onEvent(getActivity(), UEvent.EDIT_SKILL_DELETE);
                AlertDialog.newInstance("提示", "确定要删除吗?").setPositiveListener(new AlertDialog.PositiveListener() {
                    @Override
                    public void onPositiveClick(final DialogInterface dialog) {
                        dialog.dismiss();
                        service.delete(skill.getId(), new ToastCallback(mActivity) {
                            @Override
                            public void success(ToastResponse res, Response response) {
                                super.success(res, response);
                                if (res.isSuccess()) {
                                    adapter.remove(postion);
                                    if (adapter.getItemCount() <= 0) {

                                    }
                                }
                            }

                            @Override
                            public void end() {
                                super.end();
                            }
                        });
                    }

                    @Override
                    public String positiveText() {
                        return mActivity.getResources().getString(android.R.string.ok);
                    }
                }).setNegativeListener(new AlertDialog.NegativeListener() {
                    @Override
                    public void onNegativeClick(DialogInterface dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public String negativeText() {
                        return mActivity.getResources().getString(android.R.string.cancel);
                    }
                }).show(mActivity.getFragmentManager(), "delete_dialog");

            } else if (clickItem == itemViewHolder.getBinding().btnChangeState) {
                MobclickAgent.onEvent(getActivity(), UEvent.EDIT_SKILL_UP_DOWN);
                service.up(skill.getId(), skill.isXiajia() ? 2 : 0, new ToastCallback(mActivity) {
                    @Override
                    public void success(ToastResponse res, Response response) {
                        super.success(res, response);
                        skill.setXiajia(!skill.isXiajia());
                        itemViewHolder.closeWithAnim();
                    }
                });
            } else if (clickItem == itemViewHolder.getBinding().btnBottom) {
                Map<String, RobotUser> robotMap = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getRobotList();
                String chatLoginName = skill.getChatLoginName();
                if (robotMap.containsKey(chatLoginName)) {
                    MobclickAgent.onEvent(getActivity(), UEvent.CONTACT_TA);
                    ChatActivity.show(getActivity(), itemViewHolder.getData(), new ChatUser(chatLoginName, skill.getUid(), skill.getNickname()));
                } else {
                    RobotUser robot = new RobotUser();
                    robot.setId(skill.getUid());
                    robot.setUsername(chatLoginName);
                    robot.setNick(skill.getNickname());
                    robot.setAvatar(skill.getAvatar());
                    robot.setWechat(skill.getWeichat());
                    robot.setQq(skill.getQq());
                    robot.setMobile(skill.getContact());


                    maimeng.yodian.app.client.android.chat.domain.User user = new maimeng.yodian.app.client.android.chat.domain.User();
                    user.setId(skill.getUid());
                    user.setUsername(chatLoginName);
                    user.setNick(skill.getNickname());
                    user.setAvatar(skill.getAvatar());
                    user.setWechat(skill.getWeichat());
                    user.setQq(skill.getQq());
                    user.setMobile(skill.getContact());


                    // 存入内存
                    ((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveOrUpdate(robot);
                    ((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveOrUpdate(user);
                    MobclickAgent.onEvent(getActivity(), UEvent.CONTACT_TA);
                    ChatActivity.show(getActivity(), itemViewHolder.getData(), new ChatUser(chatLoginName, skill.getUid(), skill.getNickname()));
                }
            }
        } else {
            if (holder instanceof SkillListHomeAdapter.HeaderViewHolder) {
                SkillListHomeAdapter.HeaderViewHolder viewHolder = (SkillListHomeAdapter.HeaderViewHolder) holder;
                headerMainHomeBinding = viewHolder.getBinding();
                if (clickItem == headerMainHomeBinding.btnChat) {
                    mActivity.startActivity(new Intent(mActivity, ChatMainActivity.class));
                } else if (clickItem == headerMainHomeBinding.btnSettings) {
                    MobclickAgent.onEvent(getActivity(), UEvent.SETTING_SYSTEM_FROM_HOME);
                    mActivity.startActivity(new Intent(mActivity, SettingsActivity.class));
                } else if (clickItem == headerMainHomeBinding.btnReport) {
                    report();
                } else if (clickItem == headerMainHomeBinding.btnBack) {
                    mActivity.finish();
                } else if (clickItem == headerMainHomeBinding.btnCreateskill) {
                    MobclickAgent.onEvent(getActivity(), UEvent.PUBLISH_SKILL_FROM_HOME);
                    User user = User.read(getActivity());
                    String weChat = user.getInfo().getWechat();
                    String qq = user.getInfo().getQq();
                    String contact = user.getInfo().getContact();
                    boolean weChatIsEmpty = TextUtils.isEmpty(weChat);
                    boolean qqIsEmpty = TextUtils.isEmpty(qq);
                    boolean contactIsEmpty = TextUtils.isEmpty(contact);
                    if (weChatIsEmpty && qqIsEmpty && contactIsEmpty) {
                        AlertDialog.newInstance("提示", "请完善个人信息").setPositiveListener(new AlertDialog.PositiveListener() {
                            @Override
                            public void onPositiveClick(DialogInterface dialog) {
                                startActivityForResult(new Intent(getActivity(), SettingUserInfo.class), REQUEST_UPDATEINFO);
                            }

                            @Override
                            public String positiveText() {
                                return "前往";
                            }
                        }).show(getActivity().getFragmentManager(), "");

                    } else {
                        SkillTemplateActivity.show(getActivity(), BaseFragment.REQUEST_CREATE_SKILL);

                    }
                } else if (clickItem == headerMainHomeBinding.btnMyOrder) {

                    if (user.getInfo().getSellMsg() > 0) {
                        OrderListActivity.show(getActivity(), true);
                        return;

                    }
                    if (user.getInfo().getBuyMsg() > 0) {
                        OrderListActivity.show(getActivity(), false);
                        return;
                    }
                    OrderListActivity.show(getActivity(), true);


                } else if (clickItem == headerMainHomeBinding.btnMyRemainder) {
                    Intent intent = new Intent(getActivity(), RemainderMainActivity.class);
                    startActivity(intent);
                }
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (user != null) {
            if (user.getId() == User.read(getActivity()).getId()) {
                page = 1;
                syncRequest();
                MobclickAgent.onEvent(getActivity(), UEvent.HOME);
            }
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (user != null && user.getId() != User.read(getActivity()).getId()) {
            return;
        }
        if (hidden) {

        } else {
            MobclickAgent.onEvent(getActivity(), UEvent.ENTRY_INDEX_FROM_HOME);
            if (user != null) {
                syncRequest();
            }
            refreshMissMsgIcon();
        }
    }


    private void report() {
        AlertDialog alert = AlertDialog.newInstance("提示", mActivity.getString(R.string.lable_alert_report_user));
        alert.setNegativeListener(new AlertDialog.NegativeListener() {
            @Override
            public void onNegativeClick(DialogInterface dialog) {
                dialog.dismiss();
            }

            @Override
            public String negativeText() {
                return mActivity.getString(android.R.string.cancel);
            }
        });
        alert.setPositiveListener(new AlertDialog.PositiveListener() {
            @Override
            public void onPositiveClick(DialogInterface dialog) {
                Network.getService(CommonService.class).report(3, 0, 0, user.getUid(), new ToastCallback(mActivity));
            }

            @Override
            public String positiveText() {
                return mActivity.getString(R.string.lable_report);
            }
        });
        alert.show(mActivity.getFragmentManager(), "alert");
    }

    private void refreshMissMsgIcon() {

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                headerMainHomeBinding = adapter.getHeaderBinding();
                if (headerMainHomeBinding != null) {
                    View view = headerMainHomeBinding.missMsgCount;

                    if (view != null) {
                        unread = EMChatManager.getInstance().getUnreadMsgsCount();
                        if (!User.read(mActivity).isPushOn()) {
                            unread = 0;
                        }
                        if (unread > 0) {
                            view.setVisibility(View.VISIBLE);
                        } else {
                            view.setVisibility(View.INVISIBLE);
                        }

                    }
                }

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }
}
