package maimeng.yodian.app.client.android.adapter;

import android.app.Fragment;
import android.content.Context;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @param <IT> 数据类型，JavaBean
 */
public abstract class AbstractListAdapter<IT> extends BaseAdapter {
    protected final Context mContext;
    private final ArrayList<IT> datas = new ArrayList<>();
    public void delete(int position) {
        this.datas.remove(position);
        notifyDataSetChanged();
    }

    public void remove(int postion){
        this.datas.remove(postion);
        notifyDataSetChanged();
    }

    public AbstractListAdapter(Context context) {
        this.mContext = context;
    }

    public AbstractListAdapter(Fragment fragment) {
        this(fragment.getActivity());
    }

    public AbstractListAdapter(android.support.v4.app.Fragment fragment) {
        this(fragment.getActivity());
    }




    public void reload(final List<IT> datas, boolean append) {
        if (!append) {
            this.datas.clear();
        }
        this.datas.addAll(datas);
        sort(this.datas);
        if(!append)notifyDataSetChanged();
    }

    /**
     * 排序，Call after reload(List<IT>,boolean) method
     *
     * @param datas
     */
    protected void sort(ArrayList<IT> datas) {
        //do same thins
    }

    @Override
    public int getCount() {
        return datas.size();
    }
    public final IT getItem(int position) {
        return datas.get(position);
    }
}
