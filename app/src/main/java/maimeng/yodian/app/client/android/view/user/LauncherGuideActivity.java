package maimeng.yodian.app.client.android.view.user;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.common.LauncherCheck;
import maimeng.yodian.app.client.android.view.auth.AuthRedirect;

/**
 * Created by xujianhua on 9/2/15.
 */
public class LauncherGuideActivity extends Activity implements View.OnClickListener
        , ViewPager.OnPageChangeListener {
    private ViewPager viewPager;
    private PagerAdapter adapter;
    private ArrayList<View> views = new ArrayList<View>();
    private final static int[] resIds = new int[]{
            R.mipmap.ic_launch_first,
            R.mipmap.ic_launch_second,
            R.mipmap.ic_launch_third,
            R.mipmap.ic_launch_four

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (!LauncherCheck.isFirstRun(this)) {
            finish();
        } else {
            setContentView(R.layout.activity_launcher_guide);
            viewPager = (ViewPager) this.findViewById(R.id.pager);
            for (int resId : resIds) {
                View root = getLayoutInflater().inflate(R.layout.activity_launcher_guide_page1, null, false);
                ImageView imageView = (ImageView) root.findViewById(R.id.launchPage);
                imageView.setImageResource(resId);
                views.add(root);
            }
            views.get(views.size() - 1).setOnClickListener(this);
            adapter = new GuideAdapter(views);
            viewPager.setAdapter(adapter);
            viewPager.addOnPageChangeListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        LauncherCheck.updateFirstRun(this, false);
        AuthRedirect.toHome(this);
        finish();
    }


    private class GuideAdapter extends PagerAdapter {
        private final ArrayList<View> views = new ArrayList<View>();

        GuideAdapter(ArrayList<View> views) {
            this.views.clear();
            this.views.addAll(views);
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position == views.size() - 1) {
            viewPager.removeOnPageChangeListener(this);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (position == views.size() - 1) {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    LauncherCheck.updateFirstRun(LauncherGuideActivity.this, false);
                    AuthRedirect.toHome(LauncherGuideActivity.this);
                    finish();
                }
            };
            new Timer().schedule(task, 3000);

        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}