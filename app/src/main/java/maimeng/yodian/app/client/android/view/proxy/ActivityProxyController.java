package maimeng.yodian.app.client.android.view.proxy;

import android.support.design.widget.FloatingActionButton;

/**
 * Created by android on 15-7-13.
 */
public class ActivityProxyController {
    private final MainListProxy mListProxy;
    private final MainHomeProxy mHomeProxy;

    public void onFloatClick(FloatingActionButton v) {
        if(mListProxy.isShow()){
            mListProxy.hide(v);
        }else{
            mListProxy.show(v);
        }
    }
    public ActivityProxyController(MainListProxy mListProxy, MainHomeProxy mHomeProxy) {
        this.mListProxy=mListProxy;
        this.mHomeProxy=mHomeProxy;
    }

    public void onTitleChanged(CharSequence title, int color) {
        mHomeProxy.onTitleChanged(title,color);
        mListProxy.onTitleChanged(title,color);
    }
}

