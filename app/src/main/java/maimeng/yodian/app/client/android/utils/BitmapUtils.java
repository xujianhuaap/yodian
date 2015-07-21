package maimeng.yodian.app.client.android.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by android on 2015/7/21.
 */
public final class BitmapUtils {

    final public static Bitmap compress(Bitmap source, int height, int width, boolean crop){
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            options.inJustDecodeBounds = true;

            double beY = (double)options.outHeight * 1.0D / (double)height;
            double beX = (double)options.outWidth * 1.0D / (double)width;
            options.inSampleSize = (int)(crop?(beY > beX?beX:beY):(beY < beX?beX:beY));
            if(options.inSampleSize <= 1) {
                options.inSampleSize = 1;
            }

            while(options.outHeight * options.outWidth / options.inSampleSize > 2764800) {
                ++options.inSampleSize;
            }

            int newHeight = height;
            int newWidth = width;
            if(crop) {
                if(beY > beX) {
                    newHeight = (int)((double)width * 1.0D * (double)options.outHeight / (double)options.outWidth);
                } else {
                    newWidth = (int)((double)height * 1.0D * (double)options.outWidth / (double)options.outHeight);
                }
            } else if(beY < beX) {
                newHeight = (int)((double)width * 1.0D * (double)options.outHeight / (double)options.outWidth);
            } else {
                newWidth = (int)((double)height * 1.0D * (double)options.outWidth / (double)options.outHeight);
            }

            options.inJustDecodeBounds = false;
            if(source == null) {
                return null;
            } else {
                Bitmap scale = Bitmap.createScaledBitmap(source, newWidth, newHeight, true);
                if(scale != null) {
                    source = scale;
                }

                if(crop) {
                    Bitmap cropped = Bitmap.createBitmap(source, source.getWidth() - width >> 1, source.getHeight() - height >> 1, width, height);
                    if(cropped == null) {
                        return source;
                    }

                    source = cropped;
                }

                return source;
            }
        } catch (OutOfMemoryError var15) {
            options = null;
            return null;
        }
    }
}
