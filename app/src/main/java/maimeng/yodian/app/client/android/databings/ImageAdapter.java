package maimeng.yodian.app.client.android.databings;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import maimeng.yodian.app.client.android.network.loader.Circle;
import maimeng.yodian.app.client.android.network.loader.ImageLoaderManager;

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
        new ImageLoaderManager.Loader(iv, Uri.parse(url)).placeHolder(placeHolderDrawable).error(errorDrawable).circle(circle).start(iv.getContext());
    }
}
