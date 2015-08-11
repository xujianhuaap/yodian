package maimeng.yodian.app.client.android.network.response;

import java.util.ArrayList;
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
        private ArrayList<Theme>category;

        public ArrayList<Theme> getCategory() {
            return category;
        }

        public void setCategory(ArrayList<Theme> category) {
            this.category = category;
        }

        public List<Skill> getList() {
            return list;
        }

        public void setList(List<Skill> list) {
            this.list = list;
        }

        private List<Skill> list;
    }
}
