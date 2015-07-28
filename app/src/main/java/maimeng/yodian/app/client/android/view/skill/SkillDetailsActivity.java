package maimeng.yodian.app.client.android.view.skill;

import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import maimeng.yodian.app.client.android.R;

/**
 * Created by android on 2015/7/22.
 */
public class SkillDetailsActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {
    private static final String LOG_TAG = SkillDetailsActivity.class.getName();
    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.scrollView)
    CoordinatorLayout mScrollView;
    @Bind(R.id.appBarLayout)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.title_containar)
    FrameLayout mTitleContainar;
    @Bind(R.id.content_containar)
    FrameLayout mContentContainar;//装着顶部详情的容器
    @Bind(R.id.wechat_icon)
    ImageView mWechatIcon;
    @Bind(R.id.title_wechat_icon)
    ImageView mTitleWechatIcon;
    private RecyclerView mList;
    private RectF mRect1 = new RectF();
    private RectF mRect2 = new RectF();
    private AccelerateDecelerateInterpolator mSmoothInterpolator;
    private float mMinHeaderTranslation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill_details);
        mSmoothInterpolator = new AccelerateDecelerateInterpolator();
        ButterKnife.bind(this);
        mMinHeaderTranslation = -TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,180,getResources().getDisplayMetrics()) + getResources().getDimensionPixelSize(R.dimen.actionBarSize);
        mList = (RecyclerView) findViewById(R.id.recyclerView);
        mList.setLayoutManager(new LinearLayoutManager(this));
        mList.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(new TextView(parent.getContext()));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((TextView) holder.itemView).setText("test" + position);
            }

            @Override
            public int getItemCount() {
                return 100;
            }
        });
        mAppBarLayout.addOnOffsetChangedListener(this);
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
