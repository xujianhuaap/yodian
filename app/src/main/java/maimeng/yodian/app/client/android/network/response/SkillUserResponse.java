package maimeng.yodian.app.client.android.network.response;

import java.util.List;

import maimeng.yodian.app.client.android.model.Skill;
import maimeng.yodian.app.client.android.model.Theme;
import maimeng.yodian.app.client.android.model.User;


/**
 * Created by android on 15-7-14.
 */
public class SkillUserResponse extends Response {
    private DataNode data;


    public DataNode getData() {
        return data;
    }

    public void setData(DataNode data) {
        this.data = data;
    }

    public class DataNode {

        public List<Skill> getList() {
            return list;
        }

        public void setList(List<Skill> list) {
            this.list = list;
        }

        private List<Skill> list;
        private List<Theme> category;

        public List<Theme> getCategory() {
            return category;
        }

        public void setCategory(List<Theme> category) {
            this.category = category;
        }

        private User user;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }


        public class Head {
            private long value;
            private String pic;

            public long getValue() {
                return value;
            }

            public void setValue(int value) {
                this.value = value;
            }

            public String getPic() {
                return pic;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }
        }

    }
}
