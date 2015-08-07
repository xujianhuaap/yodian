package maimeng.yodian.app.client.android.view.skill;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
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
import maimeng.yodian.app.client.android.view.dialog.WaitDialog;
import maimeng.yodian.app.client.android.widget.EndlessRecyclerOnScrollListener;
import maimeng.yodian.app.client.android.widget.ListLayoutManager;

/**
 * Created by android on 15-8-6.
 */
public class SkillPreviewActivity extends AppCompatActivity implements View.OnClickListener,
        Callback<RmarkListResponse>,AbstractAdapter.ViewHolderClickListener<RmarkAdapter.ViewHolder> {

    private static final String LOG_TAG =SkillPreviewActivity.class.getName() ;

    private ActivitySkillPreviewBinding mBinding;
    private int page=1;
    private SkillService mSkillService;
    private WaitDialog mWaitDialog;
    private Skill mSkill;

    private RmarkAdapter mAdapter;

    public static void show(Skill skill,Context context){
        Intent intent=new Intent();
        intent.putExtra("skill", skill);
        intent.setClass(context,SkillPreviewActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int width=getResources().getDisplayMetrics().widthPixels;
        Intent intent=getIntent();
         mSkill= intent.getParcelableExtra("skill");
        Spanned  priceText= Html.fromHtml(getResources().getString(R.string.lable_price, mSkill.getPrice(), mSkill.getUnit()));

        ButterKnife.bind(this);
        mSkillService = Network.getService(SkillService.class);

        ListLayoutManager linearLayoutManager=new ListLayoutManager(this);
        EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener=new
                EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore() {

            }
        };
        mAdapter=new RmarkAdapter(this,this);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_skill_preview);
//        ViewHeaderPreviewDiaryBinding headerPreviewDiaryBinding=DataBindingUtil.inflate(
//                getLayoutInflater(),R.layout.view_header_preview_diary,mBinding.recDiary,false);
//
//        headerPreviewDiaryBinding.setSkill(mSkill);
//        headerPreviewDiaryBinding.price.setText(priceText);
//        headerPreviewDiaryBinding.pic.setLayoutParams(new RelativeLayout.LayoutParams(width, width * 3 / 4));

        mBinding.setSkill(mSkill);
        mBinding.recDiary.setLayoutManager(linearLayoutManager);
        mBinding.recDiary.setHasFixedSize(true);
        mBinding.recDiary.addOnScrollListener(endlessRecyclerOnScrollListener);
        mBinding.recDiary.setAdapter(mAdapter);

        refresh(mSkill);
    }

    private void refresh(Skill skill){
        mSkillService.rmark_list(skill.getId(), page, this);
    }

    @Override
    public void onClick(View v) {

    }



    @Override
    public void end() {
        if(mWaitDialog!=null){
            mWaitDialog.dismiss();
        }
    }

    @Override
    public void failure(HNetError hNetError) {
        ErrorUtils.checkError(this,hNetError);
    }

    @Override
    public void start() {
        mWaitDialog = WaitDialog.show(this);
    }

    @Override
    public void success(RmarkListResponse rmarkListResponse, Response response) {
        if(rmarkListResponse.isSuccess()){
            List<Rmark>rmarks=rmarkListResponse.getData().getList();
            mAdapter.reload(rmarks,true);
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onItemClick(RmarkAdapter.ViewHolder holder, int postion) {

    }

    @Override
    public void onClick(RmarkAdapter.ViewHolder holder, View clickItem, int postion) {
        //


            mSkillService.delete_rmark(mSkill.getId(),new ToastCallback(this));

    }


}
