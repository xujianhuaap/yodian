package maimeng.yodian.app.client.android.entry.skillseletor;

import maimeng.yodian.app.client.android.model.User;

/**
 * Created by xujianhua on 9/14/15.
 */
public class HeaderViewEntry extends ViewEntry{
    public User user;

    public HeaderViewEntry( User user) {
        super(ViewEntry.VIEW_TYPE_HEAD);
        this.user = user;
    }
}
