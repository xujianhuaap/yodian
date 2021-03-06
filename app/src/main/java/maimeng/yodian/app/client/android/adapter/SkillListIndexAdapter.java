package maimeng.yodian.app.client.android.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.viewpagerindicator.IconPageIndicator;
import com.viewpagerindicator.IconPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrFrameLayout;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.databinding.SkillListItemSkillBinding;
import maimeng.yodian.app.client.android.databings.ImageAdapter;
import maimeng.yodian.app.client.android.entry.skillseletor.BannerViewEntry;
import maimeng.yodian.app.client.android.entry.skillseletor.ItemViewEntry;
import maimeng.yodian.app.client.android.entry.skillseletor.ViewEntry;
import maimeng.yodian.app.client.android.model.skill.Banner;
import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.view.skill.SkillPreviewActivity;
import maimeng.yodian.app.client.android.widget.AutoScrollViewPager;
import maimeng.yodian.app.client.android.widget.ViewPagerFix;
import maimeng.yodian.app.client.android.widget.YDView;

/**
 * Created by android on 15-7-13.
 */
public class SkillListIndexAdapter extends AbstractAdapter<ViewEntry, SkillListIndexAdapter.BaseViewHolder> {
    private final PtrFrameLayout refreshLayout;
    private int mScreenWidth;

    public SkillListIndexAdapter(Context context, ViewHolderClickListener<BaseViewHolder> viewHolderClickListener, PtrFrameLayout refreshLayout) {
        super(context, viewHolderClickListener);
        this.refreshLayout = refreshLayout;
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
    }

    public SkillListIndexAdapter(Fragment fragment, ViewHolderClickListener<BaseViewHolder> viewHolderClickListener, PtrFrameLayout refreshLayout) {
        super(fragment, viewHolderClickListener);
        this.refreshLayout = refreshLayout;
        mScreenWidth = fragment.getResources().getDisplayMetrics().widthPixels;
    }

