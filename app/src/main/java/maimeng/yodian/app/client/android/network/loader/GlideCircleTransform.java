package maimeng.yodian.app.client.android.network.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by android on 8/18/15.
 */
abstract class GlideCircleTransform extends BitmapTransformation {
    private final Circle circle;

    public GlideCircleTransform(Context context, Circle circle) {
        super(context);
        this.circle = circle;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return circleCrop(pool, toTransform);
    }

    private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;
        int size = Math.min(source.getWidth(), source.getHeight());
        int width = (source.getWidth() - size) / 2;
        int height = (source.getHeight() - size) / 2;

//        // TODO this could be acquired from the pool too
//        Bitmap squared = Bitmap.createBitmap(source, width, height, size, size);

        Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(result);
        if (circle != null) {
            BitmapShader shader = new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            Paint paint = new Paint();
            paint.setShader(shader);
            paint.setAntiAlias(true);
            float r = size / 2f;
            if (width != 0 || height != 0) {
                Matrix matrix = new Matrix();
                matrix.setTranslate(-width, -height);
                shader.setLocalMatrix(matrix);
            }
            canvas.drawCircle(r, r, r, paint);
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(circle.getBorderColor());
            paint.setStrokeWidth(circle.getBorderSize());
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(r, r, r - circle.getBorderSize() / 2, paint);
        } else {
            BitmapShader shader = new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            Paint paint = new Paint();
            paint.setShader(shader);
            paint.setAntiAlias(true);
            float r = size / 2f;
            if (width != 0 || height != 0) {
                Matrix matrix = new Matrix();
                matrix.setTranslate(-width, -height);
                shader.setLocalMatrix(matrix);
            }
            canvas.drawCircle(r, r, r, paint);
        }
        return result;
    }

    @Override
    public String getId() {
        if (circle != null) {
            return circle.getBorderColor() + circle.getClass().getName() + getClass().getName() + circle.getBorderSize();
        }
        return getClass().getName();
    }

}
