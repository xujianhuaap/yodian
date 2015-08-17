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

	private String wechat;
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public User(){}
	
	public User(String username){
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


	public static User parse(EMMessage message) throws EaseMobException {
		final User user = new User();
		String userName = message.getFrom();
		String nickname=message.getStringAttribute("nickName");
		String avatar=message.getStringAttribute("avatar");
		String uid = message.getStringAttribute("uid");
		String wechat=message.getStringAttribute("weChat");
		user.setAvatar(avatar);
		user.setId(uid);
		user.setNick(nickname);
		user.setUsername(userName);
		user.setWechat(wechat);
		return user;
	}
}
