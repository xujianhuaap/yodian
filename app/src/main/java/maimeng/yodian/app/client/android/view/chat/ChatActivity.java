package maimeng.yodian.app.client.android.view.chat;

import android.content.Intent;
import android.os.Bundle;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;

import maimeng.yodian.app.client.android.view.AbstractActivity;

/**
 * Created by android on 15-7-22.
 */
public class ChatActivity extends AbstractActivity {
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username = ChatIntent.getUserName(getIntent());
        setTitle(username);
        EMConversation conversation = EMChatManager.getInstance().getConversationByType(username, EMConversation.EMConversationType.Chat);
        conversation.markAllMessagesAsRead();
    }

    static class ChatIntent extends Intent{
        static final String KEY_USERNAME="_username";
        public ChatIntent(String username){
            putExtra(KEY_USERNAME,username);
        }
        public static String getUserName(Intent intent){
            return intent.getStringExtra(ChatIntent.KEY_USERNAME);
        }
    }
}
