package maimeng.yodian.app.client.android.view.skill.proxy;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import maimeng.yodian.app.client.android.adapter.SkillListSelectorAdapter;
import maimeng.yodian.app.client.android.chat.DemoHXSDKHelper;
import maimeng.yodian.app.client.android.chat.activity.ChatActivity;
import maimeng.yodian.app.client.android.chat.db.UserDao;
import maimeng.yodian.app.client.android.chat.domain.RobotUser;
import maimeng.yodian.app.client.android.common.DefaultItemTouchHelperCallback;
import maimeng.yodian.app.client.android.common.PullHeadView;
import maimeng.yodian.app.client.android.model.skill.Banner;
import maimeng.yodian.app.client.android.model.skill.DataNode;
import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.entry.skillseletor.BannerViewEntry;
import maimeng.yodian.app.client.android.entry.skillseletor.HeadViewEntry;
import maimeng.yodian.app.client.android.entry.skillseletor.ItemViewEntry;
import maimeng.yodian.app.client.android.entry.skillseletor.ViewEntry;
import maimeng.yodian.app.client.android.model.Theme;
import maimeng.yodian.app.client.android.model.User;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.response.SkillResponse;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.view.MainTabActivity;
import maimeng.yodian.app.client.android.view.WebViewActivity;
import maimeng.yodian.app.client.android.view.chat.ChatMainActivity;
import maimeng.yodian.app.client.android.view.dialog.AlertDialog;
import maimeng.yodian.app.client.android.view.dialog.ShareDialog;
import maimeng.yodian.app.client.android.view.skill.CreateOrEditSkillActivity;
import maimeng.yodian.app.client.android.view.skill.SkillDetailsActivity;
import maimeng.yodian.app.client.android.view.user.UserHomeActivity;
import maimeng.yodian.app.client.android.widget.CategoryLayout;
import maimeng.yodian.app.client.android.widget.CategoryView;
import maimeng.yodian.app.client.android.widget.EndlessRecyclerOnScrollListener;
import maimeng.yodian.app.client.android.widget.ListLayoutManager;
import maimeng.yodian.app.client.android.widget.PagerRecyclerView;

/**
 * Created by android on 15-7-13.
 */

