package maimeng.yodian.app.client.android.chat;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import maimeng.yodian.app.client.android.BuildConfig;
import maimeng.yodian.app.client.android.YApplication;
import maimeng.yodian.app.client.android.chat.db.UserDao;
import maimeng.yodian.app.client.android.chat.domain.RobotUser;
import maimeng.yodian.app.client.android.chat.domain.User;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.StringResponse;
import maimeng.yodian.app.client.android.network.service.ChatService;
import maimeng.yodian.app.client.android.utils.LogUtil;

/**
 * Created by android on 2015/7/27.
 */
public class AsyncContactService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initializeContacts();
        updateContacts();
        return super.onStartCommand(intent, flags, startId);
    }
    private void updateContacts(){
        EMChatManager instance = EMChatManager.getInstance();
        if(!instance.isConnected()){
            return;
        }
        instance.loadAllConversations();
        Hashtable<String, EMConversation> conversations = instance.getAllConversations();
        UserDao dao = new UserDao(this);
        for(EMConversation conversation:conversations.values()){
            if(!YApplication.getInstance().isHasAdmin()&& "hx_admin".equals(conversation.getUserName())){
                YApplication.getInstance().setHasAdmin(true);
                getSharedPreferences("chat", Context.MODE_PRIVATE).edit().putBoolean("hasAdmin", true).apply();
            }
            if(conversation.getAllMsgCount()>0){
                List<EMMessage> messages = conversation.loadMoreMsgFromDB(conversation.getMessage(0).getMsgId(), conversation.getAllMsgCount());
                for(EMMessage message:messages){
                    try {
                        RobotUser robot = RobotUser.parse(message);
                        maimeng.yodian.app.client.android.chat.domain.User user=maimeng.yodian.app.client.android.chat.domain.User.parse(message);
                        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveOrUpdate(user.getUsername(), user);
                        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveOrUpdate(robot.getUsername(), robot);
                        dao.saveOrUpdate(user);
                        dao.saveOrUpdate(robot);
                        log("refresh Contacts by %s,nickname:%s,avatar:%s,uid:%s,wechat:%s",user.getUsername(),user.getNick(),user.getAvatar(),user.getId(),user.getWechat());
                    } catch (EaseMobException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        UserDao userDao=new UserDao(this);
        Map<String, User> contactList = userDao.getContactList();
        DemoApplication.getInstance().setContactList(contactList);
        if(YApplication.getInstance().isHasAdmin()){

        }else{
            Network.getService(ChatService.class).sendService(new Callback.SimpleCallBack<maimeng.yodian.app.client.android.network.response.Response>() {
                @Override
                public void success(maimeng.yodian.app.client.android.network.response.Response res, Response response) {
                    if(response.getStatus()==200){
                        LogUtil.i(AsyncContactService.class.getName(),"Send Admin Msg Success");
                    }
                }

                @Override
                public void failure(HNetError hNetError) {
                    hNetError.printStackTrace();
                }

            });
        }

    }
    private void initializeContacts() {
        Map<String, User> userlist = new HashMap<String, User>();
        // 添加user"申请与通知"
        maimeng.yodian.app.client.android.chat.domain.User newFriends = new maimeng.yodian.app.client.android.chat.domain.User();
        newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
        String strChat = getResources().getString(
                maimeng.yodian.app.client.android.R.string.Application_and_notify);
        newFriends.setNick(strChat);

        userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
        // 添加"群聊"
        maimeng.yodian.app.client.android.chat.domain.User groupUser = new maimeng.yodian.app.client.android.chat.domain.User();
        String strGroup = getResources().getString(maimeng.yodian.app.client.android.R.string.group_chat);
        groupUser.setUsername(Constant.GROUP_USERNAME);
        groupUser.setNick(strGroup);
        groupUser.setHeader("");
        userlist.put(Constant.GROUP_USERNAME, groupUser);

        // 添加"Robot"
        maimeng.yodian.app.client.android.chat.domain.User robotUser = new maimeng.yodian.app.client.android.chat.domain.User();
        String strRobot = getResources().getString(maimeng.yodian.app.client.android.R.string.robot_chat);
        robotUser.setUsername(Constant.CHAT_ROBOT);
        robotUser.setNick(strRobot);
        robotUser.setHeader("");
        userlist.put(Constant.CHAT_ROBOT, robotUser);

        // 存入内存
        DemoApplication.getInstance().setContactList(userlist);
        // 存入db
        UserDao dao = new UserDao(this);
        List<maimeng.yodian.app.client.android.chat.domain.User> users = new ArrayList<User>(userlist.values());
        for(maimeng.yodian.app.client.android.chat.domain.User user:users){
            dao.saveOrUpdate(user);
        }
//        dao.saveContactList(users);
    }

    void log(String messasge,Object ... args){
        if(BuildConfig.DEBUG)Log.d("AsyncContactService", String.format(messasge,args));
    }
    void log(int resId,String messasge){
        if(BuildConfig.DEBUG)Log.d("AsyncContactService", getResources().getString(resId) + messasge);
    }
    void log(int resId){
        if(BuildConfig.DEBUG)Log.d("ChatServiceLoginService", getResources().getString(resId));
    }
    public static boolean isServiceRunning(Context mContext,String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(30);
        if (!(serviceList.size()>0)) {
            return false;
        }
        for (int i=0; i<serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
}
