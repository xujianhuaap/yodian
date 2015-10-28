package maimeng.yodian.app.client.android.widget;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by android on 2015/10/28.
 */
public class ScrollImageView extends View {
    private int mImageWidth, mImageHeight;
    private volatile Rect mRect = new Rect();
    private BitmapRegionDecoder mDecoder;
    private static final BitmapFactory.Options options = new BitmapFactory.Options();
    private ObjectAnimator animator;

    static {
        options.inPreferredConfig = Bitmap.Config.RGB_565;
    }

    public ScrollImageView(Context context) {
        super(context);
    }

    public ScrollImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScrollImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void checkWidth() {

        Rect rect = mRect;
        int imageWidth = mImageWidth;
        int imageHeight = mImageHeight;

        if (rect.right > imageWidth) {
            rect.right = imageWidth;
            rect.left = imageWidth - getWidth();
        }

        if (rect.left < 0) {
            rect.left = 0;
            rect.right = getWidth();
        }
    }

    private void checkHeight() {

        Rect rect = mRect;
        int imageWidth = mImageWidth;
        int imageHeight = mImageHeight;

        if (rect.bottom > imageHeight) {
            rect.bottom = imageHeight;
            rect.top = imageHeight - getHeight();
        }

        if (rect.top < 0) {
            rect.top = 0;
            rect.bottom = getHeight();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDecoder != null) {
            Bitmap bm = mDecoder.decodeRegion(mRect, options);
            if (bm != null) {
                canvas.drawBitmap(bm, 0, 0, null);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int imageWidth = mImageWidth;
        int imageHeight = mImageHeight;

        //默认直接显示图片的中心区域，可以自己去调节
        mRect.left = imageWidth / 2 - width / 2;
        mRect.top = imageHeight / 2 - height / 2;
        mRect.right = mRect.left + width;
        mRect.bottom = mRect.top + height;
    }

    public void setImage(InputStream is) {
        try {
            mDecoder = BitmapRegionDecoder.newInstance(is, false);
            BitmapFactory.Options tmpOptions = new BitmapFactory.Options();
            tmpOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, tmpOptions);
            mImageWidth = tmpOptions.outWidth;
            mImageHeight = tmpOptions.outHeight;
            requestLayout();
            invalidate();
            startScroll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int offsetX;

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
        mRect.offset(0, offsetY);
        checkHeight();
        invalidate();
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
        mRect.offset(offsetX, 0);
        checkWidth();
        invalidate();
    }

    private int offsetY;

    public void startScroll() {
        if (animator == null) {
            animator = ObjectAnimator.ofInt(this, "offsetX", 0, 3310).setDuration(10000);
            animator.setRepeatMode(ObjectAnimator.REVERSE);
            animator.setRepeatCount(ObjectAnimator.INFINITE);
//            animator.start();
        }
    }
}
