package maimeng.yodian.app.client.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import maimeng.yodian.app.client.android.chat.AsyncContactService;
import maimeng.yodian.app.client.android.chat.Constant;
import maimeng.yodian.app.client.android.chat.DemoApplication;
import maimeng.yodian.app.client.android.chat.DemoHXSDKHelper;
import maimeng.yodian.app.client.android.chat.db.UserDao;
import maimeng.yodian.app.client.android.chat.domain.RobotUser;
import maimeng.yodian.app.client.android.model.User;
import maimeng.yodian.app.client.android.utils.LogUtil;

/**
 * Created by android on 2015/7/22.
 */
public class ChatServiceLoginService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        EMChatManager.getInstance().logout();
//        EMChatManager.getInstance().login(User.read(this).getChatLoginName(), "hx123456", this);


        final User read = User.read(this);
        final String currentUsername = read.getChatLoginName();
        final String currentPassword = "hx123456";
        if (TextUtils.isEmpty(currentUsername)) {
            return super.onStartCommand(intent, flags, startId);
        }
        EMChatManager.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

            @Override
            public void onSuccess() {
                // 登陆成功，保存用户名密码
                DemoApplication.getInstance().setUserName(currentUsername);
                try {

                    UserDao userDao = new UserDao(ChatServiceLoginService.this);
                    RobotUser robotUser = new RobotUser();
                    maimeng.yodian.app.client.android.chat.domain.User user = new maimeng.yodian.app.client.android.chat.domain.User();
                    robotUser.setAvatar(read.getAvatar());
                    robotUser.setId(read.getUid() + "");
                    robotUser.setUsername(read.getChatLoginName());
                    robotUser.setNick(read.getNickname());

                    user.setAvatar(read.getAvatar());
                    user.setId(read.getUid() + "");
                    user.setUsername(read.getChatLoginName());
                    user.setNick(read.getNickname());
                    userDao.saveOrUpdate(robotUser);
                    userDao.saveOrUpdate(user);


                    // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                    // ** manually load all local groups and
                    EMGroupManager.getInstance().loadAllGroups();
                    EMChatManager.getInstance().loadAllConversations(new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            System.out.println("onSuccess");
                        }

                        @Override
                        public void onError(int i, String s) {
                            System.out.println("onError");
                        }

                        @Override
                        public void onProgress(int i, String s) {
                            System.out.println("onProgress");
                        }
                    });
                    EMChatManager.getInstance().updateCurrentUserNick(User.read(ChatServiceLoginService.this).getNickname());
                    // 处理好友和群组
                    startService(new Intent(ChatServiceLoginService.this, AsyncContactService.class));
                } catch (Exception e) {
                    e.printStackTrace();
                    DemoApplication.getInstance().logout(null);
                    log(maimeng.yodian.app.client.android.R.string.login_failure_failed);
                    return;
                }
                // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(
                        DemoApplication.currentUserNick.trim());
                if (!updatenick) {
                    Log.i("ChatServiceLoginService", "update current user nick fail");
                }
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(final int code, final String message) {
                log(maimeng.yodian.app.client.android.R.string.login_failure_failed, message);
            }
        });


        return super.onStartCommand(intent, flags, startId);
    }


    void log(int resId, String messasge) {
        LogUtil.d("ChatServiceLoginService", getResources().getString(resId) + messasge);
    }

    void log(int resId) {
        LogUtil.d("ChatServiceLoginService", getResources().getString(resId));
    }
}
