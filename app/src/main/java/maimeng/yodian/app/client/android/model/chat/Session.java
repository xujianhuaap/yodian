package maimeng.yodian.app.client.android.model.chat;

import android.content.Context;
import android.text.TextUtils;

import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;

import java.util.Date;

import maimeng.yodian.app.client.android.utils.CommonUtils;
import maimeng.yodian.app.client.android.utils.DateUtils;
import maimeng.yodian.app.client.android.utils.SmileUtils;

/**
 * Created by android on 2015/7/22.
 */
public class Session {
    private final EMConversation conversation;
    private final Context mContext;
    private String username;//用户名

    public String getUsername() {
        return username;
    }

    private String nickname;//昵称
    private String lastDatetime;
    private String avatar;
    private CharSequence lastContent;
    private int missCount=0;//未读消息数
    private int msgCount=0;//消息数


    public String getAvatar() {
        return avatar;
    }

    private boolean sendFaild=false;
    public boolean isSendFaild() {
        return sendFaild;
    }

    public EMConversation getConversation() {
        return conversation;
    }

    public int getMissCount() {
        return missCount;
    }

    public int getMsgCount() {
        return msgCount;
    }

    public String getNickname() {
        return nickname;
    }

    public CharSequence getLastContent() {
        return lastContent;
    }

    public String getLastDatetime() {
        return lastDatetime;
    }
    public Session(Context context,EMConversation conversation){
        this.mContext=context;
        this.conversation=conversation;
        this.username=conversation.getUserName();
        EMMessage lastMessage = conversation.getLastMessage();
        lastContent=SmileUtils.getSmiledText(mContext, CommonUtils.getMessageDigest(lastMessage, mContext));
        missCount=conversation.getUnreadMsgCount();
        msgCount=conversation.getMsgCount();
        lastDatetime= DateUtils.getTimestampString(mContext, new Date(lastMessage.getMsgTime()));
        sendFaild=lastMessage.direct == EMMessage.Direct.SEND && lastMessage.status == EMMessage.Status.FAIL;
        try {
            String nickname = lastMessage.getStringAttribute("nickName");
            if (!TextUtils.isEmpty(nickname)) {
                this.nickname=nickname;
            }else{
                this.nickname=lastMessage.getUserName();
            }
        } catch (EaseMobException e) {
            e.printStackTrace();
        }
        try {
            this.avatar = lastMessage.getStringAttribute("avatar");
        } catch (EaseMobException e) {
            e.printStackTrace();
        }
    }
}
