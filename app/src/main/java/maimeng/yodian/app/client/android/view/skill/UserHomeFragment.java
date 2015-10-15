package maimeng.yodian.app.client.android.view.skill;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.melnykov.fab.FloatingActionButton;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

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
import maimeng.yodian.app.client.android.chat.db.UserDao;
import maimeng.yodian.app.client.android.chat.domain.RobotUser;
import maimeng.yodian.app.client.android.common.PullHeadView;
import maimeng.yodian.app.client.android.databinding.UserHomeHeaderBinding;
import maimeng.yodian.app.client.android.entry.skillhome.HeaderViewEntry;
import maimeng.yodian.app.client.android.entry.skillhome.ItemViewEntry;
import maimeng.yodian.app.client.android.entry.skillhome.ViewEntry;
import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.response.SkillUserResponse;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.response.UserInfoResponse;
import maimeng.yodian.app.client.android.network.service.CommonService;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.network.service.UserService;
import maimeng.yodian.app.client.android.service.ChatServiceLoginService;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.view.AbstractActivity;
import maimeng.yodian.app.client.android.view.BaseFragment;
import maimeng.yodian.app.client.android.view.SettingsActivity;
import maimeng.yodian.app.client.android.view.chat.ChatMainActivity;
import maimeng.yodian.app.client.android.view.dialog.AlertDialog;
import maimeng.yodian.app.client.android.view.dialog.ShareDialog;
import maimeng.yodian.app.client.android.widget.EndlessRecyclerOnScrollListener;
import maimeng.yodian.app.client.android.widget.ListLayoutManager;

/**
 * Created by android on 15-10-10.
 */
public class UserHomeFragment extends BaseFragment implements EMEventListener, PtrHandler, AbstractAdapter.ViewHolderClickListener<SkillListHomeAdapter.ViewHolder>, Callback<SkillUserResponse> {
    private AbstractActivity mActivity;
    private FloatingActionButton mFloatButton;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mActivity = (AbstractActivity) getActivity();
        mFloatButton = mActivity.getFloatButton();
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
        refreshMissMsgIcon();
        Bundle args = getArguments();
        long uid = args.getLong("uid", 0);
        if (uid > 0) {
            service.list(uid, page, this);
        } else {
            user = User.read(getActivity());
            init();
        }
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
                Skill skill = data.getParcelableExtra("skill");
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

    private boolean inited = false;

    public void init() {
        final User read = User.read(this.mActivity);
        init(read);
    }

    private void initSkillInfo() {
        page = 1;
        mRefreshLayout.autoRefresh();
    }


