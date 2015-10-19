package maimeng.yodian.app.client.android.network.response;

import maimeng.yodian.app.client.android.model.user.User;

/**
 * Created by android on 2015/8/17.
 */
public class UserInfoResponse extends Response {
    public  DataNode data;


    public DataNode getData() {
        return data;
    }

    public void setData(DataNode data) {
        this.data = data;
    }

    public final class DataNode{
        private User.Info user;

        public User.Info getUser() {
            return user;
        }

        public void setUser(User.Info user) {
            this.user = user;
        }
    }
}
