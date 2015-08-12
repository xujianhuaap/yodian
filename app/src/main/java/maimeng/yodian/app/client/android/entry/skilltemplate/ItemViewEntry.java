package maimeng.yodian.app.client.android.entry.skilltemplate;

import maimeng.yodian.app.client.android.model.SkillTemplate;

public class ItemViewEntry extends ViewEntry {
    public final SkillTemplate template;

    public ItemViewEntry(SkillTemplate template) {
        super(VIEW_TYPE_ITEM);
        this.template=template;
    }
}
