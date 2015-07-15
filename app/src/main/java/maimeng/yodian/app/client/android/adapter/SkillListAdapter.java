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

import org.henjue.library.share.weibo.model.User;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.common.UserAuth;
import maimeng.yodian.app.client.android.databinding.SkillListItemBinding;
import maimeng.yodian.app.client.android.model.Skill;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.widget.RoundImageView;
import maimeng.yodian.app.client.android.widget.SwipeItemLayout;

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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final UserAuth user;
        private SwipeItemLayout swipeItemLayout;
        private final SkillListItemBinding binding;
        public ViewHolder(SkillListItemBinding binding) {
            super(binding.getRoot());
            swipeItemLayout=(SwipeItemLayout)itemView;
            this.binding=binding;
            binding.btnEdit.setOnClickListener(this);
            user=UserAuth.read(swipeItemLayout.getContext());
        }
        public void bind(Skill item){
            if(item.getUid()==user.uid){
                binding.btnEdit.setVisibility(View.VISIBLE);
            }else{
                binding.btnEdit.setVisibility(View.GONE);
            }
            binding.setSkill(item);
            binding.price.setText(Html.fromHtml(itemView.getResources().getString(R.string.lable_price,item.getPrice(),item.getUnit())));
            Network.image(item.getAvatar(), binding.userAvatar);
            Network.image(item.getPic(), binding.img);
        }

        @Override
        public void onClick(View v) {
            if(v==binding.btnEdit){
                if(swipeItemLayout.isClosed()){
                    swipeItemLayout.openWithAnim();
                }else{
                    swipeItemLayout.closeWithAnim();
                }
            }
        }
    }
}



