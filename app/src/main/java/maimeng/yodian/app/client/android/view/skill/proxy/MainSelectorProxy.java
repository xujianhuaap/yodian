package maimeng.yodian.app.client.android.view.skill.proxy;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.applib.controller.HXSDKHelper;
import com.melnykov.fab.FloatingActionButton;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import kotlin.data;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.adapter.AbstractAdapter;
import maimeng.yodian.app.client.android.adapter.RmarkAdapter;
import maimeng.yodian.app.client.android.adapter.SkillListSelectorAdapter;
import maimeng.yodian.app.client.android.chat.DemoHXSDKHelper;
import maimeng.yodian.app.client.android.chat.activity.ChatActivity;
import maimeng.yodian.app.client.android.chat.db.UserDao;
import maimeng.yodian.app.client.android.chat.domain.RobotUser;
import maimeng.yodian.app.client.android.common.DefaultItemTouchHelperCallback;
import maimeng.yodian.app.client.android.common.PullHeadView;
import maimeng.yodian.app.client.android.common.model.Skill;
import maimeng.yodian.app.client.android.entry.skillseletor.BannerViewEntry;
import maimeng.yodian.app.client.android.entry.skillseletor.HeadViewEntry;
import maimeng.yodian.app.client.android.entry.skillseletor.ItemViewEntry;
import maimeng.yodian.app.client.android.entry.skillseletor.ViewEntry;
import maimeng.yodian.app.client.android.model.Theme;
import maimeng.yodian.app.client.android.model.User;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.SkillResponse;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.view.MainTabActivity;
import maimeng.yodian.app.client.android.view.dialog.ShareDialog;
import maimeng.yodian.app.client.android.view.skill.SkillDetailsActivity;
import maimeng.yodian.app.client.android.widget.CategoryView;
import maimeng.yodian.app.client.android.widget.EndlessRecyclerOnScrollListener;
import maimeng.yodian.app.client.android.widget.ListLayoutManager;

/**
 * Created by android on 15-7-13.
 */

