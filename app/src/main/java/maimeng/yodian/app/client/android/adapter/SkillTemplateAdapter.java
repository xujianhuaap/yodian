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

public class SkillTemplateAdapter extends AbstractAdapter<ViewEntry, SkillTemplateAdapter.ViewHolder> {
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
        if (viewType == ViewEntry.VIEW_TYPE_ADDBUTTON) {
            return new AddButtonViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.skill_template_addbutton, parent, false));
        } else if (viewType == ViewEntry.VIEW_TYPE_ITEM) {
            SkillTemplateItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.skill_template_item, parent, false);
            return new ItemViewHolder(binding);
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
            binding.setTemplate(template);
            this.template = template;
            ImageAdapter.image(binding.skillImg, template.getPic());
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
