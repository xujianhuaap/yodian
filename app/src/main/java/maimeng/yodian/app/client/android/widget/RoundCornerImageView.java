package maimeng.yodian.app.client.android.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by xujianhua on 8/31/15.
 */
public class RoundCornerImageView extends ImageView {

    public RoundCornerImageView(Context context) {
        super(context);

    }

    public RoundCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public RoundCornerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    protected void onDraw(Canvas canvas) {
        int roundPx =40;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.TRANSPARENT); //这里的颜色决定了边缘的颜色

        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        int w = getWidth();
        int h = getHeight();
        RectF rectF = new RectF(0, 0, w, w);

        Bitmap roundBitmap = getCroppedBitmap(bitmap, w,h, roundPx);

        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawBitmap(roundBitmap, 0, 0, null);
    }

    public static Bitmap getCroppedBitmap(Bitmap bmp, int length,int height, int roundPx) {

        Bitmap sbmp;
        if (bmp.getWidth() != length || bmp.getHeight() != length)
            sbmp = Bitmap.createScaledBitmap(bmp, length, height, false);
        else
            sbmp = bmp;

        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(output);

        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());
        final RectF rectF = new RectF(0, 0, sbmp.getWidth() - 0, sbmp.getHeight() - 0);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.RED);

        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(sbmp, rect, rect, paint);

        return output;
    }
}
