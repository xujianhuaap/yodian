package maimeng.yodian.app.client.android.view.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.IconPageIndicator;
import com.viewpagerindicator.IconPagerAdapter;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.common.UEvent;
import maimeng.yodian.app.client.android.databings.ImageAdapter;
import maimeng.yodian.app.client.android.model.Float;
import maimeng.yodian.app.client.android.view.skill.SkillDetailsActivity;
import maimeng.yodian.app.client.android.view.user.UserHomeActivity;
import maimeng.yodian.app.client.android.widget.AutoScrollViewPager;
import maimeng.yodian.app.client.android.widget.ViewPagerFix;

/**
 * Created by android on 2015/10/23.
 */
public class FloatActivity extends AppCompatActivity implements ViewPagerFix.OnPageChangeListener, ViewPagerFix.OnFlipListener {
    private ViewPagerFix mPager;
    private IconPageIndicator indicator;
    private ViewPagerAdapter adapter;
    private int currentPage;

    public static void start(Context context, ArrayList<Float> floatPic) {
        context.startActivity(new Intent(context, FloatActivity.class).putExtra("datas", Parcels.wrap(floatPic)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float_dialog);
        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPager = (ViewPagerFix) findViewById(R.id.pager);
        indicator = (IconPageIndicator) findViewById(R.id.titles);
        mPager.addOnPageChangeListener(this);
        mPager.setCycle(true);
        mPager.setInterval(3000);
        mPager.setOnFlipListener(this);
        adapter = new ViewPagerAdapter(new ArrayList<View>(), mPager);
        mPager.setAdapter(adapter);
        mPager.setStopScrollWhenTouch(true);
        mPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
        indicator.setViewPager(mPager);
        mPager.stopAutoScroll();
        ArrayList<Float> list = Parcels.unwrap(getIntent().getParcelableExtra("datas"));
        if (list.size() < 2) {
            indicator.setVisibility(View.INVISIBLE);
        } else {
            indicator.setVisibility(View.VISIBLE);
        }
        final List<View> views = new ArrayList<>();
        int index = 0;
        for (Float flt : list) {
            ImageView iv = new ImageView(this);
            iv.setTag(R.id.tag_item, flt);
            iv.setTag(R.id.tag_id, index++);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch ((Integer) v.getTag(R.id.tag_id)) {
                        case 0:
                            MobclickAgent.onEvent(v.getContext(), UEvent.FLOATING_AD_1_COUNT);
                            break;
                        case 1:
                            MobclickAgent.onEvent(v.getContext(), UEvent.FLOATING_AD_2_COUNT);
                            break;
                        case 2:
                            MobclickAgent.onEvent(v.getContext(), UEvent.FLOATING_AD_3_COUNT);
                            break;

                    }
                    Float flt = (Float) v.getTag(R.id.tag_item);
                    switch (flt.getType()) {
                        case 1:
                            WebViewActivity.show(v.getContext(), flt.getValue());
                            break;
                        case 2:
                            UserHomeActivity.show(v.getContext(), Long.valueOf(flt.getValue()));
                            break;
                        case 3:
                            v.getContext().startActivity(new Intent(v.getContext(), SkillDetailsActivity.class).putExtra("sid", Long.valueOf(flt.getValue())));
                            break;

                    }
                }
            });
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            views.add(iv);
            ImageAdapter.image(iv, flt.getPic());
        }
        adapter.setViews(views);
        adapter.notifyDataSetChanged();
        indicator.notifyDataSetChanged();
        mPager.startAutoScroll();
    }

    @Override
    public void onFlip() {
        mPager.stopAutoScroll();
    }

    @Override
    public void onCancel() {
        mPager.startAutoScroll();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        this.currentPage = position;
        switch (currentPage) {
            case 0:
                MobclickAgent.onEvent(this, UEvent.FLOATING_AD_1);
                break;
            case 1:
                MobclickAgent.onEvent(this, UEvent.FLOATING_AD_2);
                break;
            case 2:
                MobclickAgent.onEvent(this, UEvent.FLOATING_AD_3);
                break;

        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class ViewPagerAdapter extends PagerAdapter implements IconPagerAdapter {
        private final ViewPagerFix viewPager;

        public void setViews(List<View> views) {
            this.views = views;
        }

        private List<View> views;

        public ViewPagerAdapter(List<View> views, ViewPagerFix viewPager) {
            this.views = views;
            this.viewPager = viewPager;
        }

        @Override
        public int getIconResId(int index) {
            return R.drawable.point;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final View child = views.get(position);
            container.addView(child);
            return child;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
