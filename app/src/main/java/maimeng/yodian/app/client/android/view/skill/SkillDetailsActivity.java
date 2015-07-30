package maimeng.yodian.app.client.android.view.skill;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import in.srain.cube.views.ptr.indicator.PtrIndicator;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.adapter.AbstractAdapter;
import maimeng.yodian.app.client.android.adapter.RmarkListAdapter;
import maimeng.yodian.app.client.android.common.PullHeadView;
import maimeng.yodian.app.client.android.databinding.ActivitySkillDetailsBinding;
import maimeng.yodian.app.client.android.databinding.ViewHeaderPlaceholderBinding;
import maimeng.yodian.app.client.android.model.Rmark;
import maimeng.yodian.app.client.android.model.Skill;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.RmarkListResponse;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.widget.AlphaForegroundColorSpan;
import maimeng.yodian.app.client.android.widget.EndlessRecyclerOnScrollListener;

/**
 * Created by android on 2015/7/22.
 */
public class SkillDetailsActivity extends AppCompatActivity implements PtrHandler,AppBarLayout.OnOffsetChangedListener, Callback<RmarkListResponse>,AbstractAdapter.ViewHolderClickListener<RmarkListAdapter.ViewHolder> {
    private static final String LOG_TAG = SkillDetailsActivity.class.getName();
    @Bind(R.id.recyclerView)
    ListView mListView;
    @Bind(R.id.refresh_layout)
    PtrFrameLayout mRefreshLayout;
    private SkillService service;
    private long sid;
    private int page=1;
    private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    private RmarkListAdapter adapter;
    private Interpolator mSmoothInterpolator;
    private int mHeaderHeight;
    private int mActionBarHeight;
    private int mMinHeaderTranslation;
    private View mPlaceHolderView;
    private ViewHeaderPlaceholderBinding headBinding;
    private View mHeader;
    private SpannableString mSpannableString;
    private AlphaForegroundColorSpan mAlphaForegroundColorSpan;
    private int mActionBarTitleColor;
    private View mTitleContainer;
    private ActivitySkillDetailsBinding binding;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = Network.getService(SkillService.class);
        mActionBarTitleColor = 0xffffff;
//        mSmoothInterpolator = new AccelerateDecelerateInterpolator();
        mSmoothInterpolator = new LinearInterpolator();
        mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.skill_detail_head_height);
        mMinHeaderTranslation = -mHeaderHeight + getTitleBarHeight();
        mSpannableString = new SpannableString("测试标题");
        mAlphaForegroundColorSpan = new AlphaForegroundColorSpan(mActionBarTitleColor);
        binding=DataBindingUtil.setContentView(this, R.layout.activity_skill_details);
        ButterKnife.bind(this);
        headBinding=DataBindingUtil.inflate(getLayoutInflater(), R.layout.view_header_placeholder, mListView, false);
        mPlaceHolderView = headBinding.getRoot();
        mHeader = findViewById(R.id.header);
        mTitleContainer = findViewById(R.id.title_containar);
        mListView.addHeaderView(mPlaceHolderView);
        adapter=new RmarkListAdapter(this);
        mRefreshLayout.setPtrHandler(this);
        StoreHouseHeader header= PullHeadView.create(this);
        mRefreshLayout.addPtrUIHandler(header);
        mRefreshLayout.setHeaderView(header);
        mRefreshLayout.addPtrUIHandler(new PtrUIHandler() {
            @Override
            public void onUIReset(PtrFrameLayout ptrFrameLayout) {
                LogUtil.i("mRefreshLayout","onUIReset");
            }

            @Override
            public void onUIRefreshPrepare(PtrFrameLayout ptrFrameLayout) {
                LogUtil.i("mRefreshLayout","onUIRefreshPrepare");
            }

            @Override
            public void onUIRefreshBegin(PtrFrameLayout ptrFrameLayout) {
                LogUtil.i("mRefreshLayout","onUIRefreshBegin");
            }

            @Override
            public void onUIRefreshComplete(PtrFrameLayout ptrFrameLayout) {
                LogUtil.i("mRefreshLayout","onUIRefreshComplete");
            }

            @Override
            public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
                float currentPercent = Math.min(1.0F, ptrIndicator.getCurrentPercent());
                int posY = ptrIndicator.getCurrentPosY();
                LogUtil.i("mRefreshLayout", "posY:%d", posY);
                mHeader.setTranslationY(posY);
            }
        });
        LinearLayoutManager layout = new LinearLayoutManager(this);
        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(layout) {
            @Override
            public void onLoadMore() {
                page ++;
                sync();
            }
        };
        mListView.setAdapter(adapter);

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int scrollY = getScrollY();
                //sticky actionbar
                int translationY = Math.max(-scrollY, mMinHeaderTranslation);
                mHeader.setTranslationY(translationY);
                //header_logo --> actionbar icon
                float ratio = clamp(mHeader.getTranslationY() / mMinHeaderTranslation, 0.0f, 1.0f);
                setTextColor(binding.price,0,0,ratio);
                //binding.headerLogo.setAlpha(1f - ratio);
                mTitleContainer.setAlpha(ratio * 0.85f);//控制title栏的透明度
                float interpolation = mSmoothInterpolator.getInterpolation(ratio);
                interpolate(binding.headerLogo, binding.logo, interpolation);
                interpolate(binding.price, binding.titlePrice, interpolation);
                //actionbar title alpha
                //getActionBarTitleView().setAlpha(clamp(5.0F * ratio - 4.0F, 0.0F, 1.0F));
                //---------------------------------
                //better way thanks to @cyrilmottier
                setTitleAlpha(clamp(5.0F * ratio - 4.0F, 0.0F, 1.0F));
            }
        });


        sid=getIntent().getLongExtra("sid",0);
        mRefreshLayout.autoRefresh();
    }

    private void setTextColor(TextView tv, int startColor, int endColor, float ratio) {
        if(ratio<0.3f){
            tv.setTextColor(getResources().getColor(R.color.colorPrimary));
        }else{
            tv.setTextColor(Color.parseColor("#ffffff"));
        }


    }

    private void setTitleAlpha(float alpha) {

        findViewById(R.id.header_logo_bg).setAlpha(1f-alpha);
        findViewById(R.id.btn_contect_circle).setAlpha(alpha);
        mAlphaForegroundColorSpan.setAlpha(alpha);
        mSpannableString.setSpan(mAlphaForegroundColorSpan, 0, mSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ((TextView)findViewById(R.id.title)).setText(mSpannableString);
    }
    private RectF getOnScreenRect(RectF rect, View view) {
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        return rect;
    }

    private void interpolate(View start, View end, float interpolation) {
        LogUtil.i(LOG_TAG,"interpolation:%f",interpolation);
        RectF mRect1 = new RectF();
        RectF mRect2 = new RectF();
        getOnScreenRect(mRect1, start);
        getOnScreenRect(mRect2, end);

        float scaleX = 1.0F + interpolation * (mRect2.width() / mRect1.width() - 1.0F);
        float scaleY = 1.0F + interpolation * (mRect2.height() / mRect1.height() - 1.0F);
        float translationX = 0.5F * (interpolation * (mRect2.left + mRect2.right - mRect1.left - mRect1.right));
        float translationY = 0.5F * (interpolation * (mRect2.top + mRect2.bottom - mRect1.top - mRect1.bottom));

        start.setTranslationX(translationX);
        start.setTranslationY(translationY - mHeader.getTranslationY());
        start.setScaleX(scaleX);
        start.setScaleY(scaleY);
    }
    public float clamp(float value, float min, float max) {
        return Math.max(min,Math.min(value, max));
    }
    public int getScrollY() {
        View c = mListView.getChildAt(0);
        if (c == null) {
            return 0;
        }

        int firstVisiblePosition = mListView.getFirstVisiblePosition();
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
        if(res.isSuccess()){
            List<Rmark> list = res.getData().getList();
            Skill skill = res.getData().getDetail();
            headBinding.setSkill(skill);
            binding.setSkill(skill);
            Spanned text = Html.fromHtml(getResources().getString(R.string.lable_price, skill.getPrice(), skill.getUnit()));
            headBinding.price.setText(text);
            binding.price.setText(text);
            binding.titlePrice.setText(text);
            if(list.size()>0){
                Rmark item = list.get(0);
                for(int i=0;i<=100;i++){
                    list.add(item);
                }
                adapter.reload(list,page!=1);
                adapter.notifyDataSetChanged();
            }

        }else{
            res.showMessage(this);
        }
    }

    @Override
    public void failure(HNetError hNetError) {

    }

    @Override
    public void end() {
        mRefreshLayout.refreshComplete();
    }

    @Override
    public void onItemClick(RmarkListAdapter.ViewHolder holder, int postion) {

    }

    @Override
    public void onClick(RmarkListAdapter.ViewHolder holder, View clickItem, int postion) {

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
