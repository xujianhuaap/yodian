package maimeng.yodian.app.client.android.common;

import android.content.Context;

import in.srain.cube.views.ptr.header.StoreHouseHeader;
import maimeng.yodian.app.client.android.R;

/**
 * Created by android on 2015/7/30.
 */
public class PullHeadView extends StoreHouseHeader {
    public PullHeadView(Context context) {
        super(context);
        setTextColor(0xFFFFFF);
        setPadding(0, (int) getResources().getDimension(R.dimen.pull_refresh_paddingTop), 0, 0);
        initWithString("YoDian");
    }

    public static PullHeadView create(Context context) {
        return new PullHeadView(context);
    }
}
