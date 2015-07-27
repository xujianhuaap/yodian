package maimeng.yodian.app.client.android.view.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.TextView;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.chat.AsyncContactService;
import maimeng.yodian.app.client.android.view.AbstractActivity;

/**
 * Created by android on 2015/7/22.
 */
//public class ChatMainActivity extends AbstractActivity {
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chat_main);
//        getSupportFragmentManager().beginTransaction().replace(R.id.root,new ChatSessionListFragment()).commit();
//    }
//}
public class ChatMainActivity extends maimeng.yodian.app.client.android.chat.activity.MainActivity {
//    private FrameLayout mContent;
//    private TextView mTitle;
//    protected Toolbar mToolBar;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        mini=true;
//        super.onCreate(savedInstanceState);
//    }
//    @Override
//    public void setContentView(int layoutResID) {
//        super.setContentView(R.layout.activity_base);
//        mContent=(FrameLayout)findViewById(R.id.base_content);
//        mTitle=(TextView)findViewById(R.id.base_title);
//        mToolBar = (Toolbar) findViewById(R.id.toolbar);
//        if(mToolBar!=null) {
//            mToolBar.setTitle("");
//            setSupportActionBar(mToolBar);
//
//        }
//        getLayoutInflater().inflate(layoutResID, mContent, true);
//    }
//    @Override
//    protected void onTitleChanged(CharSequence title, int color) {
//        super.onTitleChanged(title, color);
//        if(mTitle!=null) mTitle.setText(title);
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mini=true;
        startService(new Intent(this, AsyncContactService.class));
        super.onCreate(savedInstanceState);
    }
}