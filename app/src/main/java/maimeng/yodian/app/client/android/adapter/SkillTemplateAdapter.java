package maimeng.yodian.app.client.android.adapter;

import android.app.Fragment;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.viewentry.skill.ViewEntry;

public class SkillTemplateAdapter extends AbstractAdapter<ViewEntry,SkillTemplateAdapter.ViewHolder> {
    public SkillTemplateAdapter(Context context, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(context, viewHolderClickListener);
    }

    public SkillTemplateAdapter(Fragment fragment, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(fragment, viewHolderClickListener);
    }

    public SkillTemplateAdapter(android.support.v4.app.Fragment fragment, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(fragment, viewHolderClickListener);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).viewType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==ViewEntry.VIEW_TYPE_ADDBUTTON){
            return new AddButtonViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.skill_template_addbutton,parent,false));
        }else if(viewType==ViewEntry.VIEW_TYPE_ITEM){
            return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.skill_template_item,parent,false));
        }else{
            throw new IllegalArgumentException("viewType must to be ViewEntry.VIEW_TYPE_ADDBUTTON or ViewEntry.VIEW_TYPE_ITEM");
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }
    class AddButtonViewHolder extends ViewHolder{
        public AddButtonViewHolder(View itemView) {
            super(itemView);
        }
    }
    class ItemViewHolder extends ViewHolder{
        public ItemViewHolder(View itemView) {
            super(itemView);
        }
    }
     public abstract class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
