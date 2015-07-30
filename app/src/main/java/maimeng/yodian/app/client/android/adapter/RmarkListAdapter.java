package maimeng.yodian.app.client.android.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.databinding.RmarkListItemBinding;
import maimeng.yodian.app.client.android.model.Rmark;
/**
 * Created by android on 2015/7/28.
 */
public class RmarkListAdapter extends AbstractListAdapter<Rmark>{
    public RmarkListAdapter(Context context) {
        super(context);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RmarkListItemBinding binding;
        if(convertView==null){
            binding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.rmark_list_item, parent, false);
            convertView=binding.getRoot();
            convertView.setTag(binding);
        }else{
            binding=(RmarkListItemBinding)convertView.getTag();
        }
        binding.setRmark(getItem(position));
        return convertView;
    }

    public class ViewHolder{
        private final RmarkListItemBinding binding;

        public ViewHolder(RmarkListItemBinding binding){
            this.binding=binding;
        }
        public void bind(Rmark rmark){
            this.binding.setRmark(rmark);
        }
    }
}