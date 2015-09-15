package maimeng.yodian.app.client.android.view.skill.proxy;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

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
import maimeng.yodian.app.client.android.adapter.SkillListSelectorAdapter;
import maimeng.yodian.app.client.android.chat.DemoHXSDKHelper;
import maimeng.yodian.app.client.android.chat.activity.ChatActivity;
import maimeng.yodian.app.client.android.chat.db.UserDao;
import maimeng.yodian.app.client.android.chat.domain.RobotUser;
import maimeng.yodian.app.client.android.common.PullHeadView;
import maimeng.yodian.app.client.android.databinding.ViewHeaderMainHomeBinding;
import maimeng.yodian.app.client.android.entry.skillseletor.HeaderViewEntry;
import maimeng.yodian.app.client.android.entry.skillseletor.ItemViewEntry;
import maimeng.yodian.app.client.android.entry.skillseletor.ViewEntry;
import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.model.User;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.loader.Circle;
import maimeng.yodian.app.client.android.network.loader.ImageLoaderManager;
import maimeng.yodian.app.client.android.network.response.SkillUserResponse;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.response.UserInfoResponse;
import maimeng.yodian.app.client.android.network.service.CommonService;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.network.service.UserService;
import maimeng.yodian.app.client.android.service.ChatServiceLoginService;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.view.SettingsActivity;
import maimeng.yodian.app.client.android.view.chat.ChatMainActivity;
import maimeng.yodian.app.client.android.view.dialog.AlertDialog;
import maimeng.yodian.app.client.android.view.dialog.ShareDialog;
import maimeng.yodian.app.client.android.view.skill.CreateOrEditSkillActivity;
import maimeng.yodian.app.client.android.view.skill.SkillDetailsActivity;
import maimeng.yodian.app.client.android.view.skill.SkillTemplateActivity;
import maimeng.yodian.app.client.android.view.user.SettingUserInfo;
import maimeng.yodian.app.client.android.widget.EndlessRecyclerOnScrollListener;
import maimeng.yodian.app.client.android.widget.ListLayoutManager;

/**
 * Created by android on 15-7-13.
 */
public class MainHomeProxy implements ActivityProxy, EMEventListener, AbstractAdapter.ViewHolderClickListener<SkillListHomeAdapter.ViewHolder>, PtrHandler, Callback<SkillUserResponse>, AppBarLayout.OnOffsetChangedListener {
    private static final int REQUEST_UPDATEINFO = 0x5005;
    private static final String LOG_TAG = MainHomeProxy.class.getSimpleName();
    private final CoordinatorLayout mView;
    private final Activity mActivity;
    private final RecyclerView mRecyclerView;
    private final PtrFrameLayout mRefreshLayout;
    private final SkillService service;
    private Bitmap defaultAvatar;
    private boolean inited = false;
    private final SkillListHomeAdapter adapter;
    private final EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    private User user;
    private int page = 1;
    private FloatingActionButton mFloatButton;
    private int mEditPostion;
    private final Handler handler;
    ValueAnimator animator = new ValueAnimator();
    private ViewHeaderMainHomeBinding headerMainHomeBinding;
    private List<ViewEntry> viewEntries;

    public MainHomeProxy(Activity activity, View view) {
        this(activity, view, null, "");
    }

