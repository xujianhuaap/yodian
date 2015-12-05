package maimeng.yodian.app.client.android.adapter;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viewpagerindicator.IconPageIndicator;
import com.viewpagerindicator.IconPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrFrameLayout;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.databinding.SkillListItemSkillBinding;
import maimeng.yodian.app.client.android.databinding.UserHomeHeaderBinding;
import maimeng.yodian.app.client.android.entry.skillhome.HeaderViewEntry;
import maimeng.yodian.app.client.android.entry.skillhome.ItemViewEntry;
import maimeng.yodian.app.client.android.entry.skillhome.ViewEntry;
import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.view.skill.SkillPreviewActivity;
import maimeng.yodian.app.client.android.view.user.UserHeaderFrist;
import maimeng.yodian.app.client.android.view.user.UserHeaderSecond;
import maimeng.yodian.app.client.android.widget.ViewPager;

/**
 * Created by android on 15-7-13.
 */
public class SkillListHomeAdapter extends AbstractAdapter<ViewEntry, SkillListHomeAdapter.ViewHolder> {
    private final AppCompatActivity activity;
    private int mScreenWidth;
    private UserHomeHeaderBinding mHeaderBinding;
    private HeaderViewEntry mHeaderViewEntry;
    private final PtrFrameLayout refreshLayout;

    public UserHomeHeaderBinding getHeaderBinding() {
        return mHeaderBinding;
    }

