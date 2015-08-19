package maimeng.yodian.app.client.android.view.skill.proxy;


import android.content.Intent;

import com.melnykov.fab.FloatingActionButton;

/**
 * Created by android on 15-7-13.
 */
public interface ActivityProxy {
    int REQUEST_AUTH = 0x8001;

    void init();


    void onTitleChanged(CharSequence title, int color);

    void show(final FloatingActionButton button);

    void show(final FloatingActionButton button, boolean anima);

    void hide(final FloatingActionButton button);

    void hide(final FloatingActionButton button, boolean anima);

    boolean isShow();

    /**
     * 发起网络请求
     */
    void syncRequest();

    void reset();

    void onActivityResult(int requestCode, int resultCode, Intent data);
}
