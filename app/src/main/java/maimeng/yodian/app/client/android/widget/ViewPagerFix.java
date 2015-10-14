package maimeng.yodian.app.client.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by android on 2015/8/24.
 */
public class ViewPagerFix extends AutoScrollViewPager {
    public interface OnClickListener {
        void onClickListener(View v);
    }

    public interface OnFlipListener {
        void onFlip();

        void onCancel();
    }

    public ViewPagerFix(Context paramContext) {
        super(paramContext);
    }

    public ViewPagerFix(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    private OnFlipListener onFlipListener;
    private OnClickListener onClickListener;


    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public OnFlipListener getOnFlipListener() {
        return onFlipListener;
    }

    public void setOnFlipListener(OnFlipListener onFlipListener) {
        this.onFlipListener = onFlipListener;
    }

    private float mDownX;
    private float mDownY;
    private float mUpForClickX;
    private float mUPForClickY;
    private float mDownForClickX;
    private float mDownForClickY;

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

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            mDownForClickX = ev.getX();
            mDownForClickY = ev.getY();
        }
        if (ev.getActionMasked() == MotionEvent.ACTION_UP) {
            mUpForClickX = ev.getX();
            mUPForClickY = ev.getY();
            if (mUpForClickX == mDownForClickX && mUPForClickY == mDownForClickY) {
                onClickListener.onClickListener(this);
            }

        }
        return super.onTouchEvent(ev);
    }

}
