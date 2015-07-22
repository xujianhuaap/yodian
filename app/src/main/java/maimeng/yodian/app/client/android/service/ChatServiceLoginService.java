package maimeng.yodian.app.client.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;

import maimeng.yodian.app.client.android.model.User;
import maimeng.yodian.app.client.android.utils.LogUtil;

/**
 * Created by android on 2015/7/22.
 */
public class ChatServiceLoginService extends Service implements EMCallBack {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        EMChatManager.getInstance().logout();
        EMChatManager.getInstance().login(User.read(this).getChatLoginName(),"hx123456",this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onSuccess() {
        LogUtil.d("ChatServiceLoginService", "登陆聊天服务器成功！");
        EMChatManager.getInstance().loadAllConversations();
    }

    @Override
    public void onProgress(int progress, String status) {

    }

    @Override
    public void onError(int code, String message) {
        LogUtil.d("ChatServiceLoginService", "登陆聊天服务器失败！");
    }
}
