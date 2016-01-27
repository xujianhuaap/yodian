package maimeng.yodian.app.client.android.view.skill;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;
import org.parceler.Parcels;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.adapter.AbstractHeaderAdapter;
import maimeng.yodian.app.client.android.adapter.RmarkReviewListAdapter;
import maimeng.yodian.app.client.android.common.PullHeadView;
import maimeng.yodian.app.client.android.common.UEvent;
import maimeng.yodian.app.client.android.databinding.ActivitySkillPreviewBinding;
import maimeng.yodian.app.client.android.model.Rmark;
import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.RmarkListResponse;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.view.common.AbstractActivity;
import maimeng.yodian.app.client.android.view.dialog.ShareDialog;
import maimeng.yodian.app.client.android.view.dialog.WaitDialog;
import maimeng.yodian.app.client.android.widget.EndlessRecyclerOnScrollListener;
import maimeng.yodian.app.client.android.widget.ListLayoutManager;

/**
 * Created by android on 15-8-6.
 */
public class SkillPreviewActivity extends AbstractActivity implements
        PtrHandler {

    private static final String LOG_TAG = SkillPreviewActivity.class.getName();
    private static final int REQUEST_AUTH = 0x231;

    private int page = 1;
    private boolean append;
    private boolean isEnd;
    private int mEditStatus;

    private Skill mSkill;
    private RmarkReviewListAdapter mAdapter;
    private CallBackProxy mCallBackProxy;
    private SkillService mSkillService;
    private ActivitySkillPreviewBinding mBinding;
    private WaitDialog dialog;
    private ShareDialog mShareDialog;
    private Toast toast;

    /***
     * @param skill
     * @param context
     * @param editStatus  0 预览　１更新　２　增加
     * @param requestCode
     */

    public static void show(Skill skill, Activity context, int editStatus, int requestCode) {
        Intent intent = new Intent();
        intent.putExtra("skill", Parcels.wrap(skill));
        intent.putExtra("editstatus", editStatus);
        intent.setClass(context, SkillPreviewActivity.class);
        if (requestCode == 0) {
            context.startActivity(intent);
        } else {
            context.startActivityForResult(intent, requestCode);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobclickAgent.onEvent(this, UEvent.SKILL_PREVIEW);
        Intent intent = getIntent();
        mSkill = Parcels.unwrap(intent.getParcelableExtra("skill"));
        mEditStatus = intent.getIntExtra("editstatus", 0);
        mCallBackProxy = new CallBackProxy();
        mSkillService = Network.getService(SkillService.class);


        ViewHolderClickListenerProxy viewHolderClickListenerProxy = new ViewHolderClickListenerProxy();
        mAdapter = new RmarkReviewListAdapter(this, mSkill, viewHolderClickListenerProxy);

        mBinding = bindView(R.layout.activity_skill_preview);
        mBinding.setSkill(mSkill);

        mBinding.swipeLayout.setPtrHandler(this);
        StoreHouseHeader header = PullHeadView.create(this);
        header.setTextColor(0x000000);
        mBinding.swipeLayout.addPtrUIHandler(header);
        mBinding.swipeLayout.setHeaderView(header);
        ListLayoutManager linearLayoutManager = new ListLayoutManager(this);
        mBinding.recDiary.setLayoutManager(linearLayoutManager);
        EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener = new
                EndlessRecyclerOnScrollListener(linearLayoutManager) {
                    @Override
                    public void onLoadMore() {
                        page++;
                        append = true;
                        isEnd = true;
                        refresh(mSkill);
                    }
                };
        mBinding.recDiary.addOnScrollListener(endlessRecyclerOnScrollListener);
        mBinding.recDiary.setAdapter(mAdapter);

        refresh(mSkill);
    }



    private void refresh(Skill skill) {

        mSkillService.rmark_list(skill.getId(), page, mCallBackProxy);

    }


    /***
     * 网络请求 返回数据
     */

    private class CallBackProxy implements Callback<RmarkListResponse> {

        @Override
        public void end() {
            isEnd = false;
            mBinding.swipeLayout.refreshComplete();
        }

        @Override
        public void failure(HNetError hNetError) {
            ErrorUtils.checkError(SkillPreviewActivity.this, hNetError);
        }

        @Override
        public void start() {

        }

        @Override
        public void success(RmarkListResponse rmarkListResponse, Response response) {
            if (rmarkListResponse.isSuccess()) {
                List<Rmark> rmarks = rmarkListResponse.getData().getList();
                mAdapter.reload(rmarks, append);
                mAdapter.notifyDataSetChanged();
                if (rmarks.size() == 0 && isEnd&&append) {
                    toast = Toast.makeText(SkillPreviewActivity.this, "已经到底部", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }
    }

    /***
     * RecyclerView 的点击事件
     */
    private class ViewHolderClickListenerProxy implements AbstractHeaderAdapter.ViewHolderClickListener<RmarkReviewListAdapter.ViewHolder> {

        @Override
        public void onItemClick(RmarkReviewListAdapter.ViewHolder holder, int postion) {

        }

        @Override
        public void onClick(RmarkReviewListAdapter.ViewHolder holder, View clickItem, int postion) {
            if (postion == 0) {

            } else {

//                RmarkReviewListAdapter.NormalViewHolder normalViewHolder = (RmarkReviewListAdapter.NormalViewHolder) holder;
//                if (normalViewHolder.binding.btnMenuDelete == clickItem) {
//                    mSkillService.delete_rmark(normalViewHolder.binding.getRmark().getId(), new ToastCallback(SkillPreviewActivity.this));
//                } else if (normalViewHolder.binding.btnMenuReport == clickItem) {
//
//                }
            }

        }
    }


    @Override
    protected void initToolBar(Toolbar toolbar) {
        super.initToolBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_go_back);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }
        mTitle.setTextColor(Color.WHITE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_skill_preview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.menu_share) {
            if (mShareDialog != null) {
                mShareDialog.dismiss();
                mShareDialog = null;
            }
            if (mShareDialog == null) {
                ShareDialog.ShareParams shareParams = new ShareDialog.ShareParams(mSkill,
                        mSkill.getQrcodeUrl(), mSkill.getUid(), mSkill.getNickname(), "");
                mShareDialog = ShareDialog.show(this, shareParams, 1);
            }

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        page=1;
        append=false;
        refresh(mSkill);
    }
}
