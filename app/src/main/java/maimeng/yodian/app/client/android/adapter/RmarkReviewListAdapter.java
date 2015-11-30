package maimeng.yodian.app.client.android.adapter;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Fragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.databinding.ItemRmarkListBinding;
import maimeng.yodian.app.client.android.databinding.ViewHeaderPlaceholderBinding;
import maimeng.yodian.app.client.android.databinding.ViewHeaderPreviewDiaryBinding;
import maimeng.yodian.app.client.android.model.Rmark;
import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.model.user.User;

/**
 * Created by android on 15-8-7.
 */
public class RmarkReviewListAdapter extends AbstractHeaderAdapter<Rmark, RmarkReviewListAdapter.ViewHolder> {
    private ViewHolderClickListener<RmarkReviewListAdapter.ViewHolder> mViewHolderClickListener;
    private Skill mSkill;
    private User me;
    private final int TYPE_HEADER = 232;
    private final int TYPE_NORMAL = 234;
    private int SCREEN_WIDTH;
    private Spanned priceText = null;


    public RmarkReviewListAdapter(Context context, Skill skill, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(context, viewHolderClickListener);
        me = User.read(mContext);
        SCREEN_WIDTH = mContext.getResources().getDisplayMetrics().widthPixels;
        mViewHolderClickListener = viewHolderClickListener;
        mSkill = skill;
    }

    public RmarkReviewListAdapter(Fragment fragment, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(fragment, viewHolderClickListener);
    }

    public RmarkReviewListAdapter(android.support.v4.app.Fragment fragment, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(fragment, viewHolderClickListener);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (position == 0) {
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
            viewHolder.binding.setSkill(mSkill);
            priceText = Html.fromHtml(mContext.getResources().getString(R.string.lable_price, mSkill.getPrice(), mSkill.getUnit()));
            viewHolder.binding.price.setText(priceText);
        } else {
            Rmark rmark = (Rmark) getItem(position);
            NormalViewHolder viewHolder = (NormalViewHolder) holder;
            viewHolder.bind(rmark);
            viewHolder.binding.setRmark(rmark);
        }

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            ViewHeaderPlaceholderBinding headerBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.view_header_placeholder, parent, false);
            HeaderViewHolder viewHolder = new HeaderViewHolder(headerBinding);
            return viewHolder;
        } else {
            ItemRmarkListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_rmark_list, parent, false);
            return new NormalViewHolder(binding);
        }


    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_NORMAL;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public final class HeaderViewHolder extends ViewHolder {
        public final ViewHeaderPlaceholderBinding binding;

        public HeaderViewHolder(ViewHeaderPlaceholderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            if(mSkill!=null){
                if(mSkill.getAllow_sell()==1){
                   binding.skillAllowSell.setVisibility(View.VISIBLE);
                }
                if(mSkill.isSelector()){
                    binding.skillSlector.setVisibility(View.VISIBLE);
                }
            }
            binding.pic.setLayoutParams(new RelativeLayout.LayoutParams(SCREEN_WIDTH, SCREEN_WIDTH * 4 / 5));
        }
    }

    public final class NormalViewHolder extends ViewHolder implements View.OnClickListener {
        public final ItemRmarkListBinding binding;
        private final PropertyValuesHolder alpha2;
        private final PropertyValuesHolder translation2;
        private final PropertyValuesHolder translation;
        private final PropertyValuesHolder alpha;
        ObjectAnimator open;
        ObjectAnimator close;
        boolean opened = false;

        public NormalViewHolder(ItemRmarkListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) binding.pic.getLayoutParams();
            layoutParams.width = SCREEN_WIDTH;
            layoutParams.height = SCREEN_WIDTH;
            binding.pic.setLayoutParams(layoutParams);

            translation = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, -200f);
            alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0.8f, 1.0f);
            translation2 = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, -200f, 0f);
            alpha2 = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f);

            this.binding.btnMenuDelete.setOnClickListener(this);
            this.binding.btnMenuMore.setOnClickListener(this);
            this.binding.btnMenuReport.setOnClickListener(this);
        }


        private void bind(Rmark rmark) {

            reset();
            if (rmark.getUid() == me.getUid()) {
                open = ObjectAnimator.ofPropertyValuesHolder(binding.btnMenuDelete, translation, alpha);
                open.setDuration(binding.getRoot().getContext().getResources().getInteger(R.integer.duration));


                close = ObjectAnimator.ofPropertyValuesHolder(binding.btnMenuDelete, translation2, alpha2);
                close.setDuration(binding.getRoot().getContext().getResources().getInteger(R.integer.duration));

            } else {
                open = ObjectAnimator.ofPropertyValuesHolder(binding.btnMenuReport, translation, alpha);
                open.setDuration(binding.getRoot().getContext().getResources().getInteger(R.integer.duration));

                close = ObjectAnimator.ofPropertyValuesHolder(binding.btnMenuReport, translation2, alpha2);
                close.setDuration(binding.getRoot().getContext().getResources().getInteger(R.integer.duration));

            }
        }


        public void reset() {
            opened = false;
            binding.btnMenuDelete.setTranslationX(0f);
            binding.btnMenuDelete.setAlpha(0f);
            binding.btnMenuReport.setTranslationX(0f);
            binding.btnMenuReport.setAlpha(0f);
        }

        @Override
        public void onClick(View v) {
//            if (v == binding.btnMenuMore) {
//                if (opened) {
//                    opened = false;
//                    close.start();
//                } else {
//                    opened = true;
//                    open.start();
//                }
//            } else if (v == binding.btnMenuDelete) {
//                mViewHolderClickListener.onClick(RmarkReviewListAdapter.NormalViewHolder.this, v, getLayoutPosition());
//
//                if (opened) {
//                    opened = false;
//                    close.start();
//                } else {
//                    opened = true;
//                    open.start();
//                }
//            } else if (v == binding.btnMenuReport) {
//                mViewHolderClickListener
//                        .onClick(RmarkReviewListAdapter.NormalViewHolder.this, v, getLayoutPosition());
//                if (opened) {
//                    opened = false;
//                    close.start();
//                } else {
//                    opened = true;
//                    open.start();
//                }
//            }
        }
    }

    @Override
    public Object getItem(int position) {
        if (position == 0) {
            return mSkill;
        }
        return datas.get(position - 1);
    }
}
