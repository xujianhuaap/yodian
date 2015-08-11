package maimeng.yodian.app.client.android.entry.skillseletor;

import maimeng.yodian.app.client.android.common.model.Skill;

public class ItemViewEntry extends ViewEntry {
    public final Skill skill;

    public ItemViewEntry(Skill skill) {
        super(VIEW_TYPE_ITEM);
        this.skill=skill;
    }
}
