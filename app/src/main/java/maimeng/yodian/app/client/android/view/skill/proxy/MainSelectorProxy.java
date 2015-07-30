package maimeng.yodian.app.client.android.view.skill.proxy;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

import com.easemob.applib.controller.HXSDKHelper;
import com.melnykov.fab.FloatingActionButton;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

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
import maimeng.yodian.app.client.android.common.DefaultItemTouchHelperCallback;
import maimeng.yodian.app.client.android.common.PullHeadView;
import maimeng.yodian.app.client.android.model.User;
import maimeng.yodian.app.client.android.model.Skill;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.SkillResponse;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.view.MainTabActivity;
import maimeng.yodian.app.client.android.view.dialog.ShareDialog;
import maimeng.yodian.app.client.android.view.skill.SkillDetailsActivity;
import maimeng.yodian.app.client.android.widget.EndlessRecyclerOnScrollListener;

/**
 * Created by android on 15-7-13.
 */
public class MainSelectorProxy implements ActivityProxy,AbstractAdapter.ViewHolderClickListener<SkillListSelectorAdapter.ViewHolder>, PtrHandler, Callback<SkillResponse> {
    private final View mView;
    private final MainTabActivity mActivity;
    private final SkillService service;
    private final PtrFrameLayout mRefreshLayout;
    private final RecyclerView mRecyclerView;
    private boolean inited=false;
    private User user;
    private int page=1;
    private final SkillListSelectorAdapter adapter;
    private final EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    public MainSelectorProxy(MainTabActivity activity, View view){
        this.mView=view;
        view.setVisibility(View.GONE);
        this.mActivity=activity;
        service= Network.getService(SkillService.class);
        mRefreshLayout=(PtrFrameLayout)view.findViewById(R.id.refresh_layout);
        mRecyclerView=(RecyclerView)view.findViewById(R.id.recyclerView);
        mRefreshLayout.setPtrHandler(this);
        StoreHouseHeader header= PullHeadView.create(mActivity);
        mRefreshLayout.addPtrUIHandler(header);
        mRefreshLayout.setHeaderView(header);
        LinearLayoutManager layout = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(layout);
        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(layout) {
            @Override
            public void onLoadMore() {
                page ++;
                loadData();
            }
        };
        mRecyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
        adapter=new SkillListSelectorAdapter(mActivity,this);
        mRecyclerView.setAdapter(adapter);
        ItemTouchHelper swipeTouchHelper=new ItemTouchHelper(new DefaultItemTouchHelperCallback());
        //swipeTouchHelper.attachToRecyclerView(mRecyclerView);
    }
    public void loadData() {
        service.choose(page, this);
    }

    @Override
    public void reset() {
        page=1;
    }

    public boolean isInited(){
        return this.inited;
    }
    @Override
    public void init() {
        inited=true;
        page=1;
        mRefreshLayout.autoRefresh();
    }

    @Override
    public void onTitleChanged(CharSequence title, int color) {
    }

    @Override
    public void show(final FloatingActionButton button) {
        mActivity.setTitle("优点精选");
        button.setImageResource(R.drawable.btn_home_change_normal);
        int type = TranslateAnimation.RELATIVE_TO_SELF;
        TranslateAnimation animation = new TranslateAnimation(type,0,type, 0,type,1f,type, 0);
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
        if(!mActivity.getProxyHome().isInited())mActivity.getProxyHome().init();
        mActivity.setTitle("首页");
        button.setImageResource(R.drawable.btn_selected_normal);
        int type = TranslateAnimation.RELATIVE_TO_SELF;
        AnimationSet animationSet=new AnimationSet(true);
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


        TranslateAnimation animation = new TranslateAnimation(type,0,type, 0,type,0,type, 1f);
        int[] xy=new int[2];
        button.getLocationOnScreen(xy);
        animationSet.addAnimation(animation);
        this.mView.startAnimation(animationSet);

    }

    @Override
    public boolean isShow() {
        return mView.getVisibility()==View.VISIBLE;
    }

    @Override
    public void onItemClick(SkillListSelectorAdapter.ViewHolder holder, int postion) {
        Intent intent = new Intent(mActivity, SkillDetailsActivity.class);
        intent.putExtra("sid",holder.getData().getId());
        mActivity.startActivity(intent);
    }

    @Override
    public void onClick(SkillListSelectorAdapter.ViewHolder holder, View clickItem, int postion) {
        Skill skill = holder.getData();
        if(clickItem==holder.getBinding().btnShare){
            Skill data = skill;
            ShareDialog.show(mActivity, new ShareDialog.ShareParams(data,data.getQrcodeUrl(),data.getUid(), data.getNickname(),""));
        }else if(clickItem==holder.getBinding().btnContect){
            Intent intent = new Intent(mActivity, ChatActivity.class);
            Map<String, RobotUser> robotMap = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getRobotList();
            String chatLoginName = skill.getChatLoginName();
            if(robotMap.containsKey(chatLoginName)) {
                intent.putExtra("userId", chatLoginName);
                mActivity.startActivity(intent);
            }else{
                RobotUser robot=new RobotUser();
                robot.setId(skill.getUid() + "");
                robot.setUsername(chatLoginName);
                robot.setNick(skill.getNickname());
                robot.setAvatar(skill.getAvatar());

                maimeng.yodian.app.client.android.chat.domain.User user=new maimeng.yodian.app.client.android.chat.domain.User();
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
        if(res.isSuccess()){
            List<Skill> list = res.getData().getList();
            adapter.reload(list,page!=1);
            adapter.notifyDataSetChanged();
        }else{
            res.showMessage(mActivity);
            if(!res.isValidateAuth(mActivity,REQUEST_AUTH)){
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
        if(requestCode==REQUEST_AUTH && resultCode== Activity.RESULT_OK){
            init();
        }
    }
}
