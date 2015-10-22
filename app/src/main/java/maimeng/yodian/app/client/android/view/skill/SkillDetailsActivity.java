package maimeng.yodian.app.client.android.view.skill;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.easemob.applib.controller.HXSDKHelper;
import com.melnykov.fab.ScrollDirectionListener;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import java.util.Date;
import java.util.List;
import java.util.Map;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import in.srain.cube.views.ptr.indicator.PtrIndicator;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.adapter.AbstractHeaderAdapter;
import maimeng.yodian.app.client.android.adapter.RmarkListAdapter;
import maimeng.yodian.app.client.android.chat.DemoHXSDKHelper;
import maimeng.yodian.app.client.android.chat.activity.ChatActivity;
import maimeng.yodian.app.client.android.chat.db.UserDao;
import maimeng.yodian.app.client.android.chat.domain.RobotUser;
import maimeng.yodian.app.client.android.common.PullHeadView;
import maimeng.yodian.app.client.android.databinding.ActivitySkillDetailsBinding;
import maimeng.yodian.app.client.android.databinding.ViewHeaderPlaceholderBinding;
import maimeng.yodian.app.client.android.databings.ImageBindable;
import maimeng.yodian.app.client.android.model.Rmark;
import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.loader.ImageLoaderManager;
import maimeng.yodian.app.client.android.network.response.RmarkListResponse;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.service.CommonService;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.view.AbstractActivity;
import maimeng.yodian.app.client.android.view.deal.PayWrapperActivity;
import maimeng.yodian.app.client.android.view.dialog.AlertDialog;
import maimeng.yodian.app.client.android.view.dialog.ShareDialog;
import maimeng.yodian.app.client.android.view.dialog.WaitDialog;
import maimeng.yodian.app.client.android.view.user.UserHomeActivity;

/**
 * Created by android on 2015/7/22.
 */
public class SkillDetailsActivity extends AbstractActivity implements PtrHandler, AppBarLayout.OnOffsetChangedListener, Callback<RmarkListResponse>, AbstractHeaderAdapter.ViewHolderClickListener<RmarkListAdapter.ViewHolder>, View.OnClickListener, RmarkListAdapter.ActionListener {
    private static final String LOG_TAG = SkillDetailsActivity.class.getName();
    private static final int REQEUST_RMARK_ADD = 0x1001;
    private static final int REQEUST_RMARK_AUTH = 0x1002;
    private SkillService service;
    private long sid;
    private int page = 1;
    private RmarkListAdapter adapter;
    private Interpolator mSmoothInterpolator;
    private int mHeaderHeight;
    private int mActionBarHeight;
    private int mMinHeaderTranslation;
    private View mPlaceHolderView;
    private ViewHeaderPlaceholderBinding headBinding;
    private ActivitySkillDetailsBinding binding;
    private Skill skill;
    private User user;
    private boolean isMe;
    private WaitDialog dialog;
    private FrameLayout noSkillRmark;
    private Bitmap defaultAvatar;

    public int getTitleBarHeight() {
        if (mActionBarHeight != 0) {
            return mActionBarHeight;
        }
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            mActionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return mActionBarHeight;
    }

