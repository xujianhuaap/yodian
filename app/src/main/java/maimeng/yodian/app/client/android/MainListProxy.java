package maimeng.yodian.app.client.android;

import android.animation.AnimatorSet;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

/**
 * Created by android on 15-7-13.
 */
public class MainListProxy implements ActivityProxy{
    private final View mView;
    private final MainActivity mActivity;
    private final TextView mTitle;

    public MainListProxy(MainActivity activity, View view){
        this.mView=view;
        this.mActivity=activity;
        this.mTitle=(TextView)view.findViewById(R.id.base_title);
    }

    @Override
    public void onTitleChanged(CharSequence title, int color) {
        if(mTitle!=null)mTitle.setText(title);
    }

    @Override
    public void show(final FloatingActionButton button) {
        mActivity.setTitle("优点精选");
        int type = TranslateAnimation.RELATIVE_TO_SELF;
        TranslateAnimation animation = new TranslateAnimation(type,0,type, 0,type,1f,type, 0);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                button.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                button.setEnabled(true);
                setStatusBarColor(mActivity.getResources().getColor(R.color.colorPrimary));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        this.mView.startAnimation(animation);
        this.mView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hide(final FloatingActionButton button) {
        int type = TranslateAnimation.RELATIVE_TO_SELF;
        AnimationSet animationSet=new AnimationSet(true);
        animationSet.setDuration(300);
        animationSet.setInterpolator(new AccelerateInterpolator());
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                button.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mActivity.setTitle("");
                button.setEnabled(true);
                if (android.os.Build.VERSION.SDK_INT >= 21) {
                    setStatusBarColor(mActivity.getResources().getColor(R.color.colorPrimaryGreen));
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        TranslateAnimation animation = new TranslateAnimation(type,0,type, 0,type,0,type, 1f);
        int[] xy=new int[2];
        button.getLocationOnScreen(xy);
        animationSet.addAnimation(animation);
        this.mView.startAnimation(animationSet);
        this.mView.setVisibility(View.GONE);
    }

    @Override
    public boolean isShow() {
        return mView.getVisibility()==View.VISIBLE;
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
    public void setStatusBarColor(int color){
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = mActivity.getWindow();
            // 很明显，这两货是新API才有的。
            window.setStatusBarColor(colorBurn(color));
            window.setNavigationBarColor(colorBurn(color));
        }
    }
}
