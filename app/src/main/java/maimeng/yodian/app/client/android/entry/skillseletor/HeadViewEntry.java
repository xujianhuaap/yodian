package maimeng.yodian.app.client.android.entry.skillseletor;


import maimeng.yodian.app.client.android.model.skill.Head;

public class HeadViewEntry extends ViewEntry {
    public final Head skill;
    public final Head user;

    public HeadViewEntry(Head skill, Head user) {
        super(VIEW_TYPE_HEAD);
        this.skill = skill;
        this.user = user;
    }
}
