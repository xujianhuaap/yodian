package maimeng.yodian.app.client.android.entry.skillseletor;


import java.util.List;

import maimeng.yodian.app.client.android.network.response.SkillResponse;

public class BannerViewEntry extends ViewEntry {
    public final List<SkillResponse.DataNode.Banner> banners;

    public BannerViewEntry(List<SkillResponse.DataNode.Banner> banner) {
        super(VIEW_TYPE_BANNER);
        this.banners = banner;
    }

}
