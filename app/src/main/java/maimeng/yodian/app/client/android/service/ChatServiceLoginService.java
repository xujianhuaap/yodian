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
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import maimeng.yodian.app.client.android.chat.Constant;
import maimeng.yodian.app.client.android.chat.DemoApplication;
import maimeng.yodian.app.client.android.chat.DemoHXSDKHelper;
import maimeng.yodian.app.client.android.chat.activity.MainActivity;
import maimeng.yodian.app.client.android.chat.db.UserDao;
import maimeng.yodian.app.client.android.chat.domain.RobotUser;
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
                    // 处理好友和群组
                    initializeContacts();
                    updateContacts();
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

    private void updateContacts(){
            EMChatManager.getInstance().loadAllConversations();
            Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
            Map<String, RobotUser> mMap = new HashMap<String, RobotUser>();
            Map<String,  maimeng.yodian.app.client.android.chat.domain.User> mMapUser = new HashMap<String,  maimeng.yodian.app.client.android.chat.domain.User>();
            for(EMConversation conversation:conversations.values()){
                if(conversation.getAllMsgCount()>0){
                    List<EMMessage> messages = conversation.loadMoreMsgFromDB(conversation.getMessage(0).getMsgId(), conversation.getAllMsgCount());
                    for(EMMessage message:messages){
                        try {
                            String userName = message.getFrom();
                            String nickname=message.getStringAttribute("nickName");
                            String avatar=message.getStringAttribute("avatar");
                            String uid = message.getStringAttribute("uid");
                            RobotUser robot = new RobotUser();
                            maimeng.yodian.app.client.android.chat.domain.User user=new maimeng.yodian.app.client.android.chat.domain.User();
                            robot.setAvatar(avatar);
                            robot.setId(uid);
                            robot.setNick(nickname);
                            robot.setUsername(userName);


                            user.setAvatar(avatar);
                            user.setId(uid);
                            user.setNick(nickname);
                            user.setUsername(userName);

                            mMap.put(userName, robot);
                            mMapUser.put(userName, user);
                            LogUtil.i(ChatServiceLoginService.class.getName(),"refresh Contacts by %s,nickname:%s,avatar:%s,uid:%s",userName,nickname,avatar,uid);
                        } catch (EaseMobException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            // 存入内存
            ((DemoHXSDKHelper) HXSDKHelper.getInstance()).setRobotList(mMap);
        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).setContactList(mMapUser);
            // 存入db
            UserDao dao = new UserDao(this);
            dao.saveRobotUser(new ArrayList<>(mMap.values()));
            dao.saveContactList(new ArrayList<>(mMapUser.values()));
//		asyncGetRobotNamesFromServer(new EMValueCallBack<List<EMContact>>() {
//
//			@Override
//			public void onSuccess(final List<EMContact> value) {
//				runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						progressBar.setVisibility(View.GONE);
//						swipeRefreshLayout.setRefreshing(false);
//						Map<String, RobotUser> mMap = new HashMap<String, RobotUser>();
//						for (EMContact item : value) {
//							RobotUser user = new RobotUser();
//							user.setUsername(item.getUsername());
//							user.setNick(item.getNick());
//							user.setHeader("#");
//							mMap.put(item.getUsername(), user);
//						}
//						robotList.clear();
//						robotList.addAll(mMap.values());
//						// 存入内存
//						((DemoHXSDKHelper) HXSDKHelper.getInstance()).setRobotList(mMap);
//						// 存入db
//						UserDao dao = new UserDao(RobotsActivity.this);
//						dao.saveRobotUser(robotList);
//						adapter.notifyDataSetChanged();
//					}
//				});
//			}
//
//			@Override
//			public void onError(int error, String errorMsg) {
//				runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						swipeRefreshLayout.setRefreshing(false);
//						progressBar.setVisibility(View.GONE);
//					}
//				});
//			}
//		});

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
