package maimeng.yodian.app.client.android.view.proxy;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import maimeng.yodian.app.client.android.MainActivity;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.adapter.AbstractAdapter;
import maimeng.yodian.app.client.android.adapter.MainHomeAdapter;
import maimeng.yodian.app.client.android.common.UserAuth;
import maimeng.yodian.app.client.android.model.Skill;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.SkillResponse;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.widget.EndlessRecyclerOnScrollListener;
import maimeng.yodian.app.client.android.widget.RoundImageView;

/**
 * Created by android on 15-7-13.
 */
public class MainHomeProxy implements ActivityProxy,AbstractAdapter.ViewHolderClickListener<MainHomeAdapter.ViewHolder>, PtrHandler, Callback<SkillResponse>, AppBarLayout.OnOffsetChangedListener {
    private final CoordinatorLayout mView;
    private final MainActivity mActivity;
    private final RecyclerView mRecyclerView;
    private final PtrFrameLayout mRefreshLayout;
    private final SkillService service;
    private final AppBarLayout appBar;
    private final RoundImageView mUserAvatar;
    private final TextView mUserNickname;
    private boolean inited=false;
    private final MainHomeAdapter adapter;
    private final EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    private UserAuth user;
    private int page=1;
    public MainHomeProxy(MainActivity activity, View view){
        this.mView=(CoordinatorLayout)view;
        this.mActivity=activity;
        service=Network.getService(SkillService.class);
        appBar=(AppBarLayout)view.findViewById(R.id.appBarLayout);
        mRefreshLayout=(PtrFrameLayout)view.findViewById(R.id.refresh_layout);
        mRecyclerView=(RecyclerView)view.findViewById(R.id.recyclerView);
        mUserAvatar=(RoundImageView)view.findViewById(R.id.user_avatar);
        mUserNickname=(TextView)view.findViewById(R.id.user_nickname);
        view.findViewById(R.id.user_nickname);
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
                refresh();
            }
        };
        mRecyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
        adapter=new MainHomeAdapter(mActivity,this);
        mRecyclerView.setAdapter(adapter);
    }

    private void refresh() {
        service.list(user.uid, page, this);
    }

    public boolean isInited(){
        return this.inited;
    }
    @Override
    public void init() {
        inited=true;
        user=UserAuth.read(this.mActivity);
        initUsrInfo();
        initSkillInfo();


    }

    private void initSkillInfo() {
        page=1;
        mRefreshLayout.autoRefresh();
    }

    private void initUsrInfo() {
        mUserNickname.setText(user.nickname);
        Network.image(user.img,mUserAvatar);
    }

    @Override
    public void onTitleChanged(CharSequence title, int color) {
    }

    @Override
    public void show(FloatingActionButton button) {

    }

    @Override
    public void hide(FloatingActionButton button) {

    }

    @Override
    public boolean isShow() {
        return false;
    }

    @Override
    public void onItemClick(MainHomeAdapter.ViewHolder holder, int postion) {

    }

    @Override
    public void onClick(MainHomeAdapter.ViewHolder holder, View clickItem, int postion) {

    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View content, View header) {
        // 默认实现，根据实际情况做改动
        return PtrDefaultHandler.checkContentCanBePulledDown(ptrFrameLayout, content, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
        page=1;
        refresh();
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
}
