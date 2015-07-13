package maimeng.yodian.app.client.android;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.umeng.analytics.MobclickAgent;

import maimeng.yodian.app.client.android.view.AbstractActivity;


public class MainActivity extends AbstractActivity  {
    private ActivityProxyController controller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main, false);
        final MainListProxy mListProxy = new MainListProxy(this, findViewById(R.id.list_root));
        final MainHomeProxy mHomeProxy = new MainHomeProxy(this, findViewById(R.id.home_root));
        controller=new ActivityProxyController(mListProxy,mHomeProxy);
        final FloatingActionButton floatButton = (FloatingActionButton)findViewById(R.id.btn_float);
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mListProxy.isInited())mListProxy.init();
                if(!mHomeProxy.isInited())mHomeProxy.init();
                controller.onFloatClick((FloatingActionButton) v);
            }
        });
        mListProxy.init();
    }
    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        controller.onTitleChanged(title,color);
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
