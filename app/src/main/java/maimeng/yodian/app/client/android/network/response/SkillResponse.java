package maimeng.yodian.app.client.android.network.response;

import java.util.List;

import maimeng.yodian.app.client.android.model.Skill;

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

   public class DataNode{
        public List<Skill> getList() {
            return list;
        }

        public void setList(List<Skill> list) {
            this.list = list;
        }

        private List<Skill> list;
    }
}