    public SkillListHomeAdapter(AppCompatActivity context, ViewHolderClickListener<ViewHolder> viewHolderClickListener, PtrFrameLayout refreshLayout) {
        super(context, viewHolderClickListener);
        this.activity = context;
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        this.refreshLayout = refreshLayout;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == ViewEntry.VIEW_TYPE_HEAD) {
            UserHomeHeaderBinding headerBinding = DataBindingUtil.inflate(inflater, R.layout.user_home_header, parent, false);
            return new HeaderViewHolder(headerBinding, activity.getSupportFragmentManager());
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
    public class HeaderViewHolder extends SkillListHomeAdapter.ViewHolder implements ViewPager.OnPageChangeListener, ViewPager.OnFlipListener {

        private final ViewPager banner;
        private final IconPageIndicator indicator;
        private final ViewPagerAdapter adapter;
        private User user;
        private Bitmap defaultAvatar;

        public int currentPage;

        public UserHomeHeaderBinding getBinding() {
            return mHeaderBinding;
        }

        public User getData() {
            return user;
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
        }

        @Override
        public void onCancel() {
            refreshLayout.setEnabled(true);
        }

        @Override
        public void onClick(View v) {
            super.onClick(v);
            mViewHolderClickListener.onClick(HeaderViewHolder.this, v, getLayoutPosition());
        }

        public HeaderViewHolder(UserHomeHeaderBinding headerMainHomeBinding, FragmentManager manager) {
            super(headerMainHomeBinding.getRoot());
            mHeaderBinding = headerMainHomeBinding;
            banner = headerMainHomeBinding.bannerPager;
            indicator = headerMainHomeBinding.titles;
            banner.setOnClickListener(new ViewPager.OnClickListener() {
                @Override
                public void onClickListener(View v) {
                    mViewHolderClickListener.onClick(HeaderViewHolder.this, v, getLayoutPosition());
                }

            });


            banner.addOnPageChangeListener(this);
//            banner.setInterval(3000);
            banner.setOnFlipListener(this);
            adapter = new ViewPagerAdapter(new ArrayList<android.support.v4.app.Fragment>(), manager);
            banner.setAdapter(adapter);
            indicator.setViewPager(banner);
            headerMainHomeBinding.btnChat.setOnClickListener(this);
            headerMainHomeBinding.btnSettings.setOnClickListener(this);
            mHeaderBinding.btnReport.setOnClickListener(this);
            mHeaderBinding.btnBack.setOnClickListener(this);
            mHeaderBinding.btnCreateskill.setOnClickListener(this);
            mHeaderBinding.btnMyOrder.setOnClickListener(this);
            mHeaderBinding.btnMyRemainder.setOnClickListener(this);
        }

        public void bind(User user) {
            this.user = user;
            mHeaderBinding.setUser(user);
            mHeaderBinding.executePendingBindings();
            if (user.getUid() != User.read(mContext).getUid()) {
                mHeaderBinding.btnCreateskill.setVisibility(View.GONE);
                mHeaderBinding.bottom.setVisibility(View.GONE);
                mHeaderBinding.btnSettings.setVisibility(View.GONE);
                mHeaderBinding.btnChat.setVisibility(View.GONE);
                mHeaderBinding.btnReport.setVisibility(View.VISIBLE);
                mHeaderBinding.btnBack.setVisibility(View.VISIBLE);
            } else {
                mHeaderBinding.bottom.setVisibility(View.VISIBLE);
                if (user.getInfo().getSellMsg() == 0 && user.getInfo().getBuyMsg() == 0) {
                    mHeaderBinding.msgOrderTopic.setVisibility(View.GONE);
                } else {
                    mHeaderBinding.msgOrderTopic.setVisibility(View.VISIBLE);
                }
                if (user.getInfo().getMoneyMsg() == 0) {
                    mHeaderBinding.msgMoneyTopic.setVisibility(View.GONE);
                } else {
                    mHeaderBinding.msgMoneyTopic.setVisibility(View.VISIBLE);
                }
                mHeaderBinding.btnSettings.setVisibility(View.VISIBLE);
                mHeaderBinding.btnChat.setVisibility(View.VISIBLE);
                mHeaderBinding.btnBack.setVisibility(View.GONE);
                mHeaderBinding.btnReport.setVisibility(View.GONE);
            }
            List<android.support.v4.app.Fragment> fragments = new ArrayList<>(2);
            fragments.add(UserHeaderFrist.newInstance(user));
            fragments.add(UserHeaderSecond.newInstance(user));
            if(fragments.size()<2){
                mHeaderBinding.titles.setVisibility(View.GONE);
            }else {
                mHeaderBinding.titles.setVisibility(View.VISIBLE);
            }
            adapter.setViews(fragments);
            adapter.notifyDataSetChanged();
            indicator.notifyDataSetChanged();
            banner.setCurrentItem(0);

        }


    }

    /***
     *
     */
    public class ItemViewHolder extends SkillListHomeAdapter.ViewHolder {
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
            binding.btnEdit.setOnClickListener(this);
            binding.btnShare.setOnClickListener(this);
            binding.btnChangeState.setOnClickListener(this);
            binding.btnDelete.setOnClickListener(this);
            binding.btnUpdate.setOnClickListener(this);
            binding.userAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            //create by xu 08-06
            binding.btnReview.setOnClickListener(this);
            //end
            user = User.read(itemView.getContext());
        }

        public void bind(Skill item) {
            closeWithAnim();
            if(getLayoutPosition()==datas.size()-1 ){
                binding.bottom.setVisibility(View.VISIBLE);
            }else {
                binding.bottom.setVisibility(View.GONE);
            }
            this.data = item;
            binding.setSkill(item);
            binding.executePendingBindings();
            isMe = item.getUid() == user.getUid();
            if (isMe) {
                binding.btnEdit.setVisibility(View.VISIBLE);
                binding.bottomDiv.setVisibility(View.VISIBLE);
            } else {
                binding.btnEdit.setVisibility(View.GONE);
                binding.bottomDiv.setVisibility(View.GONE);
            }

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

    class ViewPagerAdapter extends FragmentPagerAdapter implements IconPagerAdapter {

        public void setViews(List<android.support.v4.app.Fragment> views) {
            this.views = views;
        }

        private List<android.support.v4.app.Fragment> views;

        public ViewPagerAdapter(List<android.support.v4.app.Fragment> views, FragmentManager manager) {
            super(manager);
            this.views = views;
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
        public android.support.v4.app.Fragment getItem(int position) {
            return views.get(position);
        }
    }

}



