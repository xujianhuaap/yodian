package maimeng.yodian.app.client.android.view.skill;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.adapter.AbstractAdapter;
import maimeng.yodian.app.client.android.adapter.RmarkAdapter;
import maimeng.yodian.app.client.android.adapter.RmarkListAdapter;
import maimeng.yodian.app.client.android.common.model.Skill;
import maimeng.yodian.app.client.android.databinding.ActivitySkillPreviewBinding;
import maimeng.yodian.app.client.android.databinding.ViewHeaderPlaceholderBinding;
import maimeng.yodian.app.client.android.databinding.ViewHeaderPreviewDiaryBinding;
import maimeng.yodian.app.client.android.model.Rmark;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.response.RmarkListResponse;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.view.dialog.ShareDialog;
import maimeng.yodian.app.client.android.view.dialog.WaitDialog;
import maimeng.yodian.app.client.android.widget.EndlessRecyclerOnScrollListener;
import maimeng.yodian.app.client.android.widget.ListLayoutManager;

/**
 * Created by android on 15-8-6.
 */
public class SkillPreviewActivity extends AppCompatActivity implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String LOG_TAG =SkillPreviewActivity.class.getName() ;

    private int page=1;
    private boolean append;

    private Skill mSkill;
    private RmarkAdapter mAdapter;
    private CallBackProxy mCallBackProxy;
    private SkillService mSkillService;
    private ActivitySkillPreviewBinding mBinding;

    public static void show(Skill skill,Context context){
        Intent intent=new Intent();
        intent.putExtra("skill", skill);
        intent.setClass(context,SkillPreviewActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent=getIntent();
        mSkill= intent.getParcelableExtra("skill");

        ButterKnife.bind(this);
        mCallBackProxy = new CallBackProxy();
        mSkillService = Network.getService(SkillService.class);

        ListLayoutManager linearLayoutManager=new ListLayoutManager(this);
        EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener=new
                EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore() {
                page++;
                append=true;
                refresh(mSkill);
            }
        };

        ViewHolderClickListenerProxy viewHolderClickListenerProxy=new ViewHolderClickListenerProxy();
        mAdapter=new RmarkAdapter(this,mSkill,viewHolderClickListenerProxy);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_skill_preview);
        mBinding.setSkill(mSkill);
        mBinding.recDiary.setLayoutManager(linearLayoutManager);
        mBinding.recDiary.setHasFixedSize(true);
        mBinding.recDiary.addOnScrollListener(endlessRecyclerOnScrollListener);
        mBinding.recDiary.setAdapter(mAdapter);
        mBinding.fabGoback.setOnClickListener(this);
        mBinding.ivShare.setOnClickListener(this);
        mBinding.swipeLayout.setOnRefreshListener(this);

        refresh(mSkill);
    }



    @Override
     public void onRefresh() {
         page=1;
         append=false;
         refresh(mSkill);
     }

     private void refresh(Skill skill){

        mSkillService.rmark_list(skill.getId(), page, mCallBackProxy);
    }

    @Override
    public void onClick(View v) {
        if(v==mBinding.fabGoback){
            finish();
        }if(v==mBinding.ivShare){
            ShareDialog.ShareParams shareParams=new ShareDialog.ShareParams(mSkill,
                    mSkill.getQrcodeUrl(),mSkill.getUid(),mSkill.getNickname(),"");
            ShareDialog.show(this, shareParams);

        }
    }



    /***
     * 网络请求 返回数据
     *
     */

    private class CallBackProxy implements Callback<RmarkListResponse>{

        @Override
        public void end() {
            mBinding.swipeLayout.setRefreshing(false);
        }

        @Override
        public void failure(HNetError hNetError) {
            mBinding.swipeLayout.setRefreshing(false);
            ErrorUtils.checkError(SkillPreviewActivity.this,hNetError);
        }

        @Override
        public void start() {
            mBinding.swipeLayout.setRefreshing(true);
        }

        @Override
        public void success(RmarkListResponse rmarkListResponse, Response response) {
            if(rmarkListResponse.isSuccess()){
                List<Rmark>rmarks=rmarkListResponse.getData().getList();
                if(page==1&& rmarks.size()==0){
                    mBinding.recDiary.setVisibility(View.INVISIBLE);
                }
                mAdapter.reload(rmarks,append);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    /***
     *  RecyclerView 的点击事件
     */
    private class ViewHolderClickListenerProxy implements AbstractAdapter.ViewHolderClickListener<RmarkAdapter.ViewHolder>{

        @Override
        public void onItemClick(RmarkAdapter.ViewHolder holder, int postion) {

        }

        @Override
        public void onClick(RmarkAdapter.ViewHolder holder, View clickItem, int postion) {
            if(postion==0){

            }else{

                RmarkAdapter.NormalViewHolder normalViewHolder=(RmarkAdapter.NormalViewHolder)holder;
                if(normalViewHolder.binding.btnMenuDelete==clickItem){
                    mSkillService.delete_rmark(normalViewHolder.binding.getRmark().getId(),new ToastCallback(SkillPreviewActivity.this));
                }else if(normalViewHolder.binding.btnMenuReport==clickItem){

                }
            }

        }
    }



}
