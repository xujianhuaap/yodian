package maimeng.yodian.app.client.android.view.proxy;


import com.melnykov.fab.FloatingActionButton;

/**
 * Created by android on 15-7-13.
 */
public interface ActivityProxy {
    void init();
    void onTitleChanged(CharSequence title, int color);
    void show(FloatingActionButton button);
    void hide(FloatingActionButton button);
    boolean isShow();
}
