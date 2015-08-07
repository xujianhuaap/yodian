package maimeng.yodian.app.client.android.adapter;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Fragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.common.model.Skill;
import maimeng.yodian.app.client.android.databinding.RmarkListItemBinding;
import maimeng.yodian.app.client.android.databinding.SkillListItemHomeBinding;
import maimeng.yodian.app.client.android.model.Rmark;

/**
 * Created by android on 15-8-7.
 */
public class RmarkAdapter extends AbstractAdapter<Rmark,RmarkAdapter.ViewHolder> {
    private ViewHolderClickListener<RmarkAdapter.ViewHolder> viewHolderViewHolderClickListener;

    public RmarkAdapter(Context context, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(context, viewHolderClickListener);
        this.viewHolderViewHolderClickListener=viewHolderViewHolderClickListener;
    }

    public RmarkAdapter(Fragment fragment, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(fragment, viewHolderClickListener);
    }

    public RmarkAdapter(android.support.v4.app.Fragment fragment, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(fragment, viewHolderClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.binding.setRmark(getItem(position));

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RmarkListItemBinding binding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.rmark_list_item, parent, false);
        return new ViewHolder(binding);
    }

    public  final class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final RmarkListItemBinding binding;
        private final PropertyValuesHolder alpha2;
        private final PropertyValuesHolder translation2;
        private final PropertyValuesHolder translation;
        private final PropertyValuesHolder alpha;
        ObjectAnimator open;
        ObjectAnimator close;
        boolean opened=false;

        public ViewHolder(RmarkListItemBinding binding) {
            super(binding.getRoot());
            this.binding=binding;

            translation=PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, -200f);
            alpha=PropertyValuesHolder.ofFloat(View.ALPHA, 0.8f, 1.0f);
            translation2=PropertyValuesHolder.ofFloat(View.TRANSLATION_X, -200f, 0f);
            alpha2=PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f);

            this.binding.btnMenuDelete.setOnClickListener(this);
            this.binding.btnMenuMore.setOnClickListener(this);
            this.binding.btnMenuReport.setOnClickListener(this);
        }

        private void bind(){
            reset();
//            if(rmark.getUid()==me.getUid()){
//                open = ObjectAnimator.ofPropertyValuesHolder(binding.btnMenuDelete, translation, alpha);
//                open.setDuration(binding.getRoot().getContext().getResources().getInteger(R.integer.duration));
//
//
//                close = ObjectAnimator.ofPropertyValuesHolder(binding.btnMenuDelete, translation2, alpha2);
//                close.setDuration(binding.getRoot().getContext().getResources().getInteger(R.integer.duration));
//
//            }else{
//                open = ObjectAnimator.ofPropertyValuesHolder(binding.btnMenuReport, translation, alpha);
//                open.setDuration(binding.getRoot().getContext().getResources().getInteger(R.integer.duration));
//
//                close = ObjectAnimator.ofPropertyValuesHolder(binding.btnMenuReport, translation2, alpha2);
//                close.setDuration(binding.getRoot().getContext().getResources().getInteger(R.integer.duration));
//
//            }
        }


        public void reset() {
            opened=false;
            binding.btnMenuDelete.setTranslationX(0f);
            binding.btnMenuDelete.setAlpha(0f);
            binding.btnMenuReport.setTranslationX(0f);
            binding.btnMenuReport.setAlpha(0f);
        }

        @Override
        public void onClick(View v) {
            if(v==binding.btnMenuMore){
                if(opened){
                    opened=false;
                    close.start();
                }else {
                    opened=true;
                    open.start();
                }
            }else if(v==binding.btnMenuDelete){
                RmarkAdapter.this.viewHolderViewHolderClickListener.onClick(RmarkAdapter.ViewHolder.this, v, getLayoutPosition());

                if(opened){
                    opened=false;
                    close.start();
                }else {
                    opened=true;
                    open.start();
                }
            }else if(v==binding.btnMenuReport){
                RmarkAdapter.this.viewHolderViewHolderClickListener.onClick(ViewHolder.this, v, getLayoutPosition());
                if(opened){
                    opened=false;
                    close.start();
                }else {
                    opened=true;
                    open.start();
                }
            }
        }
    }

}
