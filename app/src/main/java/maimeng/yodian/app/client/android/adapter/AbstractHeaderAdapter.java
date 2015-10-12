package maimeng.yodian.app.client.android.adapter;

/**
 * Created by android on 15-8-11.
 */

import android.app.Fragment;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android on 15-8-11.
 */
public abstract class AbstractHeaderAdapter<IT, VH extends RecyclerView.ViewHolder > extends RecyclerView.Adapter<VH> {
    protected final ViewHolderClickListener<VH> mViewHolderClickListener;
    protected final Context mContext;

    public void delete(int position) {
        this.datas.remove(position);
        notifyItemRemoved(position);
    }

    public void remove(int postion){
        this.datas.remove(postion);
        notifyItemRemoved(postion);
    }

    /**
     * ViewHolder点击监听
     *
     * @param <VH>
     */
    public interface ViewHolderClickListener<VH> {
        void onItemClick(VH holder, int postion);

        void onClick(VH holder, View clickItem, int postion);
    }


    public AbstractHeaderAdapter(Context context, ViewHolderClickListener<VH> viewHolderClickListener) {
        this.mViewHolderClickListener = viewHolderClickListener;
        this.mContext = context;
    }

    public AbstractHeaderAdapter(Fragment fragment, ViewHolderClickListener<VH> viewHolderClickListener) {
        this(fragment.getActivity(), viewHolderClickListener);
    }

    public AbstractHeaderAdapter(android.support.v4.app.Fragment fragment, ViewHolderClickListener<VH> viewHolderClickListener) {
        this(fragment.getActivity(), viewHolderClickListener);
    }



    public final ArrayList<IT> datas = new ArrayList<>();
    public void reload(final List<IT> datas, boolean append) {
        if (!append) {
            this.datas.clear();
        }
        this.datas.addAll(datas);
        sort(this.datas);
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
    public final int getItemCount() {
        return this.datas.size()+1;
    }

    public abstract Object getItem(int position) ;
}


