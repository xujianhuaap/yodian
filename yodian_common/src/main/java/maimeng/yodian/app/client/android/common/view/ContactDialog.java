package maimeng.yodian.app.client.android.common.view;

import android.animation.ArgbEvaluator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import maimeng.yodian.app.client.android.common.R;
import pl.droidsonroids.gif.GifImageView;


/**
 * Created by android on 2015/8/7.
 */
public class ContactDialog extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat_Dialog);
        super.onCreate(savedInstanceState);
        GifImageView view = new GifImageView(this);
        setContentView(view);
        view.setImageResource(R.drawable.weixin);
    }
}
