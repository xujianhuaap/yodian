package maimeng.yodian.app.client.android.view.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.easemob.chat.EMMessage;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.common.view.ContactDialog;

/**
 * Created by android on 8/17/15.
 */
public class ChatActivity extends maimeng.yodian.app.client.android.chat.activity.ChatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.menu_chat_clear);
        item.setVisible(false);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0,1001,0,"").setIcon(R.drawable.btn_ic_contact).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId ==1001){
            startActivity(new Intent(this,ContactDialog.class).putExtra("wechat",getSkill().getWeichat()));
        }else if(itemId ==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewMessage(EMMessage message) {

    }
}
