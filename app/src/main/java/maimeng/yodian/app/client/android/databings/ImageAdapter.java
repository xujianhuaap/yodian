package maimeng.yodian.app.client.android.databings;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;

import maimeng.yodian.app.client.android.widget.YDView;

/**
 * Created by android on 9/1/15.
 */
public class ImageAdapter {
    @android.databinding.BindingAdapter("app:imgUrl")
    public static void image(YDView iv, String url) {
        iv.setImageURI(Uri.parse(url));
    }
}
