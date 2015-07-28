package maimeng.yodian.app.client.android.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.databinding.RmarkListItemBinding;
import maimeng.yodian.app.client.android.model.Rmark;
/**
 * Created by android on 2015/7/28.
 */
public class RmarkListAdapter extends AbstractAdapter<Rmark,RmarkListAdapter.ViewHolder>{
    public RmarkListAdapter(Context context, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(context, viewHolderClickListener);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RmarkListItemBinding binding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.rmark_list_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(getItem(position));
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private final RmarkListItemBinding binding;

        public ViewHolder(RmarkListItemBinding binding){
            super(binding.getRoot());
            this.binding=binding;
        }
        public void bind(Rmark rmark){
            this.binding.setRmark(rmark);
        }
    }
}
