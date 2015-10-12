package maimeng.yodian.app.client.android.view.skill;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.model.skill.Banner;
import maimeng.yodian.app.client.android.model.skill.DataNode;
import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.model.skill.Theme;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.SkillResponse;
import maimeng.yodian.app.client.android.network.response.SkillUserResponse;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.view.BaseFragment;
import maimeng.yodian.app.client.android.view.dialog.WaitDialog;

/**
 * Created by android on 15-10-10.
 */
public class IndexFragment extends BaseFragment implements Callback<SkillResponse> {
    private android.support.design.widget.TabLayout mTabNav;
    private android.support.v4.view.ViewPager mPager;
    private SkillService service;
    private FragmentAdapter adapter;
    private WaitDialog dialog;

    public static IndexFragment newInstance() {
        return new IndexFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_index, container, false);
        service = Network.getService(SkillService.class);
        this.mPager = (ViewPager) view.findViewById(R.id.pager);
        this.mTabNav = (TabLayout) view.findViewById(R.id.tab_nav);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTabNav.setTabMode(TabLayout.MODE_SCROLLABLE);
        adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabNav));
        mPager.setOffscreenPageLimit(6);
        mPager.setAdapter(adapter);
        mTabNav.setupWithViewPager(mPager);
        service.choose(1, 0, this);
    }

    @Override
    public void start() {
        dialog = WaitDialog.show(getActivity());
    }

    @Override
    public void success(SkillResponse res, Response response) {
        if (res.isSuccess()) {
            DataNode data = res.getData();
            List<Theme> navs = data.getCategory();
            if (navs != null) {
                mTabNav.removeAllTabs();
                for (Theme theme : navs) {
                    mTabNav.addTab(mTabNav.newTab().setText(theme.getName()));
                }
                adapter.refresh(navs, data.getList(), data.getBanner());
            }
        } else {
            res.showMessage(getContext());
        }

    }

    @Override
    public void failure(HNetError hNetError) {
        ErrorUtils.checkError(getContext(), hNetError);
    }

    @Override
    public void end() {
        if (dialog != null) dialog.dismiss();
    }

    private final class FragmentAdapter extends FragmentPagerAdapter {

        private final ArrayList<Theme> navs;
        private final ArrayList<Skill> mFristList;
        private final ArrayList<Banner> mBanner;
        private final Map<Long, Fragment> fragmentMap = new HashMap<>();

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
            navs = new ArrayList<>();
            mFristList = new ArrayList<>();
            mBanner = new ArrayList<>();
        }

//        @Override
//        public CharSequence getPageTitle(int position) {
//            final Theme theme = navs.get(position);
//            return theme.getName();
//        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            final Theme theme = navs.get(position);
            if (fragmentMap.containsKey(theme.getScid())) {
                return fragmentMap.get(theme.getScid());
            } else {
                if (position == 0) {
                    Fragment fragment = SkillFragment.newInstance(theme.getName(), theme.getScid(), mFristList, mBanner);
                    fragmentMap.put(theme.getScid(), fragment);
                    return fragment;
                } else {
                    SkillFragment fragment = SkillFragment.newInstance(theme.getName(), theme.getScid());
                    fragmentMap.put(theme.getScid(), fragment);
                    return fragment;
                }
            }
        }

        @Override
        public int getCount() {
            return navs.size();
        }

        public void refresh(List<Theme> navs, List<Skill> fristList, List<Banner> banner) {
            this.navs.clear();
            this.navs.addAll(navs);
            this.mFristList.clear();
            this.mFristList.addAll(fristList);
            this.mBanner.clear();
            this.mBanner.addAll(banner);
            notifyDataSetChanged();
        }
    }
}
