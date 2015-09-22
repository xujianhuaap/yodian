package maimeng.yodian.app.client.android.databings;

import android.databinding.BindingAdapter;
import android.databinding.BindingConversion;
import android.text.Html;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import maimeng.yodian.app.client.android.utils.MoneyUtils;

/**
 * Created by android on 15-7-22.
 */
public class ResourceAdapter {
    @BindingAdapter("android:text")
    public static void spanText(TextView iv, CharSequence text) {
        iv.setText(text, TextView.BufferType.SPANNABLE);
    }

    @BindingAdapter("android:text")
    public static void spanText(TextView iv, double text) {
        iv.setText(MoneyUtils.format(text));
    }

    @BindingAdapter("android:text")
    public static void text(TextView tv, Date time) {
        Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(time.getTime());
        int createYear = ca.get(Calendar.YEAR);
        int curYear = Calendar.getInstance().get(Calendar.YEAR);
        if (createYear != curYear) {
            text(tv, time, "yyyy-MM-dd");
        } else {
            text(tv, time, "MM-dd");
        }

    }

    @BindingAdapter({"android:text", "dateFormat"})
    public static void text(TextView tv, Date time, String format) {
        SimpleDateFormat fmt = new SimpleDateFormat(format, Locale.getDefault());
        fmt.setTimeZone(TimeZone.getDefault());
        tv.setText(fmt.format(time));
    }
}
