package maimeng.yodian.app.client.android.view.skill.proxy;


import android.content.Intent;

import com.melnykov.fab.FloatingActionButton;

/**
 * Created by android on 15-7-13.
 */
public class ActivityProxyController {
    private final MainListProxy mListProxy;
    private final MainHomeProxy mHomeProxy;
    static final int REQUEST_CREATE_SKILL = 0x1003;//新建技能
    public void onFloatClick(FloatingActionButton v) {
        if(mListProxy.isShow()){
            mListProxy.hide(v);
            mHomeProxy.show(v);
        }else{
            mListProxy.show(v);
            mHomeProxy.hide(v);
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(mListProxy.isShow()){
            mListProxy.onActivityResult(requestCode,resultCode,data);
        }else{
            mHomeProxy.onActivityResult(requestCode,resultCode,data);
        }
    }
}

