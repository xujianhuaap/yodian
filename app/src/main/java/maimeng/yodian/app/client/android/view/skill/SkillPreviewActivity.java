package maimeng.yodian.app.client.android.view.skill;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.squareup.picasso.Picasso;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import java.util.List;

import butterknife.ButterKnife;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.adapter.AbstractHeaderAdapter;
import maimeng.yodian.app.client.android.adapter.RmarkAdapter;
import maimeng.yodian.app.client.android.common.model.Skill;
import maimeng.yodian.app.client.android.constants.ApiConfig;
import maimeng.yodian.app.client.android.databinding.ActivitySkillPreviewBinding;
import maimeng.yodian.app.client.android.model.Rmark;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.TypedBitmap;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.response.RmarkListResponse;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.service.SkillService;
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
    private static final int REQUEST_AUTH = 0x231;

    private int page=1;
    private boolean append;
    private int mEditStatus;

    private Skill mSkill;
    private Bitmap mBitmap;
    private RmarkAdapter mAdapter;
    private CallBackProxy mCallBackProxy;
    private SkillService mSkillService;
    private ActivitySkillPreviewBinding mBinding;
    private WaitDialog dialog;
    private ShareDialog mShareDialog;

    /***
     *
     * @param skill
     * @param context
     * @param editStatus 0 预览　１更新　２　增加
     * @param requestCode
     */

    public static void show(Skill skill,Activity context,int editStatus,int requestCode){
        Intent intent=new Intent();
        intent.putExtra("skill", skill);
        intent.putExtra("editstatus",editStatus);
        intent.setClass(context,SkillPreviewActivity.class);
        if(requestCode==0){
            context.startActivity(intent);
        }else{
            context.startActivityForResult(intent,requestCode);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent=getIntent();
        mSkill= intent.getParcelableExtra("skill");
        mEditStatus=intent.getIntExtra("editstatus",0);
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
        mBinding.btnDone.setOnClickListener(this);
        mBinding.swipeLayout.setOnRefreshListener(this);

        if(mEditStatus>0){
            mBinding.btnDone.setVisibility(View.VISIBLE);
        }else{
            mBinding.btnDone.setVisibility(View.INVISIBLE);
        }

        refresh(mSkill);
        Network.image(this, mSkill.getPic(), new TargetProxy());
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
            mShareDialog.dismiss();
            if(mShareDialog==null){
                ShareDialog.ShareParams shareParams=new ShareDialog.ShareParams(mSkill,
                        mSkill.getQrcodeUrl(),mSkill.getUid(),mSkill.getNickname(),"");
                mShareDialog = ShareDialog.show(this, shareParams);
            }

        }else if(mBinding.btnDone==v){
            submitSkill();

        }
    }

    /***
     *
     */
    private void submitSkill() {
        if (mEditStatus==1) {
        mSkillService.update(mSkill.getId(), mSkill.getName(), mSkill.getContent(), new TypedBitmap.Builder(mBitmap).setMaxSize(300).setAutoMatch(getResources()).build(), mSkill.getPrice(), mSkill.getUnit(), new ToastCallback(this) {
            @Override
            public void success(ToastResponse res, Response response) {
                super.success(res, response);
                if (res.isSuccess()) {
                    Skill skill = getIntent().getParcelableExtra("skill");
                    skill.setPic(mSkill.getPic());
                    skill.setUnit(mSkill.getUnit());
                    skill.setPrice(mSkill.getPrice());
                    skill.setName(mSkill.getName());
                    skill.setContent(mSkill.getContent());
                    skill.setCreatetime(mSkill.getCreatetime());
                    skill.setStatus(mSkill.getStatus());
                    Intent data = new Intent();
                    data.putExtra("skill", skill);
                    setResult(RESULT_OK, data);
                    finish();
                } else if (res.isValidateAuth(SkillPreviewActivity.this, REQUEST_AUTH)) ;
            }

            @Override
            public void start() {
                super.start();
                dialog = WaitDialog.show(SkillPreviewActivity.this);

            }

            @Override
            public void end() {
                super.end();
                if (dialog != null) dialog.dismiss();
            }
        });
    } else {
        mSkillService.add(mSkill.getName(), mSkill.getContent(), new TypedBitmap.Builder(mBitmap).setMaxSize(300).setAutoMatch(getResources()).build(), mSkill.getPrice(), mSkill.getUnit(), new ToastCallback(this) {
            @Override
            public void success(ToastResponse res, Response response) {
                super.success(res, response);
                if (res.isSuccess()) {
                    setResult(RESULT_OK);
                    String qurode=mSkill.getQrcodeUrl();
                    if(qurode.equals(""))  mSkill.setQrcodeUrl(ApiConfig.Api.QRODE_URL);
                    ShareDialog.ShareParams params=new ShareDialog.ShareParams(mSkill,mSkill.getQrcodeUrl(),
                            mSkill.getId(),mSkill.getNickname(),"");
                    ShareDialog.show(SkillPreviewActivity.this, params,true);

                } else if (res.isValidateAuth(SkillPreviewActivity.this, REQUEST_AUTH)) ;
            }

            @Override
            public void start() {
                super.start();
                dialog = WaitDialog.show(SkillPreviewActivity.this);

            }

            @Override
            public void end() {
                super.end();
                if (dialog != null) dialog.dismiss();
            }
        });
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
                mAdapter.reload(rmarks,append);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    /***
     *  RecyclerView 的点击事件
     */
    private class ViewHolderClickListenerProxy implements AbstractHeaderAdapter.ViewHolderClickListener<RmarkAdapter.ViewHolder>{

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

    private class TargetProxy implements com.squareup.picasso.Target{
        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mBitmap=bitmap;
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==REQUEST_AUTH){
                submitSkill();
            }
        }
    }
}
