package maimeng.yodian.app.client.android.adapter;

import android.app.Fragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.databinding.SkillListItemBinding;
import maimeng.yodian.app.client.android.model.Skill;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.widget.RoundImageView;

/**
 * Created by android on 15-7-13.
 */
public class SkillListAdapter extends AbstractAdapter<Skill,SkillListAdapter.ViewHolder>{
    public SkillListAdapter(Context context, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(context, viewHolderClickListener);
    }

    public SkillListAdapter(Fragment fragment, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(fragment, viewHolderClickListener);
    }

    public SkillListAdapter(android.support.v4.app.Fragment fragment, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(fragment, viewHolderClickListener);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SkillListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.skill_list_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Skill item = getItem(position);
        holder.bind(item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final SkillListItemBinding binding;
        public ViewHolder(SkillListItemBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
        public void bind(Skill item){
            binding.setSkill(item);
            binding.price.setText(Html.fromHtml(itemView.getResources().getString(R.string.lable_price,item.getPrice(),item.getUnit())));
            Network.image(item.getAvatar(), binding.userAvatar);
            Network.image(item.getPic(), binding.img);
        }
    }
}



