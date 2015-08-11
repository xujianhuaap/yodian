package maimeng.yodian.app.client.android.entry.skillseletor;


import maimeng.yodian.app.client.android.network.response.SkillResponse;

public class HeadViewEntry extends ViewEntry {
    public final SkillResponse.DataNode.Head skill;
    public final SkillResponse.DataNode.Head user;

    public HeadViewEntry(SkillResponse.DataNode.Head skill,SkillResponse.DataNode.Head user) {
        super(VIEW_TYPE_HEAD);
        this.skill=skill;
        this.user=user;
    }
}
