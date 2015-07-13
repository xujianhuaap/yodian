package maimeng.yodian.app.client.android;

import android.support.design.widget.FloatingActionButton;

/**
 * Created by android on 15-7-13.
 */
public interface ActivityProxy {
    void onTitleChanged(CharSequence title, int color);
    void show(FloatingActionButton button);
    void hide(FloatingActionButton button);
    boolean isShow();
}
