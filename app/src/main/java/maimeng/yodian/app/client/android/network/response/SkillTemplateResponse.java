package maimeng.yodian.app.client.android.network.response;

import java.util.List;

import maimeng.yodian.app.client.android.model.SkillTemplate;

public class SkillTemplateResponse extends Response {
    private List<SkillTemplate> data;

    public List<SkillTemplate> getData() {
        return data;
    }

    public void setData(List<SkillTemplate> data) {
        this.data = data;
    }
}
