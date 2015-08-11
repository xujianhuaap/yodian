package maimeng.yodian.app.client.android.view.skill;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;

import com.easemob.applib.controller.HXSDKHelper;
import com.melnykov.fab.ScrollDirectionListener;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
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
import maimeng.yodian.app.client.android.common.model.Skill;
import maimeng.yodian.app.client.android.databinding.ActivitySkillDetailsBinding;
import maimeng.yodian.app.client.android.databinding.ViewHeaderPlaceholderBinding;
import maimeng.yodian.app.client.android.model.Rmark;
import maimeng.yodian.app.client.android.model.User;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.response.RmarkListResponse;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.service.CommonService;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.widget.EndlessRecyclerOnScrollListener;
import maimeng.yodian.app.client.android.widget.ListLayoutManager;

/**
 * Created by android on 2015/7/22.
 */
public class SkillDetailsActivity extends AppCompatActivity implements PtrHandler, AppBarLayout.OnOffsetChangedListener, Callback<RmarkListResponse>, AbstractHeaderAdapter.ViewHolderClickListener<RmarkListAdapter.ViewHolder>, View.OnClickListener, RmarkListAdapter.ActionListener {
    private static final String LOG_TAG = SkillDetailsActivity.class.getName();
    private SkillService service;
    private long sid;
    private int page = 1;
    private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = User.read(this);
        service = Network.getService(SkillService.class);
//        mSmoothInterpolator = new AccelerateDecelerateInterpolator();
        mSmoothInterpolator = new LinearInterpolator();
        mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.skill_detail_head_height);
        mMinHeaderTranslation = -mHeaderHeight + getTitleBarHeight();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_skill_details);
        ViewCompat.setTransitionName(binding.btnBack, "back");