public class MainSelectorProxy implements ActivityProxy, EMEventListener,
        AbstractAdapter.ViewHolderClickListener<SkillListSelectorAdapter.BaseViewHolder>,
        PtrHandler, Callback<SkillResponse>, View.OnClickListener, CategoryView.CategoryClickListener {

    private static final String LOG_TAG = MainSelectorProxy.class.getName();
    private static final int CATEGORY_ANIM_ENTER = 0x37;
    private static final int CATEGORY_ANIM_DISMISS = 0x32;
    private final View mView;
    private final MainTabActivity mActivity;
    private final SkillService service;
    private final PtrFrameLayout mRefreshLayout;
    private final PagerRecyclerView mRecyclerView;
    private final CategoryLayout mCategoryContainer;
    private final View mBtnChat;
    private boolean inited = false;
    private User user;
    private int dgree = 0;
    private int page = 1;
    private int scid = 0;
    private final SkillListSelectorAdapter adapter;
    private final EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    private FloatingActionButton mFloatButton;
    private final Handler handler;

    private List<Theme> mCategory;
    private CategoryView mCategoryView;
    private ImageView mTitleIndicator;
    private android.support.v7.widget.Toolbar mToolBar;
    private TextView mTitle;
    private View mTitleBar;
    private ObjectAnimator animator;


    public MainSelectorProxy(MainTabActivity activity, View view) {
        this.mView = view;
        handler = new Handler(Looper.getMainLooper());
        view.setVisibility(View.GONE);
        this.mActivity = activity;
        service = Network.getService(SkillService.class);
        mCategoryContainer = (CategoryLayout) view.findViewById(R.id.categoryContainer);
        mBtnChat = view.findViewById(R.id.btn_chat);
        mBtnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.startActivity(new Intent(mActivity, ChatMainActivity.class));
            }
        });
        mToolBar = (android.support.v7.widget.Toolbar) view.findViewById(R.id.toolbar);
        mCategoryView = (CategoryView) mCategoryContainer.findViewById(R.id.category);
        mCategoryView.setCategoryClickListener(this);
        mTitleBar = view.findViewById(R.id.ll_title);
        mTitleIndicator = (ImageView) view.findViewById(R.id.title_logo);
        mTitle = (TextView) view.findViewById(R.id.list_title);
        mTitleBar.setOnClickListener(this);
        mTitleBar.setTag(CATEGORY_ANIM_ENTER);
        mRefreshLayout = (PtrFrameLayout) view.findViewById(R.id.refresh_layout);
        mRecyclerView = (PagerRecyclerView) view.findViewById(R.id.recyclerView);
        mRefreshLayout.setPtrHandler(this);
        StoreHouseHeader header = PullHeadView.create(mActivity);
        header.setTextColor(0x000000);
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
        adapter = new SkillListSelectorAdapter(mActivity, this, mRefreshLayout);
        mRecyclerView.setAdapter(adapter);

        animator = ObjectAnimator.ofFloat(mTitleIndicator, View.ROTATION, 180);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                if (mTitleBar.getTag().equals(CATEGORY_ANIM_ENTER)) {
                    mCategoryContainer.enterAnimator.setCurrentPlayTime(animation.getCurrentPlayTime());
                } else {
                    mCategoryContainer.dismissAnimator.setCurrentPlayTime(animation.getCurrentPlayTime());
                }
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                dgree++;
                mCategoryContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                if (mTitleBar.getTag().equals(CATEGORY_ANIM_ENTER)) {
                    mTitleBar.setTag(CATEGORY_ANIM_DISMISS);
                } else {
                    mTitleBar.setTag(CATEGORY_ANIM_ENTER);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setDuration(mActivity.getResources().getInteger(R.integer.duration_long));
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (!animator.isRunning() && animator != null && (Integer) mTitleBar.getTag() == CATEGORY_ANIM_DISMISS) {
                    categoryEnterAndDismissAnim();
                }


                return false;
            }
        });

        ItemTouchHelper swipeTouchHelper = new ItemTouchHelper(new DefaultItemTouchHelperCallback());
        //swipeTouchHelper.attachToRecyclerView(mRecyclerView);
    }


    @Override
    public void onClickListener(View v, Theme theme) {

        scid = (int) theme.getScid();
        page=1;
        syncRequest();
        mRecyclerView.scrollToPosition(0);
        mTitle.setText(theme.getName());

        categoryEnterAndDismissAnim();

    }

    @Override
    public void onClick(View v) {
        categoryEnterAndDismissAnim();
    }

    private void categoryEnterAndDismissAnim() {
        mCategoryContainer.initAnimator(mActivity.getResources().getInteger(R.integer.duration_long), mToolBar.getHeight());

        animator.setFloatValues(180 * (dgree + 1));
        if (animator != null && !animator.isRunning()) {
            animator.setupStartValues();
            animator.start();
        }


    }

    public void syncRequest() {
        service.choose(page, scid, this);
    }

    @Override
    public void reset() {
        page = 1;
    }

    public boolean isInited() {
        return this.inited;
    }

    @Override
    public void init() {
        inited = true;
        page = 1;
        mRefreshLayout.autoRefresh();
    }


    @Override
    public void onTitleChanged(CharSequence title, int color) {
    }

    @Override
    public void show(final FloatingActionButton button) {
        show(button, true);

    }

    @Override
    public void show(final FloatingActionButton button, boolean anima) {
        int unread = EMChatManager.getInstance().getUnreadMsgsCount();
        if (unread > 0) {
            mView.findViewById(R.id.miss_msg_count).setVisibility(View.VISIBLE);
        } else {
            mView.findViewById(R.id.miss_msg_count).setVisibility(View.INVISIBLE);
        }
        if (anima) {
            mFloatButton = button;
            button.attachToRecyclerView(mRecyclerView);
            mActivity.setTitle("优点精选");
            button.setImageResource(R.drawable.btn_home_change_normal);
            int type = TranslateAnimation.RELATIVE_TO_SELF;
            TranslateAnimation animation = new TranslateAnimation(type, 0, type, 0, type, 1f, type, 0);
            animation.setInterpolator(new AccelerateInterpolator());
            animation.setDuration(mActivity.getResources().getInteger(R.integer.duration));
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    button.setEnabled(false);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    button.setEnabled(true);
                    mView.setVisibility(View.VISIBLE);
//                setStatusBarColor(mActivity.getResources().getColor(R.color.colorPrimary));
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            this.mView.startAnimation(animation);
        } else {
            mView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hide(final FloatingActionButton button) {
        hide(button, true);

    }

    @Override
    public void hide(final FloatingActionButton button, boolean anima) {
        if (anima) {
            if (!mActivity.getProxyHome().isInited()) mActivity.getProxyHome().init();
            mActivity.setTitle("首页");
            button.setImageResource(R.drawable.btn_selected_normal);
            int type = TranslateAnimation.RELATIVE_TO_SELF;
            AnimationSet animationSet = new AnimationSet(true);
            animationSet.setDuration(mActivity.getResources().getInteger(R.integer.duration));
            animationSet.setInterpolator(new AccelerateInterpolator());
            animationSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    button.setEnabled(false);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    button.setEnabled(true);
                    mView.setVisibility(View.GONE);
//                    setStatusBarColor(mActivity.getResources().getColor(R.color.colorPrimaryGreen));
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });


            TranslateAnimation animation = new TranslateAnimation(type, 0, type, 0, type, 0, type, 1f);
            int[] xy = new int[2];
            button.getLocationOnScreen(xy);
            animationSet.addAnimation(animation);
            this.mView.startAnimation(animationSet);
        } else {
            mView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean isShow() {
        return mView.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onItemClick(final SkillListSelectorAdapter.BaseViewHolder h, int postion) {

        if (h instanceof SkillListSelectorAdapter.ItemViewHolder) {
            mFloatButton.show();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Pair<View, String> back = Pair.create((View) mFloatButton, "back");
//                Pair<View, String> contact = Pair.create((View) holder.getBinding().pic, "pic");
//                Pair<View, String> nick = Pair.create((View) holder.getBinding().userNickname, "nick");
//                Pair<View, String> avatar = Pair.create((View) holder.getBinding().userAvatar, "avatar");
                    //ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, back, contact, avatar);
                    Skill skill = ((SkillListSelectorAdapter.ItemViewHolder) h).getData();
                    if (skill.getStatus() == 0) {
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, back);
                        ActivityCompat.startActivity(mActivity, new Intent(mActivity, SkillDetailsActivity.class).putExtra("skill", skill), options.toBundle());
                    }

                }
            }, 200);
        }
    }

    private void clickBanner(SkillListSelectorAdapter.BannerViewHolder holder) {
        int current = holder.currentPage % holder.list.banners.size();
        Banner banner = holder.list.banners.get(current);
        if (banner.getType() == 3) {
            Pair<View, String> back = Pair.create((View) mFloatButton, "back");
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, back);
            ActivityCompat.startActivity(mActivity, new Intent(mActivity, SkillDetailsActivity.class).putExtra("sid", Long.parseLong(banner.getValue())), options.toBundle());
        } else if (banner.getType() == 2) {
            Pair<View, String> back = Pair.create((View) mFloatButton, "back");
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, back);
            ActivityCompat.startActivity(mActivity, new Intent(mActivity, UserHomeActivity.class).putExtra("uid", Long.parseLong(banner.getValue())), options.toBundle());
        } else if (banner.getType() == 1) {
            Pair<View, String> back = Pair.create((View) mFloatButton, "back");
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, back);
            ActivityCompat.startActivity(mActivity, WebViewActivity.newIntent(mActivity, banner.getValue()), options.toBundle());

        }
    }

    /**
     * @param type 1:skill,2:user
     * @param data
     */
    private void clickHead(int type, HeadViewEntry data) {
        if (type == 1) {
            Pair<View, String> back = Pair.create((View) mFloatButton, "back");
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, back);
            ActivityCompat.startActivity(mActivity, new Intent(mActivity, SkillDetailsActivity.class).putExtra("sid", data.skill.getValue()), options.toBundle());
        } else {
            Pair<View, String> back = Pair.create((View) mFloatButton, "back");
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, back);
            ActivityCompat.startActivity(mActivity, new Intent(mActivity, UserHomeActivity.class).putExtra("uid", data.user.getValue()), options.toBundle());
        }
    }

    private int mEditPostion;

    @Override
    public void onClick(SkillListSelectorAdapter.BaseViewHolder h, View clickItem, final int postion) {


        if (h instanceof SkillListSelectorAdapter.ItemViewHolder) {
            final SkillListSelectorAdapter.ItemViewHolder holder = (SkillListSelectorAdapter.ItemViewHolder) h;
            final Skill skill = holder.getData();
            if (clickItem == holder.getBinding().btnShare) {
                Skill data = skill;
                ShareDialog.show(mActivity, new ShareDialog.ShareParams(data, data.getQrcodeUrl(), data.getUid(), data.getNickname(), ""), 1);
            } else if (clickItem == holder.getBinding().userAvatar) {
                UserHomeActivity.show(mActivity, skill.getUid(), holder.getDefaultAvatar(), skill.getNickname(), mFloatButton, null,
                        holder.getBinding().userNickname);
            } else if (clickItem == holder.getBinding().btnUpdate) {
                Intent intent = new Intent(mActivity, CreateOrEditSkillActivity.class);
                intent.putExtra("skill", skill);
                mActivity.startActivityForResult(intent, ActivityProxyController.REQUEST_EDIT_SKILL);
                mEditPostion = postion;
                holder.closeWithAnim();
            } else if (clickItem == holder.getBinding().btnDelete) {
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

            } else if (clickItem == holder.getBinding().btnChangeState) {
                service.up(skill.getId(), skill.getStatus(), new ToastCallback(mActivity) {
                    @Override
                    public void success(ToastResponse res, Response response) {
                        super.success(res, response);
                        skill.setStatus(skill.getStatus() == 0 ? 1 : 0);
                        holder.closeWithAnim();
                    }
                });
            } else if (clickItem == holder.getBinding().btnBottom) {
                Intent intent = new Intent(mActivity, ChatActivity.class);
                intent.putExtra("skill", holder.getData());
                intent.putExtra("uid",holder.getData().getUid());
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
        } else if (SkillListSelectorAdapter.HeadViewHolder.class.isInstance(h)) {
            SkillListSelectorAdapter.HeadViewHolder holder = (SkillListSelectorAdapter.HeadViewHolder) h;
            if (clickItem == holder.binding.headSkill) {
                clickHead(1, holder.data);
            } else if (clickItem == holder.binding.headUser) {
                clickHead(2, holder.data);
            }
        } else if (SkillListSelectorAdapter.BannerViewHolder.class.isInstance(h)) {
            clickBanner(((SkillListSelectorAdapter.BannerViewHolder) h));
        }

    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View content, View header) {
        // 默认实现，根据实际情况做改动
        return PtrDefaultHandler.checkContentCanBePulledDown(ptrFrameLayout, content, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
        reset();
        syncRequest();

    }

    @Override
    public void start() {

    }

    @Override
    public void success(SkillResponse res, Response response) {
        if (res.isSuccess()) {
            DataNode data = res.getData();
            List<Skill> list = data.getList();
            final List<ViewEntry> entries;
            if (page == 1) {
                entries = new ArrayList<>(list.size() + 2);
                List<Banner> banners = data.getBanner();
                entries.add(new BannerViewEntry(banners));
                entries.add(new HeadViewEntry(data.getHeadSkill(), data.getHeadUser()));
            } else {
                entries = new ArrayList<>(list.size());
            }

            for (Skill skill : list) {
                entries.add(new ItemViewEntry(skill));
            }
            adapter.reload(entries, page != 1);
            adapter.notifyDataSetChanged();

            if (page == 1) {
                mCategory = res.getData().getCategory();
                if (mCategory == null) {
                    mCategory = new ArrayList<Theme>();
                }
                mCategoryView.bindData(mActivity, mCategory);
            }


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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_AUTH && resultCode == Activity.RESULT_OK) {
            init();
        } else if (requestCode == ActivityProxyController.REQUEST_EDIT_SKILL && resultCode == Activity.RESULT_OK) {
            Skill skill = data.getParcelableExtra("skill");
            if (mEditPostion != -1 && skill != null) {
                ((ItemViewEntry) adapter.getItem(mEditPostion)).skill.update(skill);
                adapter.notifyItemChanged(mEditPostion);
                mEditPostion = -1;
            }
        }
    }

    @Override
    public void onResume() {
        if(User.read(mActivity).isPushOn()){
            EMChatManager.getInstance().registerEventListener(this);
        }else {
            EMChatManager.getInstance().unregisterEventListener(this);
        }

        refreshMissMsgIcon();
    }

    @Override
    public void onPause() {
        EMChatManager.getInstance().unregisterEventListener(this);
    }

    private void refreshMissMsgIcon() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int unread = EMChatManager.getInstance().getUnreadMsgsCount();
                if(!User.read(mActivity).isPushOn()){
                    unread=0;
                }
                if (unread > 0) {
                    mView.findViewById(R.id.miss_msg_count).setVisibility(View.VISIBLE);
                } else {
                    mView.findViewById(R.id.miss_msg_count).setVisibility(View.INVISIBLE);
                }

            }
        });
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
}
