package maimeng.yodian.app.client.android;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import maimeng.yodian.app.client.android.view.AbstractActivity;


public class MainActivity extends AbstractActivity  {
    private ActivityProxyController controller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main, false);
        MainListProxy mListProxy = new MainListProxy(this, findViewById(R.id.list_root));
        MainHomeProxy mHomeProxy = new MainHomeProxy(this, findViewById(R.id.home_root));
        controller=new ActivityProxyController(mListProxy,mHomeProxy);
        final FloatingActionButton floatButton = (FloatingActionButton)findViewById(R.id.btn_float);
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onFloatClick((FloatingActionButton) v);
            }
        });
        setTitle("优点精选");
    }
    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        controller.onTitleChanged(title,color);
    }

}
