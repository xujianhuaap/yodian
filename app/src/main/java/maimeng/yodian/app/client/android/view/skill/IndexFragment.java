package maimeng.yodian.app.client.android.view.skill;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.model.skill.Banner;
import maimeng.yodian.app.client.android.model.skill.DataNode;
import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.model.skill.Theme;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.SkillResponse;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.view.BaseFragment;
import maimeng.yodian.app.client.android.view.chat.ChatMainActivity;
import maimeng.yodian.app.client.android.view.dialog.WaitDialog;

/**
 * Created by android on 15-10-10.
 */
public class IndexFragment extends BaseFragment implements Callback<SkillResponse>, EMEventListener {
    private android.support.design.widget.TabLayout mTabNav;
    private android.support.v4.view.ViewPager mPager;
    private SkillService service;
    private FragmentAdapter adapter;
    private TypeAdater typeAdapter;
    private WaitDialog dialog;
    private View mBtnChat;
    private View mBtnPull;
    private RecyclerView mTypeList;

    public static IndexFragment newInstance() {
        return new IndexFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_index, container, false);
        service = Network.getService(SkillService.class);
        return view;
    }

    private final boolean rotationed[] = {false};

    public void toggleTypePop(View view) {
        final View root = findViewById(R.id.pop_layout);
        final TranslateAnimation rootAnim;
        if (root.getVisibility() != View.VISIBLE) {
            findViewById(R.id.pull_overy).setVisibility(View.VISIBLE);
            rootAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            rootAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    LogUtil.d("amin", "show :%s", root.getVisibility() == View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        } else {
            rootAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    -1.0f);
            rootAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    findViewById(R.id.pull_overy).setVisibility(View.INVISIBLE);
                    LogUtil.d("amin", "close :%s", root.getVisibility() == View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        rootAnim.setDuration(300);

        if (mBtnPull == view) {
            final ObjectAnimator btnAnim;
            if (rotationed[0]) {
                btnAnim = ObjectAnimator.ofFloat(view, View.ROTATION, 180f, 360f);
            } else {
                btnAnim = ObjectAnimator.ofFloat(view, View.ROTATION, 0f, 180f);
            }
            btnAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    rotationed[0] = !rotationed[0];
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            btnAnim.setDuration(300);
            btnAnim.start();
        }
        root.startAnimation(rootAnim);
        if (root.getVisibility() != View.VISIBLE) {
            root.setVisibility(View.VISIBLE);
        } else {
            root.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mPager = (ViewPager) view.findViewById(R.id.pager);
        mBtnPull = findViewById(R.id.btn_pull);
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTypePop(v);
            }
        };
        mBtnPull.setOnClickListener(clickListener);
        findViewById(R.id.pull_overy).setOnClickListener(clickListener);
        this.mTabNav = (TabLayout) view.findViewById(R.id.tab_nav);
        mTypeList = findViewById(R.id.typeList);
        mTypeList.setOnClickListener(clickListener);
        mBtnChat = view.findViewById(R.id.btn_chat);
        mBtnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChatMainActivity.class));
            }
        });
        mTabNav.setTabMode(TabLayout.MODE_SCROLLABLE);
        adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());
        mPager.setOffscreenPageLimit(6);
        mPager.setAdapter(adapter);
        mTabNav.setupWithViewPager(mPager);
        mTypeList.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        typeAdapter = new TypeAdater();
        mTypeList.setAdapter(typeAdapter);
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
                typeAdapter.refresh(navs);
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

    public boolean isShowPop() {
        return findViewById(R.id.pop_layout).getVisibility() == View.VISIBLE;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean result = isShowPop();
        if (result) {
            toggleTypePop(mBtnChat);
        }
        return result;
    }

    private final class TypeAdater extends RecyclerView.Adapter<ViewHolder> {
        private final List<Theme> datas;
        private int offset = 0;

        public TypeAdater() {
            datas = new ArrayList<>();
        }

        public void refresh(List<Theme> list) {
            datas.clear();
            if (list != null) {
                datas.addAll(list);
            }
            int div = datas.size() % 4;
            offset = div != 0 ? 4 - div : 0;
            for (int i = 0; i < offset; i++) {
                datas.add(new Theme());
            }
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater(null).inflate(R.layout.item_index_pull, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(datas.get(position));
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

    private final class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tv;

        public Theme getTheme() {
            return theme;
        }

        private Theme theme;

        public ViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.name);
            tv.setOnClickListener(this);
        }

        public void bind(Theme theme) {
            if (getLayoutPosition() >= typeAdapter.datas.size() - typeAdapter.offset) {
                tv.setVisibility(View.INVISIBLE);
            } else {
                tv.setVisibility(View.VISIBLE);
            }
            this.theme = theme;
            this.tv.setText(theme.getName());
            if (getLayoutPosition() == typeAdapter.datas.size() - 1) {
                int pdTop = itemView.getPaddingTop();
                itemView.setPadding(0, pdTop, 0, pdTop);
            }
        }

        @Override
        public void onClick(View v) {
            mPager.setCurrentItem(getLayoutPosition(), false);
            toggleTypePop(v);
        }
    }

    public long key(int index, int id) {
        return index | (long) id << 32;
    }

    public int index(long value) {
        return (int) (0x0000ffff & value);
    }

    public int id(long value) {
        return (int) (value >> 32);
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
            long key = key(position, theme.getScid());
            if (fragmentMap.containsKey(key)) {
                return fragmentMap.get(key);
            } else {
                if (position == 0) {
                    Fragment fragment = SkillFragment.newInstance(theme.getName(), theme.getScid(), mFristList, mBanner);
                    fragmentMap.put(key, fragment);
                    return fragment;
                } else {
                    SkillFragment fragment = SkillFragment.newInstance(theme.getName(), theme.getScid());
                    fragmentMap.put(key, fragment);
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

    @Override
    public void onResume() {
        super.onResume();
        int unread = EMChatManager.getInstance().getUnreadMsgsCount();
        if (unread > 0) {
            findViewById(R.id.miss_msg_count).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.miss_msg_count).setVisibility(View.INVISIBLE);
        }
        if (User.read(getActivity()).isPushOn()) {
            EMChatManager.getInstance().registerEventListener(this);
        } else {
            EMChatManager.getInstance().unregisterEventListener(this);
        }

        refreshMissMsgIcon();
    }

    @Override
    public void onPause() {
        super.onPause();
        EMChatManager.getInstance().unregisterEventListener(this);
    }

    private void refreshMissMsgIcon() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int unread = EMChatManager.getInstance().getUnreadMsgsCount();
                if (!User.read(getActivity()).isPushOn()) {
                    unread = 0;
                }
                if (unread > 0) {
                    findViewById(R.id.miss_msg_count).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.miss_msg_count).setVisibility(View.INVISIBLE);
                }

            }
        });
    }

    @Override
    public void onEvent(EMNotifierEvent event) {

        switch (event.getEvent()) {
            case EventNewMessage:
            case EventReadAck:
                refreshMissMsgIcon();
                break;
        }


    }
}