    public MainHomeProxy(Activity activity, View view, Bitmap avatar, String nickname) {
        this.mView = (CoordinatorLayout) view;
        handler = new Handler(Looper.getMainLooper());
        this.mActivity = activity;
        view.setVisibility(View.GONE);
        service = Network.getService(SkillService.class);


        mRefreshLayout = (PtrFrameLayout) view.findViewById(R.id.refresh_layout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRefreshLayout.setPtrHandler(this);
        StoreHouseHeader header = PullHeadView.create(mActivity);

        mRefreshLayout.addPtrUIHandler(header);
        mRefreshLayout.setHeaderView(header);
        ListLayoutManager layout = new ListLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(layout);
        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(layout) {
            @Override
            public void onLoadMore() {
                page++;
                syncRequest();
            }
        };
        mRecyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
        adapter = new SkillListHomeAdapter(mActivity, this);
        mRecyclerView.setAdapter(adapter);
        animator.setIntValues(activity.getResources().getColor(R.color.colorPrimaryDark), activity.getResources().getColor(R.color.colorPrimary));
        animator.setEvaluator(new ArgbEvaluator());
        animator.setDuration(10000);
        viewEntries = new ArrayList<>();



    }

    public void syncRequest() {
        LogUtil.i(LOG_TAG, "syncRequest().user.id:%d,user.id:%d", user.getId(), user.getId());
        service.list(user.getUid(), page, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ActivityProxyController.REQUEST_CREATE_SKILL) {
                init();
                mActivity.startActivity(new Intent(mActivity, SkillDetailsActivity.class).putExtras(data));
            } else if (requestCode == REQUEST_AUTH) {
                init();
            } else if (requestCode == ActivityProxyController.REQUEST_EDIT_SKILL) {
                Skill skill = data.getParcelableExtra("skill");
                if (mEditPostion != -1 && skill != null) {
                   ViewEntry entry= adapter.getItem(mEditPostion);
                    if(entry instanceof ItemViewEntry){
                        ItemViewEntry itemViewEntry=(ItemViewEntry)entry;
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

    public boolean isInited() {
        return this.inited;
    }

    @Override
    public void init() {
        final User read = User.read(this.mActivity);
        init(read);
    }


    public void init(User user) {
        if (this.user == null) {
            this.user = user;
        }
        if(user!=null){
            viewEntries.add(0, new HeaderViewEntry(user));
            adapter.reload(viewEntries,true);
            adapter.notifyDataSetChanged();
        }

        LogUtil.i(LOG_TAG, "init().uid:%d,token:%s", user.getId(), user.getToken());
        inited = true;
        initSkillInfo();


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
                        MainHomeProxy.this.user.setInfo(data);
                        if (MainHomeProxy.this.user.getUid() == data.getUid()) {
                            MainHomeProxy.this.user.write(mActivity);
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

    private void initSkillInfo() {
        page = 1;
        mRefreshLayout.autoRefresh();
    }

    @Override
    public void reset() {
        inited = false;
        adapter.reload(new ArrayList<ViewEntry>(), false);
        adapter.notifyDataSetChanged();
    }


    private void initUsrInfo() {
        adapter.update(0,new HeaderViewEntry(user));
    }

    @Override
    public void onTitleChanged(CharSequence title, int color) {
    }

    private void refreshMissMsgIcon() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                headerMainHomeBinding=adapter.getHeaderBinding();
                if(headerMainHomeBinding!=null){
                    View view=headerMainHomeBinding.missMsgCount;
                    if(view!=null){
                        int unread = EMChatManager.getInstance().getUnreadMsgsCount();
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
    public void show(FloatingActionButton button, boolean anima) {
        refreshMissMsgIcon();
        this.mFloatButton = button;
        button.attachToRecyclerView(mRecyclerView);
        if (anima) {
            AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
            alphaAnimation.setDuration(mActivity.getResources().getInteger(R.integer.duration));
            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            this.mView.startAnimation(alphaAnimation);
        } else {
            mView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void hide(FloatingActionButton button) {
        hide(button, true);
    }

    @Override
    public void hide(FloatingActionButton button, boolean anima) {
        if (anima) {
            AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
            alphaAnimation.setDuration(mActivity.getResources().getInteger(R.integer.duration));
            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            this.mView.startAnimation(alphaAnimation);
        } else {
            mView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean isShow() {
        return false;
    }

    @Override
    public void onItemClick(final SkillListHomeAdapter.ViewHolder holder, int postion) {
        if(holder instanceof SkillListHomeAdapter.ItemViewHolder){
            final SkillListHomeAdapter.ItemViewHolder itemViewHolder=(SkillListHomeAdapter.ItemViewHolder)holder;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Pair<View, String> back = Pair.create((View) mFloatButton, "back");
                    Skill skill=itemViewHolder.getData();
                    if(skill.getStatus()==0){
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, back);
                        ActivityCompat.startActivity(mActivity, new Intent(mActivity, SkillDetailsActivity.class).putExtra("skill",skill) , options.toBundle());
                    }

                }
            }, 200);
        }


    }

    @Override
    public void onClick(final SkillListHomeAdapter.ViewHolder holder, final View clickItem, final int postion) {

        if(holder instanceof SkillListHomeAdapter.ItemViewHolder){
            final SkillListHomeAdapter.ItemViewHolder itemViewHolder=(SkillListHomeAdapter.ItemViewHolder)holder;
            final Skill skill = itemViewHolder.getData();
            if (clickItem == itemViewHolder.getBinding().btnShare) {
                Skill data = skill;
                ShareDialog.show(mActivity, new ShareDialog.ShareParams(data, data.getQrcodeUrl(), data.getUid(), data.getNickname(), ""), 1);
            } else if (clickItem == itemViewHolder.getBinding().btnUpdate) {
                Intent intent = new Intent(mActivity, CreateOrEditSkillActivity.class);
                intent.putExtra("skill", skill);
                mActivity.startActivityForResult(intent, ActivityProxyController.REQUEST_EDIT_SKILL);
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
        }else{
            if(holder instanceof SkillListHomeAdapter.HeaderViewHolder){
                SkillListHomeAdapter.HeaderViewHolder viewHolder=(SkillListHomeAdapter.HeaderViewHolder)holder;
                User user=viewHolder.getData();
                headerMainHomeBinding = viewHolder.getHeaderMainHomeBinding();
                if(clickItem== headerMainHomeBinding.btnChat){
                    mActivity.startActivity(new Intent(mActivity, ChatMainActivity.class));
                } else if(clickItem== headerMainHomeBinding.userAvatar){

//                    Pair<View, String> avatar = Pair.create(clickItem, "avatar");
//                    Pair<View, String> back = Pair.create((View) mFloatButton, "back");
//                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, avatar, back);
//                    ActivityCompat.startActivityForResult(mActivity, new Intent(mActivity, SettingUserInfo.class), REQUEST_UPDATEINFO, options.toBundle());
                } else  if(clickItem== headerMainHomeBinding.btnSettings){
                    Pair<View, String> back = Pair.create((View) mFloatButton, "back");
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, back);
                    ActivityCompat.startActivity(mActivity, new Intent(mActivity, SettingsActivity.class), options.toBundle());

                }else if(clickItem== headerMainHomeBinding.btnReport){
                            report();
                }else if(clickItem== headerMainHomeBinding.btnCreateskill){
                    if (TextUtils.isEmpty(User.read(mActivity).getWechat())) {
                        AlertDialog.newInstance("提示", "你未设置微信号").setPositiveListener(new AlertDialog.PositiveListener() {
                            @Override
                            public void onPositiveClick(DialogInterface dialog) {
                                Pair<View, String> avatar = Pair.create(clickItem, "avatar");
                                Pair<View, String> back = Pair.create((View) mFloatButton, "back");
                                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, avatar, back);
                                ActivityCompat.startActivityForResult(mActivity, new Intent(mActivity, SettingUserInfo.class), REQUEST_UPDATEINFO, options.toBundle());
                            }

                            @Override
                            public String positiveText() {
                                return "前往";
                            }
                        }).show(mActivity.getFragmentManager(), "");

                    } else {

                        Pair<View, String> top = Pair.create(clickItem, "top");
                        Pair<View, String> floatbutton = Pair.create((View) mFloatButton, "floatbutton");
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, top, floatbutton);
                        ActivityCompat.startActivityForResult(mActivity, new Intent(mActivity, SkillTemplateActivity.class), ActivityProxyController.REQUEST_CREATE_SKILL, options.toBundle());
                    }

                }
            }
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
    public void start() {

    }

    @Override
    public void success(SkillUserResponse res, Response response) {
        if (res.isSuccess()) {
            List<Skill> list = res.getData().getList();

            for(int i=0;i<list.size();i++){
                viewEntries.add(new ItemViewEntry(list.get(i)));
            }
            adapter.reload(viewEntries, page != 1);
            adapter.notifyDataSetChanged();
            this.user.update(res.getData().getUser());
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


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        mRefreshLayout.setEnabled(i == 0);
        final float ratio = (float) Math.abs(i) / 507f;
        final int playTime = (int) (ratio * 10000);
        animator.setCurrentPlayTime(playTime);
    }



    @Override
    public void show(FloatingActionButton viewById) {
        show(viewById, true);
    }

    @Override
    public void onResume() {
        EMChatManager.getInstance().registerEventListener(this);
        refreshMissMsgIcon();
    }

    @Override
    public void onPause() {
        EMChatManager.getInstance().unregisterEventListener(this);
    }

    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage:
            case EventReadAck:
                refreshMissMsgIcon();
                break;
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



}
