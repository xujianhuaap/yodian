package maimeng.yodian.app.client.android.common;

import android.databinding.BindingAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by android on 2015/7/18.
 */
public class ResBindAdapter {
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

    @BindingAdapter({"android:text", "bind:dateFormat"})
    public static void text(TextView tv, Date time, String format) {
        SimpleDateFormat fmt = new SimpleDateFormat(format, Locale.getDefault());
        fmt.setTimeZone(TimeZone.getDefault());
        tv.setText(fmt.format(time));
    }
}
