package maimeng.yodian.app.client.android.entry.skillhome;

import maimeng.yodian.app.client.android.model.skill.Skill;

public class ItemViewEntry extends ViewEntry {
    public final Skill skill;

    public ItemViewEntry(Skill skill) {
        super(VIEW_TYPE_ITEM);
        this.skill = skill;
    }
}
