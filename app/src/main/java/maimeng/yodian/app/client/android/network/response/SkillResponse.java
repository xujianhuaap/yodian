package maimeng.yodian.app.client.android.network.response;

import maimeng.yodian.app.client.android.model.skill.DataNode;


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
}
