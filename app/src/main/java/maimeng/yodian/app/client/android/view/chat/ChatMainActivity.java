package maimeng.yodian.app.client.android.view.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.umeng.analytics.MobclickAgent;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.chat.AsyncContactService;
import maimeng.yodian.app.client.android.common.UEvent;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mini = true;
        MobclickAgent.onEvent(this, UEvent.CONVERSATIONLIST);
        startService(new Intent(this, AsyncContactService.class));
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_go_back);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}