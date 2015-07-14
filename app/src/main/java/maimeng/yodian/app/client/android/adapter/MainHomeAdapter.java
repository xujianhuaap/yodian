package maimeng.yodian.app.client.android.adapter;

import android.app.Fragment;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import maimeng.yodian.app.client.android.model.Skill;

/**
 * Created by android on 15-7-13.
 */
public class MainHomeAdapter extends AbstractAdapter<Skill,MainHomeAdapter.ViewHolder>{
    public MainHomeAdapter(Context context, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(context, viewHolderClickListener);
    }

    public MainHomeAdapter(Fragment fragment, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(fragment, viewHolderClickListener);
    }

    public MainHomeAdapter(android.support.v4.app.Fragment fragment, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(fragment, viewHolderClickListener);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new TextView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv.setText(getItem(position).getName());
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView tv;
        public ViewHolder(View itemView) {
            super(itemView);
            this.tv=(TextView)itemView;
        }
    }
}
