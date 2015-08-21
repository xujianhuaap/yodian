package cn.bingoogolapple.bgabanner;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/6/19 11:23
 * 描述:继承ViewPager，通过反射方式实现支持低版本上切换动画
 */
public class BGAViewPager extends ViewPager {
    private boolean mScrollable = true;
    private float mDownX;
    private float mDownY;

    public BGAViewPager(Context context) {
        super(context);
    }

    public BGAViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPageTransformer(boolean reverseDrawingOrder, ViewPager.PageTransformer transformer) {
        /**
         继承ViewPager，重写setPageTransformer方法，移除版本限制，通过反射设置参数和方法

         getDeclaredMethod*()获取的是【类自身】声明的所有方法，包含public、protected和private方法。
         getMethod*()获取的是类的所有共有方法，这就包括自身的所有【public方法】，和从基类继承的、从接口实现的所有【public方法】。

         getDeclaredField获取的是【类自身】声明的所有字段，包含public、protected和private字段。
         getField获取的是类的所有共有字段，这就包括自身的所有【public字段】，和从基类继承的、从接口实现的所有【public字段】。
         */
        Class viewpagerClass = ViewPager.class;

        try {
            boolean hasTransformer = transformer != null;

            Field pageTransformerField = viewpagerClass.getDeclaredField("mPageTransformer");
            pageTransformerField.setAccessible(true);
            PageTransformer mPageTransformer = (PageTransformer) pageTransformerField.get(this);

            boolean needsPopulate = hasTransformer != (mPageTransformer != null);
            pageTransformerField.set(this, transformer);

            Method setChildrenDrawingOrderEnabledCompatMethod = viewpagerClass.getDeclaredMethod("setChildrenDrawingOrderEnabledCompat", boolean.class);
            setChildrenDrawingOrderEnabledCompatMethod.setAccessible(true);
            setChildrenDrawingOrderEnabledCompatMethod.invoke(this, hasTransformer);

            Field drawingOrderField = viewpagerClass.getDeclaredField("mDrawingOrder");
            drawingOrderField.setAccessible(true);
            if (hasTransformer) {
                drawingOrderField.setInt(this, reverseDrawingOrder ? 2 : 1);
            } else {
                drawingOrderField.setInt(this, 0);
            }

            if (needsPopulate) {
                Method populateMethod = viewpagerClass.getDeclaredMethod("populate");
                populateMethod.setAccessible(true);
                populateMethod.invoke(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置调用setCurrentItem(int item, boolean smoothScroll)方法时，page切换的时间长度
     *
     * @param duration page切换的时间长度
     */
    public void setPageChangeDuration(int duration) {
        try {
            Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            scrollerField.set(this, new PageChangeDurationScroller(getContext(), duration));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置是否允许用户手指滑动
     *
     * @param scrollable true表示允许跟随用户触摸滑动，false反之
     */
    public void setAllowUserScrollable(boolean scrollable) {
        mScrollable = scrollable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mScrollable) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mScrollable) {
            return super.onTouchEvent(ev);
        } else {
            return false;
        }
    }

    private BGABanner.OnFlipListener onFlipListener;

    BGABanner.OnFlipListener getOnFlipListener() {
        return onFlipListener;
    }

    void setOnFlipListener(BGABanner.OnFlipListener onFlipListener) {
        this.onFlipListener = onFlipListener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(getX() - mDownX) > Math.abs(ev.getY() - mDownY)) {
                    if (onFlipListener != null) onFlipListener.onFlip();
                } else {
                    if (onFlipListener != null) onFlipListener.onCancel();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (onFlipListener != null) onFlipListener.onCancel();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}