    public void init(User user) {
        if (this.user == null) {
            this.user = user;
        }
        if (user != null) {
            adapter.reload(new HeaderViewEntry(user));
            adapter.notifyDataSetChanged();
        }

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
            List<Skill> list = res.getData().getList();
            List<ViewEntry> entries = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                entries.add(new ItemViewEntry(list.get(i)));
            }
            adapter.reload(entries, page != 1);
            adapter.notifyDataSetChanged();
            if (user == null) {
                inited = true;
                user = res.getData().getUser();
            } else {
                this.user.update(res.getData().getUser());
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

    private void showUserInfo() {
        final User read = User.read(mActivity);
        LogUtil.i(LOG_TAG, "showUserInfo().read.id:%d,user.id:%d", read.getId(), user.getId());
        LogUtil.i(LOG_TAG, "showUserInfo().read.token:%s,user.token:%s", read.getToken(), user.getToken());
        if (read.getUid() == user.getUid()) {
            Network.getService(UserService.class).info(new Callback<UserInfoResponse>() {
                @Override
                public void start() {

                }

                @Override
                public void success(UserInfoResponse res, Response response) {
                    if (res.isSuccess()) {
                        final User.Info data = res.getData();
                        UserHomeFragment.this.user.setInfo(data);
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
    public void onItemClick(final SkillListHomeAdapter.ViewHolder holder, int postion) {
        if (holder instanceof SkillListHomeAdapter.ItemViewHolder) {
            final SkillListHomeAdapter.ItemViewHolder itemViewHolder = (SkillListHomeAdapter.ItemViewHolder) holder;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Pair<View, String> back = Pair.create((View) mFloatButton, "back");
                    Skill skill = itemViewHolder.getData();
                    if (skill.getStatus() == 0) {
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, back);
                        ActivityCompat.startActivity(mActivity, new Intent(mActivity, SkillDetailsActivity.class).putExtra("skill", skill), options.toBundle());
                    }

                }
            }, 200);
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
                Intent intent = new Intent(mActivity, CreateOrEditSkillActivity.class);
                intent.putExtra("skill", skill);
                startActivityForResult(intent, REQUEST_EDIT_SKILL);
                mEditPostion = postion;
                itemViewHolder.closeWithAnim();
            } else if (clickItem == itemViewHolder.getBinding().btnDelete) {
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
                                        mFloatButton.show(true);
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
                service.up(skill.getId(), skill.getStatus(), new ToastCallback(mActivity) {
                    @Override
                    public void success(ToastResponse res, Response response) {
                        super.success(res, response);
                        skill.setStatus(skill.getStatus() == 0 ? 1 : 0);
                        itemViewHolder.closeWithAnim();
                    }
                });
            } else if (clickItem == itemViewHolder.getBinding().btnBottom) {
                Intent intent = new Intent(mActivity, ChatActivity.class);
                intent.putExtra("skill", itemViewHolder.getData());
                intent.putExtra("uid", skill.getUid());
                Map<String, RobotUser> robotMap = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getRobotList();
                String chatLoginName = skill.getChatLoginName();
                if (robotMap.containsKey(chatLoginName)) {
                    intent.putExtra("userId", chatLoginName);
                    mActivity.startActivity(intent);
                } else {
                    RobotUser robot = new RobotUser();
                    robot.setId(skill.getUid() + "");
                    robot.setUsername(chatLoginName);
                    robot.setNick(skill.getNickname());
                    robot.setAvatar(skill.getAvatar());
                    robot.setWechat(skill.getWeichat());


                    maimeng.yodian.app.client.android.chat.domain.User user = new maimeng.yodian.app.client.android.chat.domain.User();
                    user.setId(skill.getUid() + "");
                    user.setUsername(chatLoginName);
                    user.setNick(skill.getNickname());
                    user.setAvatar(skill.getAvatar());
                    user.setWechat(skill.getWeichat());


                    // 存入内存
                    ((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveOrUpdate(skill.getChatLoginName(), robot);
                    ((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveOrUpdate(skill.getChatLoginName(), user);
                    // 存入db
                    UserDao dao = new UserDao(mActivity);
                    dao.saveOrUpdate(user);
                    dao.saveOrUpdate(robot);
                    intent.putExtra("userId", chatLoginName);
                    intent.putExtra("userNickname", skill.getNickname());
                    mActivity.startActivity(intent);
                }
            }
        } else {
            if (holder instanceof SkillListHomeAdapter.HeaderViewHolder) {
                SkillListHomeAdapter.HeaderViewHolder viewHolder = (SkillListHomeAdapter.HeaderViewHolder) holder;
                headerMainHomeBinding = viewHolder.getBinding();
                if (clickItem == headerMainHomeBinding.btnChat) {
                    mActivity.startActivity(new Intent(mActivity, ChatMainActivity.class));
                } else if (clickItem == headerMainHomeBinding.btnSettings) {
                    Pair<View, String> back = Pair.create((View) mFloatButton, "back");
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, back);
                    ActivityCompat.startActivity(mActivity, new Intent(mActivity, SettingsActivity.class), options.toBundle());
                } else if (clickItem == headerMainHomeBinding.btnReport) {
                    report();
                }
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        refreshMissMsgIcon();
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
                        int unread = EMChatManager.getInstance().getUnreadMsgsCount();
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
