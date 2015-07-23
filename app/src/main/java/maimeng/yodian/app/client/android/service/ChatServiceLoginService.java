package maimeng.yodian.app.client.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import maimeng.yodian.app.client.android.chat.Constant;
import maimeng.yodian.app.client.android.chat.DemoApplication;
import maimeng.yodian.app.client.android.chat.activity.MainActivity;
import maimeng.yodian.app.client.android.chat.db.UserDao;
import maimeng.yodian.app.client.android.model.User;
import maimeng.yodian.app.client.android.utils.LogUtil;

/**
 * Created by android on 2015/7/22.
 */
public class ChatServiceLoginService extends Service{
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        EMChatManager.getInstance().logout();
//        EMChatManager.getInstance().login(User.read(this).getChatLoginName(), "hx123456", this);


        final String currentUsername=User.read(this).getChatLoginName();
        final String currentPassword="hx123456";
        EMChatManager.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

            @Override
            public void onSuccess() {
                // 登陆成功，保存用户名密码
                DemoApplication.getInstance().setUserName(currentUsername);
                DemoApplication.getInstance().setPassword(currentPassword);

                try {
                    // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                    // ** manually load all local groups and
                    EMGroupManager.getInstance().loadAllGroups();
                    EMChatManager.getInstance().loadAllConversations();
                    // 处理好友和群组
                    initializeContacts();
                } catch (Exception e) {
                    e.printStackTrace();
                    log(maimeng.yodian.app.client.android.chat.R.string.login_failure_failed);
                    return;
                }
                // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(
                        DemoApplication.currentUserNick.trim());
                if (!updatenick) {
                    Log.e("LoginActivity", "update current user nick fail");
                }
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(final int code, final String message) {
                log(maimeng.yodian.app.client.android.chat.R.string.login_failure_failed,message);
            }
        });



        return super.onStartCommand(intent, flags, startId);
    }

    private void initializeContacts() {
        Map<String, maimeng.yodian.app.client.android.chat.domain.User> userlist = new HashMap<String, maimeng.yodian.app.client.android.chat.domain.User>();
        // 添加user"申请与通知"
        maimeng.yodian.app.client.android.chat.domain.User newFriends = new maimeng.yodian.app.client.android.chat.domain.User();
        newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
        String strChat = getResources().getString(
                maimeng.yodian.app.client.android.chat.R.string.Application_and_notify);
        newFriends.setNick(strChat);

        userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
        // 添加"群聊"
        maimeng.yodian.app.client.android.chat.domain.User groupUser = new maimeng.yodian.app.client.android.chat.domain.User();
        String strGroup = getResources().getString(maimeng.yodian.app.client.android.chat.R.string.group_chat);
        groupUser.setUsername(Constant.GROUP_USERNAME);
        groupUser.setNick(strGroup);
        groupUser.setHeader("");
        userlist.put(Constant.GROUP_USERNAME, groupUser);

        // 添加"Robot"
        maimeng.yodian.app.client.android.chat.domain.User robotUser = new maimeng.yodian.app.client.android.chat.domain.User();
        String strRobot = getResources().getString(maimeng.yodian.app.client.android.chat.R.string.robot_chat);
        robotUser.setUsername(Constant.CHAT_ROBOT);
        robotUser.setNick(strRobot);
        robotUser.setHeader("");
        userlist.put(Constant.CHAT_ROBOT, robotUser);

        // 存入内存
        DemoApplication.getInstance().setContactList(userlist);
        // 存入db
        UserDao dao = new UserDao(this);
        List<maimeng.yodian.app.client.android.chat.domain.User> users = new ArrayList<maimeng.yodian.app.client.android.chat.domain.User>(userlist.values());
        dao.saveContactList(users);
    }
    void log(int resId,String messasge){
        LogUtil.d("ChatServiceLoginService",getResources().getString(resId)+messasge);
    }
    void log(int resId){
        LogUtil.d("ChatServiceLoginService",getResources().getString(resId));
    }
}
