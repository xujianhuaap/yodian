package maimeng.yodian.app.client.android.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.databinding.ViewHeaderMainHomeBinding;
import maimeng.yodian.app.client.android.entry.skillseletor.HeaderViewEntry;
import maimeng.yodian.app.client.android.entry.skillseletor.ItemViewEntry;
import maimeng.yodian.app.client.android.entry.skillseletor.ViewEntry;
import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.databinding.SkillListItemSkillBinding;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.view.skill.SkillPreviewActivity;
import maimeng.yodian.app.client.android.widget.SwipeItemLayout;

/**
 * Created by android on 15-7-13.
 */
public class SkillListHomeAdapter extends AbstractAdapter<ViewEntry, SkillListHomeAdapter.ViewHolder> {
    private int mScreenWidth;
    private ViewHeaderMainHomeBinding mHeaderBinding;
    private HeaderViewEntry mHeaderViewEntry;

    public ViewHeaderMainHomeBinding getHeaderBinding() {
        return mHeaderBinding;
    }

    public SkillListHomeAdapter(Context context, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(context, viewHolderClickListener);
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
    }

    public SkillListHomeAdapter(Fragment fragment, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(fragment, viewHolderClickListener);
        mScreenWidth = fragment.getResources().getDisplayMetrics().widthPixels;
    }

