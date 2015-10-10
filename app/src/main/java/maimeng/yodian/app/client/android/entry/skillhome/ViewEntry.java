package maimeng.yodian.app.client.android.entry.skillhome;

import maimeng.yodian.app.client.android.entry.BaseVIewEntry;

public abstract class ViewEntry extends BaseVIewEntry {
    public final static int VIEW_TYPE_BANNER=2;
    public final static int VIEW_TYPE_HEAD=3;

    public ViewEntry(int viewType) {
        super(viewType);
    }
}