    public SkillListIndexAdapter(android.support.v4.app.Fragment fragment, ViewHolderClickListener<BaseViewHolder> viewHolderClickListener, PtrFrameLayout refreshLayout) {
        super(fragment, viewHolderClickListener);
        this.refreshLayout = refreshLayout;
        mScreenWidth = fragment.getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ViewEntry.VIEW_TYPE_ITEM:
                SkillListItemSkillBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.skill_list_item_skill, parent, false);
                return new ItemViewHolder(binding);
            case ViewEntry.VIEW_TYPE_BANNER:
                return new BannerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.skill_list_item_banner, parent, false));
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
            case ViewEntry.VIEW_TYPE_BANNER:
                BannerViewEntry viewEntry = (BannerViewEntry) getItem(position);

                bindBanner(holder, viewEntry);
                break;
        }

    }

    private void bindBanner(BaseViewHolder h, ViewEntry item) {
        BannerViewHolder holder = (BannerViewHolder) h;
        holder.bind(((BannerViewEntry) item));
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).viewType;
    }


    private void bindItem(BaseViewHolder h, ViewEntry item) {
        ItemViewHolder holder = (ItemViewHolder) h;
        holder.bind(((ItemViewEntry) item).skill);
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public BaseViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                mViewHolderClickListener.onItemClick(this, getLayoutPosition());
            }
        }
    }

    public class BannerViewHolder extends BaseViewHolder implements ViewPagerFix.OnPageChangeListener, ViewPagerFix.OnFlipListener {
        private final ViewPagerAdapter adapter;
        private final IconPageIndicator indicator;
        public ViewPagerFix banner;
        public int currentPage;
        public BannerViewEntry list;

        public BannerViewHolder(View root) {
            super(root);
            banner = (ViewPagerFix) root.findViewById(R.id.banner_pager);
            banner.setOnClickListener(new ViewPagerFix.OnClickListener() {
                @Override
                public void onClickListener(View v) {
                    mViewHolderClickListener.onClick(BannerViewHolder.this, v, getLayoutPosition());
                }

            });


            banner.addOnPageChangeListener(this);
            banner.setCycle(true);
            banner.setInterval(3000);
            banner.setOnFlipListener(this);
            adapter = new ViewPagerAdapter(new ArrayList<View>(), banner);
            banner.setAdapter(adapter);
            banner.setStopScrollWhenTouch(true);
            banner.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
            indicator = (com.viewpagerindicator.IconPageIndicator) root.findViewById(R.id.titles);
            indicator.setViewPager(banner);

        }

        public void bind(BannerViewEntry item) {
            banner.stopAutoScroll();
            this.list = item;
            if (list.banners.size() < 2) {
                indicator.setVisibility(View.INVISIBLE);
            } else {
                indicator.setVisibility(View.VISIBLE);
            }
            final List<View> views = new ArrayList<>();
            for (Banner banner : item.banners) {
                YDView iv = new YDView(mContext);
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                views.add(iv);
//                iv.setOnClickListener(this);

                ImageAdapter.image(iv, banner.getPic());
            }
            adapter.setViews(views);
            adapter.notifyDataSetChanged();
            indicator.notifyDataSetChanged();
            banner.startAutoScroll();
//            banner.setViews(views);
//            int type = new Random().nextInt(types.length);
//            if (type == types.length) {
//                type = types.length - 1;
//            }
//            banner.setTransitionEffect(types[type]);
        }

        @Override
        public void onClick(View v) {
            super.onClick(v);
            mViewHolderClickListener.onClick(this, v, getLayoutPosition());
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            this.currentPage = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

        @Override
        public void onFlip() {
            refreshLayout.setEnabled(false);
            banner.stopAutoScroll();
        }

        @Override
        public void onCancel() {
            refreshLayout.setEnabled(true);
            banner.startAutoScroll();
        }
    }

    public class ItemViewHolder extends BaseViewHolder implements View.OnClickListener {
        private final User user;
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
            binding.root.setOnClickListener(this);
            this.binding = binding;
            binding.userAvatar.setOnClickListener(this);
            binding.btnEdit.setOnClickListener(this);
            binding.btnShare.setOnClickListener(this);
            binding.btnChangeState.setOnClickListener(this);
            binding.btnDelete.setOnClickListener(this);
//            binding.btnDelete.setTextColor(Color.parseColor("#000000"));
            binding.btnDelete.setBackgroundColor(Color.parseColor("#99000000"));
            binding.btnUpdate.setOnClickListener(this);
            //create by xu 08-06
            binding.btnReview.setOnClickListener(this);
            //end
            user = User.read(itemView.getContext());
        }

        public void bind(Skill item) {
            closeWithAnim();
            this.data = item;
            binding.setSkill(item);
            binding.executePendingBindings();
            isMe = item.getUid() == user.getUid();
            binding.btnEdit.setVisibility(View.INVISIBLE);
            binding.btnShare.setVisibility(View.INVISIBLE);
            binding.bottomDiv.setVisibility(View.INVISIBLE);
            binding.price.setText(Html.fromHtml(itemView.getResources().getString(R.string.lable_price, item.getPrice(), item.getUnit())));
            if (item.getAllow_sell() == 1) {
                binding.iconCanbuy.setVisibility(View.VISIBLE);
            } else {
                binding.iconCanbuy.setVisibility(View.GONE);
            }
            if (item.isSelector()) {
                binding.iconSelector.setVisibility(View.VISIBLE);
            } else {
                binding.iconSelector.setVisibility(View.GONE);
            }

        }

        public void closeWithAnim() {
            binding.slideBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onClick(View v) {
            if (v == binding.root) {
                mViewHolderClickListener.onItemClick(this, getLayoutPosition());
            } else if (v == binding.btnEdit) {
                if (isMe) {
                    if (binding.slideBar.getVisibility() != View.VISIBLE) {
                        binding.slideBar.setVisibility(View.VISIBLE);
                    } else {
                        binding.slideBar.setVisibility(View.INVISIBLE);
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

    class ViewPagerAdapter extends PagerAdapter implements IconPagerAdapter {
        private final ViewPagerFix viewPager;

        public void setViews(List<View> views) {
            this.views = views;
        }

        private List<View> views;

        public ViewPagerAdapter(List<View> views, ViewPagerFix viewPager) {
            this.views = views;
            this.viewPager = viewPager;
        }

        @Override
        public int getIconResId(int index) {
            return R.drawable.point;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final View child = views.get(position);
            container.addView(child);
            return child;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}



