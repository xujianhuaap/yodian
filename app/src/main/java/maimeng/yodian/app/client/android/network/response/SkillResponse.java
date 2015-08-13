package maimeng.yodian.app.client.android.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import maimeng.yodian.app.client.android.common.model.Skill;
import maimeng.yodian.app.client.android.model.Theme;


/**
 * Created by android on 15-7-14.
 */
public class SkillResponse extends Response {
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
        private List<Banner> banner;
        private List<Theme> category;

        public List<Theme> getCategory() {
            return category;
        }

        public void setCategory(List<Theme> category) {
            this.category = category;
        }

        @SerializedName("skill")
        private Head headSkill;

        public Head getHeadUser() {
            return headUser;
        }

        public void setHeadUser(Head headUser) {
            this.headUser = headUser;
        }

        public Head getHeadSkill() {
            return headSkill;
        }

        public void setHeadSkill(Head headSkill) {
            this.headSkill = headSkill;
        }

        @SerializedName("user")

        private Head headUser;

        public List<Banner> getBanner() {
            return banner;
        }

        public void setBanner(List<Banner> banner) {
            this.banner = banner;
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

        public class Banner {
            /**
             * 1:网址;2用户;3技能
             */

            private int type;
            /**
             * 1的时候是网址，2或3的时候是id(long)
             */

            private String value;
            private String pic;

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
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
