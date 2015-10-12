package maimeng.yodian.app.client.android.view.skill;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easemob.applib.controller.HXSDKHelper;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.adapter.AbstractAdapter;
import maimeng.yodian.app.client.android.adapter.SkillListIndexAdapter;
import maimeng.yodian.app.client.android.chat.DemoHXSDKHelper;
import maimeng.yodian.app.client.android.chat.activity.ChatActivity;
import maimeng.yodian.app.client.android.chat.db.UserDao;
import maimeng.yodian.app.client.android.chat.domain.RobotUser;
import maimeng.yodian.app.client.android.common.PullHeadView;
import maimeng.yodian.app.client.android.entry.skillseletor.BannerViewEntry;
import maimeng.yodian.app.client.android.entry.skillseletor.ItemViewEntry;
import maimeng.yodian.app.client.android.entry.skillseletor.ViewEntry;
import maimeng.yodian.app.client.android.model.skill.Banner;
import maimeng.yodian.app.client.android.model.skill.DataNode;
import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.model.skill.Theme;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.response.SkillResponse;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.view.BaseFragment;
import maimeng.yodian.app.client.android.view.MainTab2Activity;
import maimeng.yodian.app.client.android.view.WebViewActivity;
import maimeng.yodian.app.client.android.view.dialog.AlertDialog;
import maimeng.yodian.app.client.android.view.dialog.ShareDialog;
import maimeng.yodian.app.client.android.view.skill.proxy.ActivityProxyController;
import maimeng.yodian.app.client.android.view.user.UserHomeActivity;
import maimeng.yodian.app.client.android.widget.EndlessRecyclerOnScrollListener;
import maimeng.yodian.app.client.android.widget.ListLayoutManager;
import maimeng.yodian.app.client.android.widget.PagerRecyclerView;

/**
 * Created by android on 2015/10/12.
 */
public class SkillFragment extends BaseFragment implements PtrHandler, AbstractAdapter.ViewHolderClickListener<SkillListIndexAdapter.BaseViewHolder>, Callback<SkillResponse> {
    private maimeng.yodian.app.client.android.widget.PagerRecyclerView mRecyclerView;
    private in.srain.cube.views.ptr.PtrFrameLayout mRefreshLayout;
    private int page = 1;
    private SkillListIndexAdapter adapter;
    private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    private SkillService service;
    private Handler handler;
    private int mEditPostion;
    private String typeName;
    private long typeId;

    public static SkillFragment newInstance(String name, long scid) {
        final SkillFragment skillFragment = new SkillFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putLong("id", scid);
        skillFragment.setArguments(args);
        return skillFragment;
    }

    public static Fragment newInstance(String name, long scid, ArrayList<Skill> datas, ArrayList<Banner> mBanner) {
        final SkillFragment skillFragment = new SkillFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putLong("id", scid);
        args.putSerializable("list", datas);
        args.putSerializable("banners", mBanner);
        skillFragment.setArguments(args);
        return skillFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        service = Network.getService(SkillService.class);
        handler = new Handler(Looper.getMainLooper());
        this.typeName = getArguments().getString("name");
        this.typeId = getArguments().getLong("id");
        View view = inflater.inflate(R.layout.fragment_skill, container, false);
        this.mRefreshLayout = (PtrFrameLayout) view.findViewById(R.id.refresh_layout);
        this.mRecyclerView = (PagerRecyclerView) view.findViewById(R.id.recyclerView);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRefreshLayout.setPtrHandler(this);
        StoreHouseHeader header = PullHeadView.create(getContext());
        header.setTextColor(0x000000);
        mRefreshLayout.addPtrUIHandler(header);
        mRefreshLayout.setHeaderView(header);
        ListLayoutManager layout = new ListLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layout);
        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(layout) {
            @Override
            public void onLoadMore() {
                page++;
                syncRequest();
            }
        };
        mRecyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
        adapter = new SkillListIndexAdapter(this, this, mRefreshLayout);
        mRecyclerView.setAdapter(adapter);
        final ArrayList<Skill> datas = (ArrayList<Skill>) getArguments().getSerializable("list");
        final ArrayList<Banner> banners = (ArrayList<Banner>) getArguments().getSerializable("banners");

