package maimeng.yodian.app.client.android.network.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;

import maimeng.yodian.app.client.android.network.loader.Circle;

/**
 * Created by android on 2015/9/15.
 */
public class CircleImageDrawable extends Drawable {
    private final Circle circle;
    private Paint mPaint;
    private int mWidth;
    private Bitmap mBitmap;

    public CircleImageDrawable(Bitmap bitmap, Circle circle) {
        mBitmap = bitmap;
        this.circle = circle;
        BitmapShader bitmapShader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setShader(bitmapShader);
        mWidth = Math.min(mBitmap.getWidth(), mBitmap.getHeight());
    }

    @Override
    public void draw(Canvas canvas) {
        int r = mWidth / 2;
        canvas.drawCircle(r, r, r, mPaint);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(circle.getBorderColor());
        paint.setStrokeWidth(circle.getBorderSize());
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(r, r, r - circle.getBorderSize() / 2, paint);
    }

    @Override
    public int getIntrinsicWidth() {
        return mWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mWidth;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
