package maimeng.yodian.app.client.android.databings;

import android.databinding.BindingAdapter;
import android.widget.TextView;

/**
 * Created by android on 15-7-22.
 */
public class ResourceAdapter {
    @BindingAdapter("android:text")
    public static void spanText(TextView iv,CharSequence text){
        iv.setText(text,TextView.BufferType.SPANNABLE);
    }
}
