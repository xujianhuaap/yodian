package maimeng.yodian.app.client.android.network.response;

import maimeng.yodian.app.client.android.model.skill.Skill;

/**
 * Created by android on 2015/8/18.
 */
public class SkillAllResponse extends Response {
    private Skill data;

    public Skill getData() {
        return data;
    }

    public void setData(Skill data) {
        this.data = data;
    }
}