public class MainSelectorProxy implements ActivityProxy,
        AbstractAdapter.ViewHolderClickListener<SkillListSelectorAdapter.BaseViewHolder>,
        PtrHandler, Callback<SkillResponse>, View.OnClickListener, CategoryView.CategoryClickListener {

    private static final String LOG_TAG = MainSelectorProxy.class.getName();
    private static final int mTitleStatus = 0x37;
    private final View mView;
    private final MainTabActivity mActivity;
    private final SkillService service;
    private final PtrFrameLayout mRefreshLayout;
    private final RecyclerView mRecyclerView;
    private boolean inited = false;
    private User user;
    private int dgree = 1;
    private int page = 1;
    private int scid = 0;
    private final SkillListSelectorAdapter adapter;
    private final EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    private FloatingActionButton mFloatButton;
    private final Handler handler;

    private List<Theme> mCategory;
    private CategoryView mCategoryView;
    private ImageView mTitleIndicator;
    private TextView mTitle;
    private ObjectAnimator animator;


    public MainSelectorProxy(MainTabActivity activity, View view) {
        this.mView = view;
        handler = new Handler(Looper.getMainLooper());
        view.setVisibility(View.GONE);
        this.mActivity = activity;
        service = Network.getService(SkillService.class);

        mCategoryView = (CategoryView) view.findViewById(R.id.category);
        mCategoryView.setCategoryClickListener(this);
        mTitleIndicator = (ImageView) view.findViewById(R.id.title_logo);
        mTitle = (TextView) view.findViewById(R.id.list_title);
        mTitle.setOnClickListener(this);
        mTitle.setTag(mTitleStatus);
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
                loadData();
            }
        };
        mRecyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
        adapter = new SkillListSelectorAdapter(mActivity, this);
        mRecyclerView.setAdapter(adapter);

        animator = ObjectAnimator.ofFloat(mTitleIndicator, View.ROTATION, 180 * dgree);
        animator.setDuration(300);
        ItemTouchHelper swipeTouchHelper = new ItemTouchHelper(new DefaultItemTouchHelperCallback());
        //swipeTouchHelper.attachToRecyclerView(mRecyclerView);
    }


    @Override
    public void onClickListener(View v, Theme theme) {
        scid = (int) theme.getScid();
        loadData();
        mTitle.setText(theme.getName());
    }

    @Override
    public void onClick(View v) {
        if (animator != null && !animator.isRunning()) {
            if (mTitleStatus == v.getTag()) {

                mCategoryView.show(600);
                v.setTag(0);
            } else {
                mCategoryView.dismiss(600);
                v.setTag(mTitleStatus);
            }

            animator.start();
            dgree++;

        }

    }

    public void loadData() {
        service.choose(page, 0, this);
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

    }

    @Override
    public void hide(final FloatingActionButton button) {
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
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, back);
                    ActivityCompat.startActivity(mActivity, new Intent(mActivity, SkillDetailsActivity.class).putExtra("skill", ((SkillListSelectorAdapter.ItemViewHolder) h).getData()), options.toBundle());
                }
            }, 200);
        }
    }

    private void clickBanner(SkillListSelectorAdapter.BannerViewHolder holder) {
        int current = holder.currentPage % holder.list.banners.size();
        SkillResponse.DataNode.Banner banner = holder.list.banners.get(current);
        if (banner.getType() == 3) {
            Pair<View, String> back = Pair.create((View) mFloatButton, "back");
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, back);
            ActivityCompat.startActivity(mActivity, new Intent(mActivity, SkillDetailsActivity.class).putExtra("sid", Long.parseLong(banner.getValue())), options.toBundle());
        } else if (banner.getType() == 2) {
            Toast.makeText(mActivity, "用户", Toast.LENGTH_SHORT).show();
        } else if (banner.getType() == 1) {
            Toast.makeText(mActivity, "网址", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(mActivity, "click Head User", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(SkillListSelectorAdapter.BaseViewHolder h, View clickItem, int postion) {
        if (h instanceof SkillListSelectorAdapter.ItemViewHolder) {
            SkillListSelectorAdapter.ItemViewHolder holder = (SkillListSelectorAdapter.ItemViewHolder) h;
            Skill skill = holder.getData();
            if (clickItem == holder.getBinding().btnShare) {
                Skill data = skill;
                ShareDialog.show(mActivity, new ShareDialog.ShareParams(data, data.getQrcodeUrl(), data.getUid(), data.getNickname(), ""));
            } else if (clickItem == holder.getBinding().btnContect) {
                Intent intent = new Intent(mActivity, ChatActivity.class);
                intent.putExtra("skill", holder.getData());
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

                    maimeng.yodian.app.client.android.chat.domain.User user = new maimeng.yodian.app.client.android.chat.domain.User();
                    user.setId(skill.getUid() + "");
                    user.setUsername(chatLoginName);
                    user.setNick(skill.getNickname());
                    user.setAvatar(skill.getAvatar());
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
        loadData();

    }

    @Override
    public void start() {

    }

    @Override
    public void success(SkillResponse res, Response response) {
        if (res.isSuccess()) {
            SkillResponse.DataNode data = res.getData();
            List<Skill> list = data.getList();
            final List<ViewEntry> entries;
            if (page == 1) {
                entries = new ArrayList<>(list.size() + 2);
                entries.add(new BannerViewEntry(data.getBanner()));
                entries.add(new HeadViewEntry(data.getHeadSkill(), data.getHeadUser()));
            } else {
                entries = new ArrayList<>(list.size());
            }

            for (Skill skill : list) {
                entries.add(new ItemViewEntry(skill));
            }
            adapter.reload(entries, page != 1);
            adapter.notifyDataSetChanged();

            mCategory = res.getData().getCategory();
            if (mCategory == null) {
                mCategory = new ArrayList<Theme>();
            }
            mCategoryView.bindData(mActivity, mCategory);
        } else {
            res.showMessage(mActivity);
            if (!res.isValidateAuth(mActivity, REQUEST_AUTH)) {
//                if(!mActivity.isFinishing()){
//                    mActivity.finish();
//                }
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
        }
    }
}
