package maimeng.yodian.app.client.android.adapter;

import android.app.Fragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.common.UserAuth;
import maimeng.yodian.app.client.android.databinding.SkillListItemBinding;
import maimeng.yodian.app.client.android.model.Skill;
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

        public Skill getData() {
            return data;
        }

        private Skill data;

        public SkillListItemBinding getBinding() {
            return binding;
        }

        private final SkillListItemBinding binding;
        public ViewHolder(SkillListItemBinding binding) {
            super(binding.getRoot());
            swipeItemLayout=(SwipeItemLayout)itemView;
            swipeItemLayout.setOnClickListener(this);
            this.binding=binding;
            binding.btnEdit.setOnClickListener(this);
            binding.btnContect.setOnClickListener(this);
            binding.btnShare.setOnClickListener(this);
            binding.btnChangeState.setOnClickListener(this);
            binding.btnDelete.setOnClickListener(this);
            binding.btnUpdate.setOnClickListener(this);
            user=UserAuth.read(swipeItemLayout.getContext());
        }
        public void bind(Skill item){
            this.data =item;
            binding.setSkill(item);
            if(item.getUid()==user.uid){
                binding.btnEdit.setVisibility(View.VISIBLE);
                binding.btnContect.setVisibility(View.GONE);
            }else{
                binding.btnContect.setVisibility(View.VISIBLE);
                binding.btnEdit.setVisibility(View.GONE);
            }

            binding.price.setText(Html.fromHtml(itemView.getResources().getString(R.string.lable_price, item.getPrice(),item.getUnit())));
        }
        public void closeWithAnim(){
            if(swipeItemLayout.isClosed()){
            }else{
                swipeItemLayout.closeWithAnim();
            }
        }
        @Override
        public void onClick(View v) {
            if(v==swipeItemLayout){
                mViewHolderClickListener.onItemClick(this,getLayoutPosition());
            }else if(v==binding.btnEdit){
                if(swipeItemLayout.isClosed()){
                    swipeItemLayout.openWithAnim();
                }else{
                    swipeItemLayout.closeWithAnim();
                }
            }else{
                mViewHolderClickListener.onClick(this,v,getLayoutPosition());
            }
        }
    }
}



