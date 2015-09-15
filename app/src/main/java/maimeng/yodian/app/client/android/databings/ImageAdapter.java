package maimeng.yodian.app.client.android.databings;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import maimeng.yodian.app.client.android.network.loader.CircleImageDrawable;
import maimeng.yodian.app.client.android.network.loader.Circle;

/**
 * Created by android on 9/1/15.
 */
public class ImageAdapter {

    @android.databinding.BindingAdapter("app:imgUrl")
    public static void image(ImageView iv, String url) {
        image(iv, url, null, null, null);
    }

    @android.databinding.BindingAdapter("app:imgUrl")
    public static void image(ImageView iv, ImageBindable url) {
        if (url == null) return;
        Uri uri = url.getUri();
        if (uri == null) return;
        image(iv, uri.toString(), null, null, url.getCircle());
    }

    public static void image(ImageView iv, String url, Drawable placeHolderDrawable, Drawable errorDrawable, final Circle circle) {
        if (TextUtils.isEmpty(url)) return;
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.showImageForEmptyUri(errorDrawable);
        builder.showImageOnLoading(placeHolderDrawable);
        if (circle != null) {
            builder.displayer(new BitmapDisplayer() {
                @Override
                public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
                    if (!(imageAware instanceof ImageViewAware)) {
                        throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
                    }
                    imageAware.setImageDrawable(new CircleImageDrawable(bitmap, circle));
                }
            });
        }
        ImageLoader.getInstance().displayImage(url, iv, builder.build());
    }
}