    private boolean inited = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (defaultAvatar != null && !defaultAvatar.isRecycled()) {
            defaultAvatar.recycle();
            defaultAvatar = null;
            System.gc();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = User.read(this);
        noSkillRmark = new FrameLayout(this);
        noSkillRmark.setPadding(0, 50, 0, 0);
        final ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.pic_no_skill_rmark);
        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        noSkillRmark.addView(iv, params);
        service = Network.getService(SkillService.class);
//        mSmoothInterpolator = new AccelerateDecelerateInterpolator();
        mSmoothInterpolator = new LinearInterpolator();
        mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.skill_detail_head_height);
        mMinHeaderTranslation = -mHeaderHeight + getTitleBarHeight();
        binding = DataBindingUtil.inflate(getLayoutInflater(),R.layout.activity_skill_details,null,false);
        setContentView(binding.getRoot());
        headBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.view_header_placeholder, binding.recyclerView, false);
        ViewCompat.setTransitionName(headBinding.btnBuySkill, "back");
        headBinding.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialog.show(SkillDetailsActivity.this, new ShareDialog.ShareParams(skill, skill.getQrcodeUrl(), skill.getUid(), skill.getNickname(), ""), 1);
            }
        });
        headBinding.userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMe) {
                    UserHomeActivity.show(SkillDetailsActivity.this, skill.getUid(), defaultAvatar, skill.getNickname(), headBinding.btnBuySkill, null,
                            headBinding.userNickname);
                }

            }
        });
        headBinding.btnContact.setOnClickListener(this);
        mPlaceHolderView = headBinding.getRoot();
        binding.recyclerView.addHeaderView(mPlaceHolderView);
        adapter = new RmarkListAdapter(this, this);
        binding.refreshLayout.setPtrHandler(this);
        StoreHouseHeader header = PullHeadView.create(this);
        binding.refreshLayout.addPtrUIHandler(header);
        binding.refreshLayout.setHeaderView(header);
        binding.refreshLayout.addPtrUIHandler(new PtrUIHandler() {
            @Override
            public void onUIReset(PtrFrameLayout ptrFrameLayout) {
                LogUtil.i("mRefreshLayout", "onUIReset");
            }

            @Override
            public void onUIRefreshPrepare(PtrFrameLayout ptrFrameLayout) {
                LogUtil.i("mRefreshLayout", "onUIRefreshPrepare");
            }

            @Override
            public void onUIRefreshBegin(PtrFrameLayout ptrFrameLayout) {
                LogUtil.i("mRefreshLayout", "onUIRefreshBegin");
            }

            @Override
            public void onUIRefreshComplete(PtrFrameLayout ptrFrameLayout) {
                LogUtil.i("mRefreshLayout", "onUIRefreshComplete");
            }

            @Override
            public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
                float currentPercent = Math.min(1.0F, ptrIndicator.getCurrentPercent());
                int posY = ptrIndicator.getCurrentPosY();
                LogUtil.i("mRefreshLayout", "posY:%d", posY);
            }
        });
        binding.recyclerView.setAdapter(adapter);
        if (getIntent().hasExtra("skill")) {
            Skill skill = getIntent().getParcelableExtra("skill");
            ImageBindable imageBindable = skill.getAvatar80();
            if (imageBindable != null && imageBindable.getUri() != null) {
                new ImageLoaderManager.Loader(SkillDetailsActivity.this, imageBindable.getUri()).width(80).height(80).callback(new ImageLoaderManager.Callback() {
                    @Override
                    public void onImageLoaded(Bitmap bitmap) {
                        SkillDetailsActivity.this.defaultAvatar = bitmap;
                    }

                    @Override
                    public void onLoadEnd() {

                    }

                    @Override
                    public void onLoadFaild() {

                    }
                }).start(this);
            } else {
                if (skill.getAvatar() != null) {
                    SkillDetailsActivity.this.defaultAvatar = ImageLoaderManager.image(SkillDetailsActivity.this, Uri.parse(skill.getAvatar()));
                }

            }

            sid = skill.getId();
            isMe = skill.getUid() == user.getUid();
            if (isMe) {
                setUIWhenIsMe();
            }
            binding.refreshLayout.autoRefresh();
            if (android.os.Build.VERSION.SDK_INT >= 21) {
//                setStatuBarColor();
            }
        } else {
            sid = getIntent().getLongExtra("sid", 0);
            binding.refreshLayout.autoRefresh();
        }

        headBinding.btnBuySkill.setOnClickListener(this);
    }

    /***
     *
     */
    private void setUIWhenIsMe() {
        headBinding.btnContact.setVisibility(View.GONE);
        headBinding.divinder.setVisibility(View.GONE);
        headBinding.btnBuySkill.setCompoundDrawables(null,null,getResources().getDrawable(R.drawable.btn_ic_add),null);
        headBinding.btnBuySkill.setText("添加日记");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQEUST_RMARK_ADD || requestCode == REQEUST_RMARK_ADD) && resultCode == RESULT_OK) {
            binding.refreshLayout.autoRefresh();
        }
    }



    private RectF getOnScreenRect(RectF rect, View view) {
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        return rect;
    }



    public float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }

    public int getScrollY() {
        View c = binding.recyclerView.getChildAt(0);
        if (c == null) {
            return 0;
        }

        int firstVisiblePosition = binding.recyclerView.getFirstVisiblePosition();
        int top = c.getTop();

        int headerHeight = 0;
        if (firstVisiblePosition >= 1) {
            headerHeight = mPlaceHolderView.getHeight();
        }

        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {

    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View content, View header) {
        // 默认实现，根据实际情况做改动
        return PtrDefaultHandler.checkContentCanBePulledDown(ptrFrameLayout, content, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
        page = 1;
        sync();
    }

    private void sync() {
        service.rmark_list(sid, page, this);
    }

    @Override
    public void start() {
        dialog = WaitDialog.show(this);
    }

    @Override
    public void success(RmarkListResponse res, Response response) {
        if (res.isSuccess()) {
            List<Rmark> list = res.getData().getList();

            if (list.size() > 0) {
                Rmark rmark = list.get(0);
                Date date = rmark.getCreatetime();
                LogUtil.d("SkillDetailsActivity", "TT" + date);

                LogUtil.d("SkillDetailsActivity", "TRTTT" + date.getTime());
            }

            if (skill == null) {
                headBinding.headerLogoBg.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        inited = true;
                    }
                }, 1000);
                skill = res.getData().getDetail();
                isMe = skill.getUid() == user.getUid();
                if (isMe) {
                   setUIWhenIsMe();
                }
                headBinding.setSkill(skill);
                if (skill.getStatus() != 0) {
                    headBinding.skillStatus.setVisibility(View.VISIBLE);
                } else {
                    headBinding.skillStatus.setVisibility(View.INVISIBLE);
                }
                binding.setSkill(skill);
                new ImageLoaderManager.Loader(SkillDetailsActivity.this, skill.getAvatar80().getUri()).width(80).height(80).callback(new ImageLoaderManager.Callback() {
                    @Override
                    public void onImageLoaded(Bitmap bitmap) {
                        SkillDetailsActivity.this.defaultAvatar = bitmap;
                    }

                    @Override
                    public void onLoadEnd() {

                    }

                    @Override
                    public void onLoadFaild() {

                    }
                }).start(this);
                Spanned text = Html.fromHtml(getResources().getString(R.string.lable_price, skill.getPrice(), skill.getUnit()));
                headBinding.price.setText(text);
                headBinding.price.setText(text);
                if (android.os.Build.VERSION.SDK_INT >= 21) {
//                    setStatuBarColor();
                }
            }
            if (isMe) {
                binding.recyclerView.removeHeaderView(noSkillRmark);
                if (list.size() > 0 || page > 1) {
                } else {
                    ////pic_no_skill_rmark
                    if (isMe) {
                        binding.recyclerView.addHeaderView(noSkillRmark);
                    }
                }
            }
            adapter.reload(list, page != 1);
            adapter.notifyDataSetChanged();
        } else {
            res.isValidateAuth(this, REQEUST_RMARK_AUTH);
            res.showMessage(this);
        }
    }

    @Override
    public void failure(HNetError hNetError) {

    }

    @Override
    public void end() {
        dialog.dismiss();
        binding.refreshLayout.refreshComplete();

    }

    private int colorBurn(int RGBValues) {
        int alpha = RGBValues >> 24;
        int red = RGBValues >> 16 & 0xFF;
        int green = RGBValues >> 8 & 0xFF;
        int blue = RGBValues & 0xFF;
        red = (int) Math.floor(red * (1 - 0.1));
        green = (int) Math.floor(green * (1 - 0.1));
        blue = (int) Math.floor(blue * (1 - 0.1));
        return Color.rgb(red, green, blue);
    }

    @Override
    public void onItemClick(RmarkListAdapter.ViewHolder holder, int postion) {

    }

    @Override
    public void onClick(RmarkListAdapter.ViewHolder holder, View clickItem, int postion) {

    }

    @Override
    public void onClick(View v) {
        if (v == headBinding.btnContact) {
            if (!isMe){
                Intent intent = new Intent(SkillDetailsActivity.this, ChatActivity.class);
                intent.putExtra("skill", skill);

                Map<String, RobotUser> robotMap = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getRobotList();
                String chatLoginName = skill.getChatLoginName();
                if (robotMap.containsKey(chatLoginName)) {
                    intent.putExtra("userId", chatLoginName);
                    startActivity(intent);
                } else {
                    RobotUser robot = new RobotUser();
                    robot.setId(skill.getUid() + "");
                    robot.setUsername(chatLoginName);
                    robot.setNick(skill.getNickname());
                    robot.setAvatar(skill.getAvatar());
                    robot.setWechat(skill.getWeichat());

                    maimeng.yodian.app.client.android.chat.domain.User user = new maimeng.yodian.app.client.android.chat.domain.User();
                    user.setId(skill.getUid() + "");
                    user.setUsername(chatLoginName);
                    user.setNick(skill.getNickname());
                    user.setAvatar(skill.getAvatar());
                    user.setWechat(skill.getWeichat());
                    // 存入内存
                    ((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveOrUpdate(skill.getChatLoginName(), robot);
                    ((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveOrUpdate(skill.getChatLoginName(), user);
                    // 存入db
                    UserDao dao = new UserDao(SkillDetailsActivity.this);
                    dao.saveOrUpdate(user);
                    dao.saveOrUpdate(robot);
                    intent.putExtra("userId", chatLoginName);
                    intent.putExtra("userNickname", skill.getNickname());
                    startActivity(intent);
                }
            }
        }else if(v==headBinding.btnBuySkill){
            if(isMe){
                RmarkPublishActivity.show(this, skill, headBinding.btnBuySkill, REQEUST_RMARK_ADD);
            }else {
                PayWrapperActivity.show(SkillDetailsActivity.this,skill);
            }
        }
    }

    @Override
    public void onDelete(final RmarkListAdapter.ViewHolder holder) {

        AlertDialog.newInstance("提示", "确定要删除吗?").setPositiveListener(new AlertDialog.PositiveListener() {
            @Override
            public void onPositiveClick(final DialogInterface dialog) {
                dialog.dismiss();
                service.delete_rmark(holder.getBinding().getRmark().getScid(), new ToastCallback(SkillDetailsActivity.this) {
                    @Override
                    public void success(ToastResponse res, Response response) {
                        super.success(res, response);
                        if (res.isSuccess()) {
                            adapter.remove(holder.getPosition());
                        }
                    }
                });
            }

            @Override
            public String positiveText() {
                return getResources().getString(android.R.string.ok);
            }
        }).setNegativeListener(new AlertDialog.NegativeListener() {
            @Override
            public void onNegativeClick(DialogInterface dialog) {
                dialog.dismiss();
            }

            @Override
            public String negativeText() {
                return getResources().getString(android.R.string.cancel);
            }
        }).show(getFragmentManager(), "delete_dialog");


    }

    @Override
    public void onReport(final RmarkListAdapter.ViewHolder holder) {

        AlertDialog.newInstance("提示", "确定要举报吗?").setPositiveListener(new AlertDialog.PositiveListener() {
            @Override
            public void onPositiveClick(final DialogInterface dialog) {
                dialog.dismiss();
                Network.getService(CommonService.class).report(2, 0, holder.getBinding().getRmark().getScid(), 0, new ToastCallback(SkillDetailsActivity.this));
            }

            @Override
            public String positiveText() {
                return getResources().getString(android.R.string.ok);
            }
        }).setNegativeListener(new AlertDialog.NegativeListener() {
            @Override
            public void onNegativeClick(DialogInterface dialog) {
                dialog.dismiss();
            }

            @Override
            public String negativeText() {
                return getResources().getString(android.R.string.cancel);
            }
        }).show(getFragmentManager(), "delete_dialog");


    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    protected void initToolBar(Toolbar toolbar) {
        super.initToolBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setDisplayUseLogoEnabled(true);
//            actionBar.setLogo(R.drawable.ic_go_back);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_go_back);
            mTitle.setTextColor(Color.WHITE);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                ActivityCompat.finishAfterTransition(SkillDetailsActivity.this);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
