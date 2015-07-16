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

import com.melnykov.fab.FloatingActionButton;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import java.util.List;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.adapter.AbstractAdapter;
import maimeng.yodian.app.client.android.adapter.SkillListAdapter;
import maimeng.yodian.app.client.android.common.DefaultItemTouchHelperCallback;
import maimeng.yodian.app.client.android.common.UserAuth;
import maimeng.yodian.app.client.android.model.Skill;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.SkillResponse;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.view.MainTabActivity;
import maimeng.yodian.app.client.android.view.dialog.ShareDialog;
import maimeng.yodian.app.client.android.widget.EndlessRecyclerOnScrollListener;

/**
 * Created by android on 15-7-13.
 */
public class MainListProxy implements ActivityProxy,AbstractAdapter.ViewHolderClickListener<SkillListAdapter.ViewHolder>, PtrHandler, Callback<SkillResponse> {
    private final View mView;
    private final MainTabActivity mActivity;
    private final SkillService service;
    private final PtrFrameLayout mRefreshLayout;
    private final RecyclerView mRecyclerView;
    private boolean inited=false;
    private UserAuth user;
    private int page=1;
    private final SkillListAdapter adapter;
    private final EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    public MainListProxy(MainTabActivity activity, View view){
        this.mView=view;
        view.setVisibility(View.GONE);
        this.mActivity=activity;
        service= Network.getService(SkillService.class);
        mRefreshLayout=(PtrFrameLayout)view.findViewById(R.id.refresh_layout);
        mRecyclerView=(RecyclerView)view.findViewById(R.id.recyclerView);
        mRefreshLayout.setPtrHandler(this);
        StoreHouseHeader header=new StoreHouseHeader(activity);
        header.setPadding(0, (int) mActivity.getResources().getDimension(R.dimen.pull_refresh_paddingTop), 0, 0);
        header.initWithString("YoDian");
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
        adapter=new SkillListAdapter(mActivity,this);
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
        button.setImageResource(R.drawable.btn_home_change);
        mActivity.setTitle("优点精选");
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
        button.setImageResource(R.drawable.btn_bg);
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
    public void onItemClick(SkillListAdapter.ViewHolder holder, int postion) {

    }

    @Override
    public void onClick(SkillListAdapter.ViewHolder holder, View clickItem, int postion) {
        if(clickItem==holder.getBinding().btnShare){
            Skill data = holder.getData();
            ShareDialog.show(mActivity, new ShareDialog.ShareParams(data.getId(), "http://www.baidu.com/", data.getPic(), data.getName(), data.getUid(), data.getNickname(), data.getPrice(), data.getUnit(), ""));
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
