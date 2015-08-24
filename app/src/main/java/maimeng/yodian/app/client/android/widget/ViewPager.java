package maimeng.yodian.app.client.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

/**
 * Created by android on 2015/8/24.
 */
public class ViewPager extends AutoScrollViewPager {
    public interface OnFlipListener {
        void onFlip();

        void onCancel();
    }

    public ViewPager(Context paramContext) {
        super(paramContext);
    }

    public ViewPager(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    private OnFlipListener onFlipListener;

    public OnFlipListener getOnFlipListener() {
        return onFlipListener;
    }

    public void setOnFlipListener(OnFlipListener onFlipListener) {
        this.onFlipListener = onFlipListener;
    }

    private float mDownX;
    private float mDownY;

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
