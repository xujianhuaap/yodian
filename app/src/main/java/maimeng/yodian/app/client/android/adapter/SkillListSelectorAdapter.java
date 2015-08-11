package maimeng.yodian.app.client.android.adapter;

import android.app.Fragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.bingoogolapple.bgabanner.BGABanner;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.common.loader.ImageLoader;
import maimeng.yodian.app.client.android.common.model.Skill;
import maimeng.yodian.app.client.android.databinding.SkillListItemSelectorBinding;
import maimeng.yodian.app.client.android.databinding.SkillListItemSelectorHeadBinding;
import maimeng.yodian.app.client.android.entry.skillseletor.BannerViewEntry;
import maimeng.yodian.app.client.android.entry.skillseletor.HeadViewEntry;
import maimeng.yodian.app.client.android.entry.skillseletor.ItemViewEntry;
import maimeng.yodian.app.client.android.entry.skillseletor.ViewEntry;
import maimeng.yodian.app.client.android.model.User;
import maimeng.yodian.app.client.android.network.response.SkillResponse;
import maimeng.yodian.app.client.android.widget.SwipeItemLayout;

/**
 * Created by android on 15-7-13.
 */
public class SkillListSelectorAdapter extends AbstractAdapter<ViewEntry, SkillListSelectorAdapter.BaseViewHolder> {
    public SkillListSelectorAdapter(Context context, ViewHolderClickListener<BaseViewHolder> viewHolderClickListener) {
        super(context, viewHolderClickListener);
    }

    public SkillListSelectorAdapter(Fragment fragment, ViewHolderClickListener<BaseViewHolder> viewHolderClickListener) {
        super(fragment, viewHolderClickListener);
    }

    public SkillListSelectorAdapter(android.support.v4.app.Fragment fragment, ViewHolderClickListener<BaseViewHolder> viewHolderClickListener) {
        super(fragment, viewHolderClickListener);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ViewEntry.VIEW_TYPE_ITEM:
                SkillListItemSelectorBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.skill_list_item_selector, parent, false);
                return new ItemViewHolder(binding);
            case ViewEntry.VIEW_TYPE_BANNER:
                return new BannerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.skill_list_item_selector_banner, parent, false));
            case ViewEntry.VIEW_TYPE_HEAD:
                SkillListItemSelectorHeadBinding headBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.skill_list_item_selector_head, parent, false);
                return new HeadViewHolder(headBinding);
            default:
                throw new RuntimeException("Error ViewType");
        }

    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ViewEntry.VIEW_TYPE_ITEM:
                bindItem(holder, getItem(position));
                break;
            case ViewEntry.VIEW_TYPE_HEAD:
                bindHead(holder, getItem(position));
                break;
            case ViewEntry.VIEW_TYPE_BANNER:
                bindBanner(holder, getItem(position));
                break;
        }

    }

    private void bindBanner(BaseViewHolder h, ViewEntry item) {
        BannerViewHolder holder = (BannerViewHolder) h;
        holder.bind(((BannerViewEntry) item));
    }

    private void bindHead(BaseViewHolder h, ViewEntry item) {
        HeadViewHolder holder = (HeadViewHolder) h;
        holder.bind(((HeadViewEntry) item));
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).viewType;
    }


    private void bindItem(BaseViewHolder h, ViewEntry item) {
        ItemViewHolder holder = (ItemViewHolder) h;
        holder.bind(((ItemViewEntry) item).skill);
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder {
        public BaseViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class BannerViewHolder extends BaseViewHolder {
        private BGABanner banner;
        private final BGABanner.TransitionEffect[] types;

        public BannerViewHolder(View root) {
            super(root);
            banner = (cn.bingoogolapple.bgabanner.BGABanner) root.findViewById(R.id.banner_pager);
            types = new BGABanner.TransitionEffect[]{BGABanner.TransitionEffect.Default,
                    BGABanner.TransitionEffect.Alpha,
                    BGABanner.TransitionEffect.Rotate,
                    BGABanner.TransitionEffect.Cube,
                    BGABanner.TransitionEffect.Flip,
                    BGABanner.TransitionEffect.Accordion,
                    BGABanner.TransitionEffect.ZoomFade,
                    BGABanner.TransitionEffect.Fade,
                    BGABanner.TransitionEffect.ZoomCenter,
                    BGABanner.TransitionEffect.ZoomStack,
                    BGABanner.TransitionEffect.Stack,
                    BGABanner.TransitionEffect.Depth,
                    BGABanner.TransitionEffect.Zoom};

        }

        public void bind(BannerViewEntry item) {
            List<View> views = new ArrayList<>();
            for (SkillResponse.DataNode.Banner banner : item.banners) {
                ImageView iv = new ImageView(mContext);
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                views.add(iv);
                ImageLoader.image(iv, banner.getPic());
            }
            banner.setViews(views);
            int type = new Random().nextInt(types.length);
            if (type == types.length) {
                type = types.length - 1;
            }
            banner.setTransitionEffect(types[type]);
        }
    }

    public class HeadViewHolder extends BaseViewHolder {
        private final SkillListItemSelectorHeadBinding binding;

        public HeadViewHolder(SkillListItemSelectorHeadBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(HeadViewEntry item) {
            binding.setSkill(item.skill);
            binding.setUser(item.user);
        }
    }

    public class ItemViewHolder extends BaseViewHolder implements View.OnClickListener {
        private final User user;
        private SwipeItemLayout swipeItemLayout;

        public Skill getData() {
            return data;
        }

        private Skill data;

        public SkillListItemSelectorBinding getBinding() {
            return binding;
        }

        private final SkillListItemSelectorBinding binding;

        public ItemViewHolder(SkillListItemSelectorBinding binding) {
            super(binding.getRoot());
            swipeItemLayout = (SwipeItemLayout) itemView.findViewById(R.id.swipe_item_layout);
            swipeItemLayout.setOnClickListener(this);
            itemView.setOnClickListener(this);
            this.binding = binding;
            binding.btnEdit.setOnClickListener(this);
            binding.btnContect.setOnClickListener(this);
            binding.btnShare.setOnClickListener(this);
            binding.btnChangeState.setOnClickListener(this);
            binding.btnDelete.setOnClickListener(this);
            binding.btnUpdate.setOnClickListener(this);
            user = User.read(swipeItemLayout.getContext());
        }

        public void bind(Skill item) {
            this.data = item;
            binding.setSkill(item);
            binding.executePendingBindings();
            if (item.getUid() == user.getUid()) {
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
            if (v == itemView) {
                mViewHolderClickListener.onItemClick(this, getLayoutPosition());
            } else if (v == binding.btnEdit) {
                if (swipeItemLayout.isClosed()) {
                    swipeItemLayout.openWithAnim();
                } else {
                    swipeItemLayout.closeWithAnim();
                }
            } else {
                mViewHolderClickListener.onClick(this, v, getLayoutPosition());
            }
        }
    }
}



