/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package maimeng.yodian.app.client.android.chat.domain;

import com.easemob.chat.EMContact;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;

public class User extends EMContact {
    private int unreadMsgCount;
    private String header;
    private String avatar;

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public User(String qq, String mobile, String wechat) {
        this.qq = qq;
        this.mobile = mobile;
        this.wechat = wechat;
    }

    private String wechat = "";
    private String mobile = "";
    private String qq = "";
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public int getUnreadMsgCount() {
        return unreadMsgCount;
    }

    public void setUnreadMsgCount(int unreadMsgCount) {
        this.unreadMsgCount = unreadMsgCount;
    }


    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public int hashCode() {
        return 17 * getUsername().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof User)) {
            return false;
        }
        return getUsername().equals(((User) o).getUsername());
    }

    @Override
    public String toString() {
        return nick == null ? username : nick;
    }


    public static User parse(EMMessage message) {
        final User user = new User();
        String userName = message.getFrom();
        String nickname = null;
        try {
            nickname = message.getStringAttribute("nickName");
        } catch (EaseMobException e) {
//            e.printStackTrace();
        }
        String avatar = null;
        try {
            avatar = message.getStringAttribute("avatar");
        } catch (EaseMobException e) {
//            e.printStackTrace();
        }
        long uid = Long.parseLong(message.getStringAttribute("uid", "0"));
        String wechat = "";
        String mobile = "";
        String qq = "";
        try {
            wechat = message.getStringAttribute("weChat");
            mobile = message.getStringAttribute("mobile");
            qq = message.getStringAttribute("qq");
        } catch (EaseMobException e) {
//            e.printStackTrace();
        }
        user.setAvatar(avatar);
        user.setId(uid);
        user.setNick(nickname);
        user.setUsername(userName);
        user.setWechat(wechat);
        user.setMobile(mobile);
        user.setQq(qq);
        return user;
    }
}
