package maimeng.yodian.app.client.android.entry.skillseletor;

public abstract class ViewEntry {
    public final static int VIEW_TYPE_ITEM=1;
    public final static int VIEW_TYPE_BANNER=2;
    public final static int VIEW_TYPE_HEAD=3;
    public final int viewType;
    public ViewEntry(int viewType){
        this.viewType=viewType;
    }
}