//        ViewCompat.setTransitionName(headBinding.pic, "pic");
//        ViewCompat.setTransitionName(headBinding.userNickname, "nick");
//        ViewCompat.setTransitionName(headBinding.userAvatar, "avatar");
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(SkillDetailsActivity.this);
            }
        });
        binding.headerLogoBg.setOnClickListener(this);
        binding.headerLogo.setOnClickListener(this);

        ButterKnife.bind(this);
        headBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.view_header_placeholder, binding.recyclerView, false);
        mPlaceHolderView = headBinding.getRoot();
        binding.recyclerView.addHeaderView(mPlaceHolderView);
        adapter = new RmarkListAdapter(this, this);
        binding.refreshLayout.setPtrHandler(this);
        StoreHouseHeader header = PullHeadView.create(this);
        binding.refreshLayout.addPtrUIHandler(header);
        binding.refreshLayout.setHeaderView(header);
        final ObjectAnimator colorAnimator = ObjectAnimator.ofObject(binding.price, "textColor", new ArgbEvaluator(), getResources().getColor(R.color.colorPrimaryDark), getResources().getColor(android.R.color.white));
        colorAnimator.setDuration(10000);
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int color = (Integer) animation.getAnimatedValue();
                binding.price.setTextColor(color);

            }
        });
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
                binding.header.setTranslationY(posY);
            }
        });
        ListLayoutManager layout = new ListLayoutManager(this);
        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(layout) {
            @Override
            public void onLoadMore() {
                page++;
                sync();
            }
        };
        binding.recyclerView.setAdapter(adapter);

        //mListView.setOnScrollListener();

        binding.btnBack.attachToListView(binding.recyclerView, new ScrollDirectionListener() {
            @Override
            public void onScrollDown() {
                binding.btnBack.show();
            }

            @Override
            public void onScrollUp() {
                binding.btnBack.hide();
            }
        }, new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int scrollY = getScrollY();
                //sticky titleBar
                int translationY = Math.max(-scrollY, mMinHeaderTranslation);
                binding.header.setTranslationY(translationY);
                //header_logo --> actionbar icon
                float ratio = clamp(binding.header.getTranslationY() / mMinHeaderTranslation, 0.0f, 1.0f);
                colorAnimator.setCurrentPlayTime((long) (ratio * 10000));//文字颜色渐变
                //binding.headerLogo.setAlpha(1f - ratio);
                binding.titleContainar.setAlpha(ratio * 0.85f);//控制title栏的透明度
                if (inited) binding.headerLogoBg.setAlpha(1f - ratio);
                float interpolation = mSmoothInterpolator.getInterpolation(ratio);
                interpolate(binding.headerLogo, binding.logo, interpolation);
                interpolate(binding.price, binding.titlePrice, interpolation);
                //titleBar title alpha
                //getActionBarTitleView().setAlpha(clamp(5.0F * ratio - 4.0F, 0.0F, 1.0F));
                //---------------------------------
                //better way thanks to @cyrilmottier
                setTitleAlpha(clamp(5.0F * ratio - 4.0F, 0.0F, 1.0F));
            }
        });
        Skill skill = getIntent().getParcelableExtra("skill");
        sid = skill.getId();
        isMe = skill.getUid() == user.getUid();
        if (isMe) {
            binding.headerLogoImg.setImageResource(R.drawable.btn_ic_add);
            binding.headerLogoBgImg.setImageResource(R.drawable.btn_ic_add);
            binding.btnContect.setText("添加日记");
        }

        binding.refreshLayout.autoRefresh();
    }

    private void setTitleAlpha(float alpha) {
        findViewById(R.id.btn_contect_circle).setAlpha(alpha);
    }

    private RectF getOnScreenRect(RectF rect, View view) {
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        return rect;
    }

    private void interpolate(View start, View end, float interpolation) {
        RectF mRect1 = new RectF();
        RectF mRect2 = new RectF();
        getOnScreenRect(mRect1, start);
        getOnScreenRect(mRect2, end);

        float scaleX = 1.0F + interpolation * (mRect2.width() / mRect1.width() - 1.0F);
        float scaleY = 1.0F + interpolation * (mRect2.height() / mRect1.height() - 1.0F);
        float translationX = 0.5F * (interpolation * (mRect2.left + mRect2.right - mRect1.left - mRect1.right));
        float translationY = 0.5F * (interpolation * (mRect2.top + mRect2.bottom - mRect1.top - mRect1.bottom));

        start.setTranslationX(translationX);
        start.setTranslationY(translationY - binding.header.getTranslationY());
        start.setScaleX(scaleX);
        start.setScaleY(scaleY);
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
        sync();
    }

    private void sync() {
        service.rmark_list(sid, page, this);
    }

    @Override
    public void start() {

    }

    @Override
    public void success(RmarkListResponse res, Response response) {
        if (res.isSuccess()) {
            List<Rmark> list = res.getData().getList();
            if (skill == null) {
                binding.headerLogoBg.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        inited = true;
                    }
                }, 1000);
                skill = res.getData().getDetail();
                headBinding.setSkill(skill);
                binding.setSkill(skill);
                Spanned text = Html.fromHtml(getResources().getString(R.string.lable_price, skill.getPrice(), skill.getUnit()));
                headBinding.price.setText(text);
                binding.price.setText(text);
                binding.titlePrice.setText(text);
            }
//            if(list.size()>0){
//                Rmark item = list.get(0);
//                for(int i=0;i<=100;i++){
//                    list.add(item);
//                }
//            }
            adapter.reload(list, page != 1);
            adapter.notifyDataSetChanged();
        } else {
            res.showMessage(this);
        }
    }

    @Override
    public void failure(HNetError hNetError) {

    }

    @Override
    public void end() {
        binding.refreshLayout.refreshComplete();
    }

    @Override
    public void onItemClick(RmarkListAdapter.ViewHolder holder, int postion) {

    }

    @Override
    public void onClick(RmarkListAdapter.ViewHolder holder, View clickItem, int postion) {

    }

    @Override
    public void onClick(View v) {
        if (v == binding.headerLogo || v == binding.headerLogoBg) {
            if (isMe) {
                RmarkPublishActivity.show(this, skill);
            } else {
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

                    maimeng.yodian.app.client.android.chat.domain.User user = new maimeng.yodian.app.client.android.chat.domain.User();
                    user.setId(skill.getUid() + "");
                    user.setUsername(chatLoginName);
                    user.setNick(skill.getNickname());
                    user.setAvatar(skill.getAvatar());
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
        }
    }

    @Override
    public void onDelete(RmarkListAdapter.ViewHolder holder) {
        service.delete_rmark(holder.getBinding().getRmark().getScid(), new ToastCallback(this) {
            @Override
            public void success(ToastResponse res, Response response) {
                super.success(res, response);
                if (res.isSuccess()) {
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onReport(RmarkListAdapter.ViewHolder holder) {
        Network.getService(CommonService.class).report(2, 0, holder.getBinding().getRmark().getScid(), 0, new ToastCallback(this));
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
