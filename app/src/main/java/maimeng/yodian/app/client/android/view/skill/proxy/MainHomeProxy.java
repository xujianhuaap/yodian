package maimeng.yodian.app.client.android.view.skill.proxy;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

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
import maimeng.yodian.app.client.android.model.User;
import maimeng.yodian.app.client.android.model.Skill;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.response.SkillResponse;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.view.MainTabActivity;
import maimeng.yodian.app.client.android.view.SettingsActivity;
import maimeng.yodian.app.client.android.view.dialog.ShareDialog;
import maimeng.yodian.app.client.android.view.skill.CreateOrEditSkillActivity;
import maimeng.yodian.app.client.android.view.skill.SkillDetailsActivity;
import maimeng.yodian.app.client.android.view.skill.SkillTemplateActivity;
import maimeng.yodian.app.client.android.view.user.SettingUserInfo;
import maimeng.yodian.app.client.android.widget.EndlessRecyclerOnScrollListener;
import maimeng.yodian.app.client.android.widget.RoundImageView;

/**
 * Created by android on 15-7-13.
 */
public class MainHomeProxy implements ActivityProxy,AbstractAdapter.ViewHolderClickListener<SkillListAdapter.ViewHolder>, PtrHandler, Callback<SkillResponse>, AppBarLayout.OnOffsetChangedListener, View.OnClickListener {
    private static final int REQUEST_UPDATEINFO = 0x5005;
    private final CoordinatorLayout mView;
    private final MainTabActivity mActivity;
    private final RecyclerView mRecyclerView;
    private final PtrFrameLayout mRefreshLayout;
    private final SkillService service;
    private final AppBarLayout appBar;
    private final RoundImageView mUserAvatar;
    private final TextView mUserNickname;
    private final View mBtnCreateSkill;
    private final View mBtnSettings;
    private boolean inited=false;
    private final SkillListAdapter adapter;
    private final EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    private User user;
    private int page=1;
    private FloatingActionButton mFloatButton;
    private int mEditPostion;

    public MainHomeProxy(MainTabActivity activity, View view){
        this.mView=(CoordinatorLayout)view;
        this.mActivity=activity;
        view.setVisibility(View.GONE);
        service=Network.getService(SkillService.class);
        appBar=(AppBarLayout)view.findViewById(R.id.appBarLayout);
        mBtnSettings=view.findViewById(R.id.btn_settings);
        mBtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pair<View,String> back=Pair.create((View)mFloatButton,"back");
                ActivityOptionsCompat options=ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, back);
                ActivityCompat.startActivity(mActivity,new Intent(mActivity, SettingsActivity.class),options.toBundle());
            }
        });
        mRefreshLayout=(PtrFrameLayout)view.findViewById(R.id.refresh_layout);
        mRecyclerView=(RecyclerView)view.findViewById(R.id.recyclerView);
        mUserAvatar=(RoundImageView)view.findViewById(R.id.user_avatar);
        mUserAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pair<View,String> avatar=Pair.create(v,"avatar");
                Pair<View,String> back=Pair.create((View)mFloatButton,"back");
                ActivityOptionsCompat options=ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, avatar, back);
                ActivityCompat.startActivityForResult(mActivity,new Intent(mActivity, SettingUserInfo.class),REQUEST_UPDATEINFO,options.toBundle());
            }
        });
        mUserNickname=(TextView)view.findViewById(R.id.user_nickname);
        mBtnCreateSkill=view.findViewById(R.id.btn_createskill);
        mBtnCreateSkill.setOnClickListener(this);
        appBar.addOnOffsetChangedListener(this);
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
    }

    public void loadData() {
        service.list(user.getUid(), page, this);
    }

    @Override
    public void reset() {
        page=1;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            if(requestCode==ActivityProxyController.REQUEST_CREATE_SKILL){
                init();
            }else if(requestCode==REQUEST_AUTH){
                init();
            }else if(requestCode==ActivityProxyController.REQUEST_EDIT_SKILL){
                Skill skill = data.getParcelableExtra("skill");
                if(mEditPostion!= -1 && skill!=null) {
                    adapter.getItem(mEditPostion).update(skill);
                    adapter.notifyItemChanged(mEditPostion);
                    mEditPostion = -1;
                }
            }else if(requestCode==REQUEST_UPDATEINFO){
                user= User.read(this.mActivity);
                initUsrInfo();
            }
        }
    }

    public boolean isInited(){
        return this.inited;
    }
    @Override
    public void init() {
        inited=true;
        user= User.read(this.mActivity);
        initUsrInfo();
        initSkillInfo();


    }

    private void initSkillInfo() {
        page=1;
        mRefreshLayout.autoRefresh();
    }

    private void initUsrInfo() {
        mUserNickname.setText(user.getNickname());
        Network.image(mUserAvatar, user.getAvatar());
    }

    @Override
    public void onTitleChanged(CharSequence title, int color) {
    }

    @Override
    public void show(FloatingActionButton button) {
        this.mFloatButton=button;
        AlphaAnimation alphaAnimation=new AlphaAnimation(0f,1f);
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

    }

    @Override
    public void hide(FloatingActionButton button) {
        AlphaAnimation alphaAnimation=new AlphaAnimation(1f,0f);
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

    }

    @Override
    public boolean isShow() {
        return false;
    }

    @Override
    public void onItemClick(SkillListAdapter.ViewHolder holder, int postion) {
        mActivity.startActivity(new Intent(mActivity, SkillDetailsActivity.class));
    }

    @Override
    public void onClick(final SkillListAdapter.ViewHolder holder, View clickItem, final int postion) {
        final Skill skill = holder.getData();
        if(clickItem==holder.getBinding().btnShare){
            Skill data = skill;
            ShareDialog.show(mActivity, new ShareDialog.ShareParams(data,data.getQrcodeUrl(),data.getUid(), data.getNickname(),""));
        }else if(clickItem==holder.getBinding().btnUpdate){
            Intent intent=new Intent(mActivity,CreateOrEditSkillActivity.class);
            intent.putExtra("skill", skill);
            mActivity.startActivityForResult(intent, ActivityProxyController.REQUEST_EDIT_SKILL);
            mEditPostion=postion;
            holder.closeWithAnim();
        }else if(clickItem==holder.getBinding().btnDelete){
            service.delete(skill.getId(),new ToastCallback(mActivity){
                @Override
                public void success(ToastResponse res, Response response) {
                    super.success(res, response);
                    if(res.isSuccess()){
                        adapter.remove(postion);
                    }
                }
            });
        }else if(clickItem==holder.getBinding().btnChangeState){
            service.up(skill.getId(), skill.getStatus(), new ToastCallback(mActivity){
                @Override
                public void success(ToastResponse res, Response response) {
                    super.success(res, response);
                    skill.setStatus(skill.getStatus() == 0 ? 1 : 0);
                    holder.closeWithAnim();
                }
            });
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
        ErrorUtils.checkError(mActivity,hNetError);
    }

    @Override
    public void end() {
        mRefreshLayout.refreshComplete();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        mRefreshLayout.setEnabled(i==0);
    }

    @Override
    public void onClick(View v) {
        Pair<View,String> top=Pair.create(v,"top");
        Pair<View,String> floatbutton=Pair.create((View)mFloatButton,"floatbutton");
        ActivityOptionsCompat options=ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity,top,floatbutton);
        ActivityCompat.startActivityForResult(mActivity,new Intent(mActivity, SkillTemplateActivity.class),ActivityProxyController.REQUEST_CREATE_SKILL,options.toBundle());
    }
}
