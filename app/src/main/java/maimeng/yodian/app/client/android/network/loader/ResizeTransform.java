package maimeng.yodian.app.client.android.network.loader;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.widget.LinearLayout;

import com.squareup.picasso.Transformation;

import java.util.Arrays;

/**
 * Created by android on 2015/8/23.
 */
public class ResizeTransform implements Transformation {
    private final String url;
    private final int size;

    public ResizeTransform(String url, int size) {
        this.url = url;
        this.size = size;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        if (source == null) return null;
        int width = source.getWidth();
        int height = source.getHeight();
        Matrix matrix = new Matrix();
        float scale = this.size / (float) width;
        matrix.postScale(scale, scale);
        Bitmap result = Bitmap.createBitmap(source, 0, 0, width, height, matrix, true);
        source.recycle();
        return result;
    }

    @Override
    public String key() {
        return this.url + ResizeTransform.class.getName() + size;
    }
}
