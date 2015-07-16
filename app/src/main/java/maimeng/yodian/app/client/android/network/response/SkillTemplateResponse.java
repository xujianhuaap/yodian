package maimeng.yodian.app.client.android.network.response;

import java.util.List;

import maimeng.yodian.app.client.android.model.SkillTemplate;

public class SkillTemplateResponse extends Response {
    private DataNode data;

    public DataNode getData() {
        return data;
    }

    public void setData(DataNode data) {
        this.data = data;
    }
    public class DataNode{
        private List<SkillTemplate> list;

        public List<SkillTemplate> getList() {
            return list;
        }

        public void setList(List<SkillTemplate> list) {
            this.list = list;
        }
    }
}
