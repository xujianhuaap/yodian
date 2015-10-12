package maimeng.yodian.app.client.android.entry.skillseletor;


import java.util.List;

import maimeng.yodian.app.client.android.model.skill.Banner;

public class BannerViewEntry extends ViewEntry {
    public final List<Banner> banners;

    public BannerViewEntry(List<Banner> banner) {
        super(VIEW_TYPE_BANNER);
        this.banners = banner;
    }

}
