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
import android.widget.ListView;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.adapter.RmarkListAdapter;
import maimeng.yodian.app.client.android.common.model.Skill;
import maimeng.yodian.app.client.android.databinding.ActivitySkillPreviewBinding;
import maimeng.yodian.app.client.android.databinding.ViewHeaderPlaceholderBinding;
import maimeng.yodian.app.client.android.model.Rmark;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.RmarkListResponse;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.view.dialog.WaitDialog;

/**
 * Created by android on 15-8-6.
 */
public class SkillPreviewActivity extends AppCompatActivity implements View.OnClickListener,
        Callback<RmarkListResponse>,RmarkListAdapter.ActionListener {

    private static final String LOG_TAG =SkillPreviewActivity.class.getName() ;

    private ActivitySkillPreviewBinding mBinding;
    private int page=1;
    private SkillService mSkillService;
    private WaitDialog mWaitDialog;


    private RmarkListAdapter mAdapter;

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
        Skill skill=intent.getParcelableExtra("skill");
        Spanned  priceText= Html.fromHtml(getResources().getString(R.string.lable_price, skill.getPrice(), skill.getUnit()));

        ButterKnife.bind(this);
        mSkillService = Network.getService(SkillService.class);

        mAdapter = new RmarkListAdapter(this,this);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_skill_preview);
        mBinding.price.setText(priceText);
        mBinding.setSkill(skill);
        mBinding.recDiary.setAdapter(mAdapter);

        refresh(skill);
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
    public void onDelete(RmarkListAdapter.ViewHolder holder) {

    }

    @Override
    public void onReport(RmarkListAdapter.ViewHolder holder) {

    }
}
