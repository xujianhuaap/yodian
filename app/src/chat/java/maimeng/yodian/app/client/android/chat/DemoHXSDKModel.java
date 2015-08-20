/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package maimeng.yodian.app.client.android.chat;

import android.content.Context;

import com.easemob.applib.model.DefaultHXSDKModel;

import java.util.Map;

import maimeng.yodian.app.client.android.chat.db.DemoDBManager;
import maimeng.yodian.app.client.android.chat.db.UserDao;
import maimeng.yodian.app.client.android.chat.domain.RobotUser;
import maimeng.yodian.app.client.android.chat.domain.User;

public class DemoHXSDKModel extends DefaultHXSDKModel{

    public DemoHXSDKModel(Context ctx) {
        super(ctx);
    }

    // demo will not use HuanXin roster
    public boolean getUseHXRoster() {
        return false;
    }
    
    // demo will switch on debug mode
    public boolean isDebugMode(){
        return true;
    }
//
//    public boolean saveContactList(List<User> contactList) {
//        UserDao dao = new UserDao(context);
//        dao.saveContactList(contactList);
//        return true;
//    }

    public Map<String, User> getContactList() {
        UserDao dao = new UserDao(context);
        return dao.getContactList();
    }
    
    public Map<String, RobotUser> getRobotList(){
    	UserDao dao = new UserDao(context);
    	return dao.getRobotUser();
    }
//
//    public boolean saveRobotList(List<RobotUser> robotList){
//    	UserDao dao = new UserDao(context);
//    	dao.saveRobotUser(robotList);
//    	return true;
//    }
    
    public void closeDB() {
        DemoDBManager.getInstance().closeDB();
    }
    
    @Override
    public String getAppProcessName() {
        return context.getPackageName();
    }
}