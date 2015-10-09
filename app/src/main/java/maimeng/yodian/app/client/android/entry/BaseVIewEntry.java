package maimeng.yodian.app.client.android.entry;

/**
 * Created by android on 15-10-9.
 */
public abstract class BaseVIewEntry {
    public final static int VIEW_TYPE_ITEM=1;
    public final int viewType;
    public BaseVIewEntry(int viewType){
        this.viewType=viewType;
    }
}
