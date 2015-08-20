package maimeng.yodian.app.client.android.network.response;

import java.util.List;

import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.model.Rmark;

/**
 * Created by android on 2015/7/28.
 */
public class RmarkListResponse extends Response {
    private DataNode data;

    public DataNode getData() {
        return data;
    }

    public void setData(DataNode data) {
        this.data = data;
    }

    public class DataNode {
        private List<Rmark> list;
        private Skill detail;

        public Skill getDetail() {
            return detail;
        }

        public void setDetail(Skill detail) {
            this.detail = detail;
        }

        public List<Rmark> getList() {
            return list;
        }

        public void setList(List<Rmark> list) {
            this.list = list;
        }
    }
}
