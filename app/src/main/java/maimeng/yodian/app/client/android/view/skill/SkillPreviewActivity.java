package maimeng.yodian.app.client.android.view.skill;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.adapter.AbstractHeaderAdapter;
import maimeng.yodian.app.client.android.adapter.RmarkAdapter;
import maimeng.yodian.app.client.android.constants.ApiConfig;
import maimeng.yodian.app.client.android.databinding.ActivitySkillPreviewBinding;
import maimeng.yodian.app.client.android.model.Rmark;
import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.TypedBitmap;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.loader.ImageLoaderManager;
import maimeng.yodian.app.client.android.network.response.RmarkListResponse;
import maimeng.yodian.app.client.android.network.response.SkillAllResponse;
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

    private static final String LOG_TAG = SkillPreviewActivity.class.getName();
    private static final int REQUEST_AUTH = 0x231;

    private int page = 1;
    private boolean append;
    private boolean isEnd;
    private int mEditStatus;

    private Skill mSkill;
    private Bitmap mBitmap;
    private RmarkAdapter mAdapter;
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
        intent.putExtra("skill", skill);
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

        Intent intent = getIntent();
        mSkill = intent.getParcelableExtra("skill");
        mEditStatus = intent.getIntExtra("editstatus", 0);
        ButterKnife.bind(this);
        mCallBackProxy = new CallBackProxy();
        mSkillService = Network.getService(SkillService.class);

        ListLayoutManager linearLayoutManager = new ListLayoutManager(this);
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

        ViewHolderClickListenerProxy viewHolderClickListenerProxy = new ViewHolderClickListenerProxy();
        mAdapter = new RmarkAdapter(this, mSkill, viewHolderClickListenerProxy);

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

        if (mEditStatus > 0) {
            mBinding.btnDone.setVisibility(View.VISIBLE);
        } else {
            mBinding.btnDone.setVisibility(View.INVISIBLE);
        }

        refresh(mSkill);
        new ImageLoaderManager.Loader(this, Uri.parse(mSkill.getPic())).callback(new ImageLoaderManager.Callback() {
            @Override
            public void onImageLoaded(Bitmap bitmap) {
                mBitmap = Bitmap.createScaledBitmap(bitmap, 720, 720, false);
            }

            @Override
            public void onLoadEnd() {

            }

            @Override
            public void onLoadFaild() {

            }
        }).width(240).height(240).start();

    }


    @Override
    public void onRefresh() {
        page = 1;
        append = false;
        mBinding.swipeLayout.setRefreshing(true);
        refresh(mSkill);
    }

    private void refresh(Skill skill) {

        mSkillService.rmark_list(skill.getId(), page, mCallBackProxy);

    }

    @Override
    public void onClick(View v) {
        if (v == mBinding.fabGoback) {
            finish();
        }
        if (v == mBinding.ivShare) {
            if (mShareDialog != null) {
                mShareDialog.dismiss();
                mShareDialog = null;
            }
            if (mShareDialog == null) {
                ShareDialog.ShareParams shareParams = new ShareDialog.ShareParams(mSkill,
                        mSkill.getQrcodeUrl(), mSkill.getUid(), mSkill.getNickname(), "");
                mShareDialog = ShareDialog.show(this, shareParams, 1);
            }

        } else if (mBinding.btnDone == v) {
            submitSkill();

        }
    }

    /***
     *
     */
    private void submitSkill() {
        if (mEditStatus == 1) {
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
            if (mBitmap != null) {
                mSkillService.add(mSkill.getName(), mSkill.getContent(), new TypedBitmap.Builder(mBitmap).setMaxSize(300).setAutoMatch(getResources()).build(), mSkill.getPrice(), mSkill.getUnit(), new Callback<SkillAllResponse>() {
                    @Override
                    public void success(SkillAllResponse res, Response response) {
                        if (res.isSuccess()) {
                            setResult(RESULT_OK);
                            String qurode = mSkill.getQrcodeUrl();
                            if (qurode.equals("")) mSkill.setQrcodeUrl(ApiConfig.Api.QRODE_URL);
                            ShareDialog.ShareParams params = new ShareDialog.ShareParams(mSkill, mSkill.getQrcodeUrl(),
                                    mSkill.getId(), mSkill.getNickname(), "");
                            ShareDialog.show(SkillPreviewActivity.this, params, true, 1);

                        } else if (res.isValidateAuth(SkillPreviewActivity.this, REQUEST_AUTH)) ;
                    }

                    @Override
                    public void failure(HNetError hNetError) {
                        ErrorUtils.checkError(SkillPreviewActivity.this, hNetError);
                    }

                    @Override
                    public void start() {
                        dialog = WaitDialog.show(SkillPreviewActivity.this);

                    }

                    @Override
                    public void end() {
                        if (dialog != null) dialog.dismiss();
                    }
                });
            } else {

            }

        }
    }


    /***
     * 网络请求 返回数据
     */

    private class CallBackProxy implements Callback<RmarkListResponse> {

        @Override
        public void end() {
            isEnd = false;
            mBinding.swipeLayout.setRefreshing(false);
        }

        @Override
        public void failure(HNetError hNetError) {
            mBinding.swipeLayout.setRefreshing(false);
            ErrorUtils.checkError(SkillPreviewActivity.this, hNetError);
        }

        @Override
        public void start() {

        }

        @Override
        public void success(RmarkListResponse rmarkListResponse, Response response) {
            if (rmarkListResponse.isSuccess()) {
                List<Rmark> rmarks = rmarkListResponse.getData().getList();
                if (rmarks.size() == 0 && isEnd && toast == null) {
                    toast = Toast.makeText(SkillPreviewActivity.this, "已经到底部", Toast.LENGTH_SHORT);
                    toast.show();
                }
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        toast = null;
                    }
                }, 2000);
                mAdapter.reload(rmarks, append);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    /***
     * RecyclerView 的点击事件
     */
    private class ViewHolderClickListenerProxy implements AbstractHeaderAdapter.ViewHolderClickListener<RmarkAdapter.ViewHolder> {

        @Override
        public void onItemClick(RmarkAdapter.ViewHolder holder, int postion) {

        }

        @Override
        public void onClick(RmarkAdapter.ViewHolder holder, View clickItem, int postion) {
            if (postion == 0) {

            } else {

//                RmarkAdapter.NormalViewHolder normalViewHolder = (RmarkAdapter.NormalViewHolder) holder;
//                if (normalViewHolder.binding.btnMenuDelete == clickItem) {
//                    mSkillService.delete_rmark(normalViewHolder.binding.getRmark().getId(), new ToastCallback(SkillPreviewActivity.this));
//                } else if (normalViewHolder.binding.btnMenuReport == clickItem) {
//
//                }
            }

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_AUTH) {
                submitSkill();
            }
        }
    }
}
