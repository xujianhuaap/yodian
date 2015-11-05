package maimeng.yodian.app.client.android.adapter;

import android.app.Fragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.databinding.SkillTemplateItemBinding;
import maimeng.yodian.app.client.android.databings.ImageAdapter;
import maimeng.yodian.app.client.android.entry.skilltemplate.ItemViewEntry;
import maimeng.yodian.app.client.android.entry.skilltemplate.ViewEntry;
import maimeng.yodian.app.client.android.model.SkillTemplate;
import maimeng.yodian.app.client.android.utils.LogUtil;

public class SkillTemplateAdapter extends AbstractAdapter<ViewEntry, SkillTemplateAdapter.ViewHolder> {
    private int mScreenWidth=0;
    private  int unit=0;
    public SkillTemplateAdapter(Context context, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(context, viewHolderClickListener);
        mScreenWidth=mContext.getResources().getDisplayMetrics().widthPixels;
        unit=mScreenWidth/10;
    }

    public SkillTemplateAdapter(Fragment fragment, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(fragment, viewHolderClickListener);
        mScreenWidth=mContext.getResources().getDisplayMetrics().widthPixels;
        unit=mScreenWidth/10;
    }

    public SkillTemplateAdapter(android.support.v4.app.Fragment fragment, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(fragment, viewHolderClickListener);
        mScreenWidth=mContext.getResources().getDisplayMetrics().widthPixels;
        unit=mScreenWidth/10;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).viewType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=null;
        if (viewType == ViewEntry.VIEW_TYPE_ADDBUTTON) {
            view=LayoutInflater.from(parent.getContext()).inflate(R.layout.skill_template_addbutton, parent, false);
            AddButtonViewHolder viewHolder=new AddButtonViewHolder(view);
            return viewHolder;
        } else if (viewType == ViewEntry.VIEW_TYPE_ITEM) {
            SkillTemplateItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.skill_template_item, parent, false);
            ItemViewHolder viewHolder=new ItemViewHolder(binding);
            return viewHolder;
        } else {
            throw new IllegalArgumentException("viewType must to be ViewEntry.VIEW_TYPE_ADDBUTTON or ViewEntry.VIEW_TYPE_ITEM");
        }

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder.getItemViewType() == ViewEntry.VIEW_TYPE_ITEM) {

            ((ItemViewHolder) holder).bind(((ItemViewEntry) getItem(position)).template);

        }
    }

    public class AddButtonViewHolder extends ViewHolder {
        public AddButtonViewHolder(View itemView) {
            super(itemView);
            ViewGroup.LayoutParams layoutParams=itemView.getLayoutParams();
            layoutParams.width=mScreenWidth*7/10;
            itemView.setLayoutParams(layoutParams);

        }
    }

    public class ItemViewHolder extends ViewHolder {
        public final SkillTemplateItemBinding binding;
        private SkillTemplate template;

        public SkillTemplate getTemplate() {
            return template;
        }

        public ItemViewHolder(SkillTemplateItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

        public void bind(SkillTemplate template) {
            String numStr=null;
            switch (getLayoutPosition()){
                case 0:
                    numStr="FIRST";
                    break;
                case 1:
                    numStr="SECOND";
                    break;
                case 2:
                    numStr="THIRD";
                    break;
                case 3:
                    numStr="FOURTH";
                    break;
                case 4:
                    numStr="FIFTH";
                    break;
                case 5:
                    numStr="SIXTH";
                    break;
                default:
                    numStr="ELSE";


            }
            ViewGroup.LayoutParams layoutParams=itemView.getLayoutParams();
            layoutParams.width=mScreenWidth*7/10+unit*(getLayoutPosition()%3);
            itemView.setLayoutParams(layoutParams);
            binding.setTemplate(template);
            this.template = template;
            binding.skillName.setText(numStr+"  "+template.getName());
        }
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mViewHolderClickListener.onItemClick(this, getLayoutPosition());
        }
    }
}