    public SkillListHomeAdapter(android.support.v4.app.Fragment fragment, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(fragment, viewHolderClickListener);
        mScreenWidth = fragment.getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == ViewEntry.VIEW_TYPE_HEAD) {
            ViewHeaderMainHomeBinding headerBinding = DataBindingUtil.inflate(inflater, R.layout.view_header_main_home, parent, false);
            return new HeaderViewHolder(headerBinding);
        } else {
            SkillListItemSkillBinding binding = DataBindingUtil.inflate(inflater, R.layout.skill_list_item_skill, parent, false);
            return new ItemViewHolder(binding);
        }

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ViewEntry item = getItem(position);
        if (position == 0) {
            if (item instanceof HeaderViewEntry) {
                User user = ((HeaderViewEntry) item).user;
                ((HeaderViewHolder) holder).bind(user);
            }
        } else {
            if (holder instanceof ItemViewHolder) {
                if (item instanceof ItemViewEntry) {
                    Skill skill = ((ItemViewEntry) item).skill;
                    ((ItemViewHolder) holder).bind(skill);
                }

            }

        }


    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ViewEntry.VIEW_TYPE_HEAD;
        }
        return ViewEntry.VIEW_TYPE_ITEM;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Override
        public void onClick(View v) {

        }

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    /***
     *
     */
    public class HeaderViewHolder extends SkillListHomeAdapter.ViewHolder {

        private User user;
        private Bitmap defaultAvatar;


        public ViewHeaderMainHomeBinding getHeaderMainHomeBinding() {
            return mHeaderBinding;
        }

        public User getData() {
            return user;
        }

        @Override
        public void onClick(View v) {
            super.onClick(v);
            mViewHolderClickListener.onClick(HeaderViewHolder.this, v, getLayoutPosition());
        }

        public HeaderViewHolder(ViewHeaderMainHomeBinding headerMainHomeBinding) {
            super(headerMainHomeBinding.getRoot());
            mHeaderBinding = headerMainHomeBinding;
            headerMainHomeBinding.btnChat.setOnClickListener(this);
            headerMainHomeBinding.btnSettings.setOnClickListener(this);
            headerMainHomeBinding.btnReport.setOnClickListener(this);
            headerMainHomeBinding.userAvatar.setOnClickListener(this);

            //我的订单 我的余额 添加技能
            headerMainHomeBinding.myOrder.setOnClickListener(this);
            headerMainHomeBinding.myRemainder.setOnClickListener(this);
            headerMainHomeBinding.btnCreateskill.setOnClickListener(this);
        }

        public void bind(User user) {
            this.user = user;
            mHeaderBinding.setUser(user);
            mHeaderBinding.executePendingBindings();
            if (user.getUid() != User.read(mContext).getUid()) {
                mHeaderBinding.btnSettings.setVisibility(View.INVISIBLE);
                mHeaderBinding.btnCreateskill.setVisibility(View.GONE);
                mHeaderBinding.btnChat.setVisibility(View.GONE);
                mHeaderBinding.icEditAvatar.setVisibility(View.GONE);
                mHeaderBinding.bottom.setVisibility(View.GONE);
                mHeaderBinding.btnReport.setVisibility(View.VISIBLE);

            } else {
                mHeaderBinding.icEditAvatar.setVisibility(View.VISIBLE);
                mHeaderBinding.bottom.setVisibility(View.VISIBLE);
            }
        }


    }

    /***
     *
     */
    public class ItemViewHolder extends SkillListHomeAdapter.ViewHolder {
        private final User user;
        private SwipeItemLayout swipeItemLayout;
        private boolean isMe;

        public Skill getData() {
            return data;
        }

        private Skill data;

        public SkillListItemSkillBinding getBinding() {
            return binding;
        }

        private final SkillListItemSkillBinding binding;

        public ItemViewHolder(SkillListItemSkillBinding binding) {
            super(binding.getRoot());
            swipeItemLayout = (SwipeItemLayout) itemView.findViewById(R.id.swipe_item_layout);
            binding.root.setOnClickListener(this);
            this.binding = binding;
            binding.btnBottom.setOnClickListener(this);
            binding.btnShare.setOnClickListener(this);
            binding.btnChangeState.setOnClickListener(this);
            binding.btnDelete.setOnClickListener(this);
            binding.btnUpdate.setOnClickListener(this);
            //create by xu 08-06
            binding.btnReview.setOnClickListener(this);
            //end
            user = User.read(swipeItemLayout.getContext());
        }

        public void bind(Skill item) {
            closeWithAnim();
            this.data = item;
            binding.setSkill(item);
            binding.executePendingBindings();
            isMe = item.getUid() == user.getUid();
            if (isMe) {
                binding.btnEdit.setVisibility(View.VISIBLE);
                binding.btnContect.setVisibility(View.GONE);
            } else {
                binding.btnContect.setVisibility(View.VISIBLE);
                binding.btnEdit.setVisibility(View.GONE);
            }

            binding.price.setText(Html.fromHtml(itemView.getResources().getString(R.string.lable_price, item.getPrice(), item.getUnit())));
        }

        public void closeWithAnim() {
            if (swipeItemLayout.isClosed()) {
            } else {
                swipeItemLayout.closeWithAnim();
            }
        }

        @Override
        public void onClick(View v) {
            if (v == binding.root) {
                mViewHolderClickListener.onItemClick(this, getLayoutPosition());
            } else if (v == binding.btnBottom) {
                if (isMe) {
                    if (swipeItemLayout.isClosed()) {
                        swipeItemLayout.openWithAnim();
                    } else {
                        swipeItemLayout.closeWithAnim();
                    }
                } else {
                    mViewHolderClickListener.onClick(this, v, getLayoutPosition());
                }
            } else if (v == binding.btnReview) {
                //create by xu 08-06
                Activity activity = (Activity) mContext;
                SkillPreviewActivity.show(binding.getSkill(), activity, 0, 0);
                //end
            } else {
                mViewHolderClickListener.onClick(this, v, getLayoutPosition());
            }
        }
    }

    public void reload(final List<ViewEntry> datas, boolean append) {

        if (!append) {
            this.datas.clear();
            this.datas.add(0, mHeaderViewEntry);
        }
        this.datas.addAll(datas);
        sort(this.datas);
    }

    public void reload(final HeaderViewEntry headerViewEntry) {
        if (this.datas.size() > 0) {
            this.datas.remove(0);
        }
        mHeaderViewEntry = headerViewEntry;
        this.datas.add(headerViewEntry);
    }


}



