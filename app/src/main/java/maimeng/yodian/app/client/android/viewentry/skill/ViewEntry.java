package maimeng.yodian.app.client.android.viewentry.skill;

public abstract class ViewEntry {
    public final static int VIEW_TYPE_ITEM=1;
    public final static int VIEW_TYPE_ADDBUTTON=2;
    public final int viewType;
    public ViewEntry(int viewType){
        this.viewType=viewType;
    }
}