        if (datas != null && banners != null) {
            callRefreshAdapter(datas, banners);
        } else {
            mRefreshLayout.autoRefresh();
        }
    }

    private void callRefreshAdapter(List<Skill> list, List<Banner> banners) {
        final List<ViewEntry> entries;
        if (page == 1 && typeId == 0) {
            entries = new ArrayList<>(list.size() + 2);
            entries.add(new BannerViewEntry(banners));
        } else {
            entries = new ArrayList<>(list.size());
        }

        for (Skill skill : list) {
            entries.add(new ItemViewEntry(skill));
        }
        adapter.reload(entries, page != 1);
        adapter.notifyDataSetChanged();
    }

    public void syncRequest() {
        service.choose(page, typeId, this);
    }

    @Override
    public void start() {

    }

    @Override
    public void success(SkillResponse res, Response response) {
        if (res.isSuccess()) {
            DataNode data = res.getData();
            List<Skill> list = data.getList();
            callRefreshAdapter(list, data.getBanner());
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
        mRefreshLayout.refreshComplete();
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View content, View header) {
        // 默认实现，根据实际情况做改动
        return PtrDefaultHandler.checkContentCanBePulledDown(ptrFrameLayout, content, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
        page = 1;
        syncRequest();

    }

    @Override
    public void onItemClick(final SkillListIndexAdapter.BaseViewHolder h, int postion) {
        if (h instanceof SkillListIndexAdapter.ItemViewHolder) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Pair<View, String> back = Pair.create((View) ((MainTab2Activity) getActivity()).getFloatButton(), "back");
                    Skill skill = ((SkillListIndexAdapter.ItemViewHolder) h).getData();
                    if (skill.getStatus() == 0) {
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), back);
                        ActivityCompat.startActivity(getActivity(), new Intent(getContext(), SkillDetailsActivity.class).putExtra("skill", skill), options.toBundle());
                    }

                }
            }, 200);
        }
    }

    @Override
    public void onClick(SkillListIndexAdapter.BaseViewHolder h, View clickItem, final int postion) {


        if (h instanceof SkillListIndexAdapter.ItemViewHolder) {
            final SkillListIndexAdapter.ItemViewHolder holder = (SkillListIndexAdapter.ItemViewHolder) h;
            final Skill skill = holder.getData();
            if (clickItem == holder.getBinding().btnShare) {
                Skill data = skill;
                ShareDialog.show(getActivity(), new ShareDialog.ShareParams(data, data.getQrcodeUrl(), data.getUid(), data.getNickname(), ""), 1);
            } else if (clickItem == holder.getBinding().userAvatar) {
                UserHomeActivity.show(getActivity(), skill.getUid(), holder.getDefaultAvatar(), skill.getNickname(), ((MainTab2Activity) getActivity()).getFloatButton(), null,
                        holder.getBinding().userNickname);
            } else if (clickItem == holder.getBinding().btnUpdate) {
                Intent intent = new Intent(getActivity(), CreateOrEditSkillActivity.class);
                intent.putExtra("skill", skill);
                startActivityForResult(intent, ActivityProxyController.REQUEST_EDIT_SKILL);
                mEditPostion = postion;
                holder.closeWithAnim();
            } else if (clickItem == holder.getBinding().btnDelete) {
                AlertDialog.newInstance("提示", "确定要删除吗?").setPositiveListener(new AlertDialog.PositiveListener() {
                    @Override
                    public void onPositiveClick(final DialogInterface dialog) {
                        dialog.dismiss();
                        service.delete(skill.getId(), new ToastCallback(getActivity()) {
                            @Override
                            public void success(ToastResponse res, Response response) {
                                super.success(res, response);
                                if (res.isSuccess()) {
                                    adapter.remove(postion);
                                }
                            }

                            @Override
                            public void end() {
                                super.end();
                            }
                        });
                    }

                    @Override
                    public String positiveText() {
                        return getResources().getString(android.R.string.ok);
                    }
                }).setNegativeListener(new AlertDialog.NegativeListener() {
                    @Override
                    public void onNegativeClick(DialogInterface dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public String negativeText() {
                        return getResources().getString(android.R.string.cancel);
                    }
                }).show(getActivity().getFragmentManager(), "delete_dialog");

            } else if (clickItem == holder.getBinding().btnChangeState) {
                service.up(skill.getId(), skill.getStatus(), new ToastCallback(getActivity()) {
                    @Override
                    public void success(ToastResponse res, Response response) {
                        super.success(res, response);
                        skill.setStatus(skill.getStatus() == 0 ? 1 : 0);
                        holder.closeWithAnim();
                    }
                });
            } else if (clickItem == holder.getBinding().btnBottom) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("skill", holder.getData());
                intent.putExtra("uid", holder.getData().getUid());
                Map<String, RobotUser> robotMap = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getRobotList();
                String chatLoginName = skill.getChatLoginName();
                if (robotMap.containsKey(chatLoginName)) {
                    intent.putExtra("userId", chatLoginName);
                    startActivity(intent);
                } else {

                    RobotUser robot = new RobotUser();
                    robot.setId(skill.getUid() + "");
                    robot.setUsername(chatLoginName);
                    robot.setNick(skill.getNickname());
                    robot.setAvatar(skill.getAvatar());
                    robot.setWechat(skill.getWeichat());


                    maimeng.yodian.app.client.android.chat.domain.User user = new maimeng.yodian.app.client.android.chat.domain.User();
                    user.setId(skill.getUid() + "");
                    user.setUsername(chatLoginName);
                    user.setNick(skill.getNickname());
                    user.setAvatar(skill.getAvatar());
                    user.setWechat(skill.getWeichat());


                    // 存入内存
                    ((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveOrUpdate(skill.getChatLoginName(), robot);
                    ((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveOrUpdate(skill.getChatLoginName(), user);
                    // 存入db
                    UserDao dao = new UserDao(getActivity());
                    dao.saveOrUpdate(user);
                    dao.saveOrUpdate(robot);
                    intent.putExtra("userId", chatLoginName);
                    intent.putExtra("userNickname", skill.getNickname());
                    startActivity(intent);
                }
            }
        } else if (SkillListIndexAdapter.BannerViewHolder.class.isInstance(h)) {
            clickBanner(((SkillListIndexAdapter.BannerViewHolder) h));
        }
    }

    private void clickBanner(SkillListIndexAdapter.BannerViewHolder holder) {
        int current = holder.currentPage % holder.list.banners.size();
        Banner banner = holder.list.banners.get(current);
        if (banner.getType() == 3) {
            Pair<View, String> back = Pair.create((View) ((MainTab2Activity) getActivity()).getFloatButton(), "back");
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), back);
            ActivityCompat.startActivity(getActivity(), new Intent(getActivity(), SkillDetailsActivity.class).putExtra("sid", Long.parseLong(banner.getValue())), options.toBundle());
        } else if (banner.getType() == 2) {
            Pair<View, String> back = Pair.create((View) ((MainTab2Activity) getActivity()).getFloatButton(), "back");
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), back);
            ActivityCompat.startActivity(getActivity(), new Intent(getActivity(), UserHomeActivity.class).putExtra("uid", Long.parseLong(banner.getValue())), options.toBundle());
        } else if (banner.getType() == 1) {
            Pair<View, String> back = Pair.create((View) ((MainTab2Activity) getActivity()).getFloatButton(), "back");
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), back);
            ActivityCompat.startActivity(getActivity(), WebViewActivity.newIntent(getActivity(), banner.getValue()), options.toBundle());

        }
    }
}
