package maimeng.yodian.app.client.android.network.loader;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.squareup.picasso.Transformation;

import java.util.Arrays;

/**
 * Created by android on 2015/8/23.
 */
public class ResizeTransform extends BitmapTransformation {
    private final String url;
    private final int size;
    private final ImageView mImageView;

    public ResizeTransform(ImageView iv, String url, int size) {
        super(iv.getContext());
        this.url = url;
        this.size = size;
        this.mImageView = iv;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return ResizeBitmap(pool, toTransform);

    }

    private Bitmap ResizeBitmap(BitmapPool pool, Bitmap source) {
        if (source == null) return null;
        int width = source.getWidth();
        int height = source.getHeight();

        Matrix matrix = new Matrix();
        float scale = this.size / (float) width;
        matrix.postScale(scale, scale);


        Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(source, matrix, new Paint());
        source.recycle();
//        if (result.getHeight() > 2048) {
//            Bitmap clipBitmap = Bitmap.createBitmap(result, 0, 0, result.getWidth(), 2048);
//            result.recycle();
//            return clipBitmap;
//        }
        return result;


    }

    @Override
    public String getId() {
        return this.url + ResizeTransform.class.getName() + size;
    }
}
