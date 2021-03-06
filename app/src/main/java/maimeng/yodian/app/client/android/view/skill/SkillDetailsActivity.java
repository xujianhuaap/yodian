package maimeng.yodian.app.client.android.view.skill;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.easemob.applib.controller.HXSDKHelper;
import com.umeng.analytics.MobclickAgent;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;
import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SimpleTimeZone;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.adapter.AbstractHeaderAdapter;
import maimeng.yodian.app.client.android.adapter.RmarkListAdapter;
import maimeng.yodian.app.client.android.chat.DemoHXSDKHelper;
import maimeng.yodian.app.client.android.chat.activity.ChatActivity;
import maimeng.yodian.app.client.android.chat.domain.RobotUser;
import maimeng.yodian.app.client.android.common.PullHeadView;
import maimeng.yodian.app.client.android.common.UEvent;
import maimeng.yodian.app.client.android.databinding.ActivitySkillDetailsBinding;
import maimeng.yodian.app.client.android.databinding.ViewHeaderPlaceholderBinding;
import maimeng.yodian.app.client.android.model.Lottery;
import maimeng.yodian.app.client.android.model.Rmark;
import maimeng.yodian.app.client.android.model.chat.ChatUser;
import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.response.RmarkListResponse;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.service.CommonService;
import maimeng.yodian.app.client.android.network.service.SkillService;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.view.common.AbstractActivity;
import maimeng.yodian.app.client.android.view.common.WebViewActivity;
import maimeng.yodian.app.client.android.view.deal.OrderDetailActivity;
import maimeng.yodian.app.client.android.view.deal.PayWrapperActivity;
import maimeng.yodian.app.client.android.view.dialog.AlertDialog;
import maimeng.yodian.app.client.android.view.dialog.ShareDialog;
import maimeng.yodian.app.client.android.view.dialog.WaitDialog;
import maimeng.yodian.app.client.android.view.user.UserHomeActivity;

/**
 * Created by android on 2015/7/22.
 */
public class SkillDetailsActivity extends AbstractActivity implements PtrHandler, AppBarLayout.OnOffsetChangedListener, Callback<RmarkListResponse>, AbstractHeaderAdapter.ViewHolderClickListener<RmarkListAdapter.ViewHolder>, View.OnClickListener, RmarkListAdapter.ActionListener {
    private static final String LOG_TAG = SkillDetailsActivity.class.getName();
    private static final int REQEUST_RMARK_ADD = 0x1001;
    private static final int REQEUST_RMARK_AUTH = 0x1002;
    private static final int REQEUST_PAY = 0x1003;//如果是支付成功返回的就进入订单界面
    private static final int MENU_ID_SHARE = 0x5001;
    private SkillService service;
    private long sid;
    private int page = 1;
    private RmarkListAdapter adapter;
    private View mPlaceHolderView;
    private ViewHeaderPlaceholderBinding headBinding;
    private ActivitySkillDetailsBinding binding;
    private Skill skill;
    private User user;
    private boolean isMe;
    private WaitDialog dialog;
    private View noSkillRmark;
    private boolean canBuy = true;


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobclickAgent.onEvent(this, UEvent.SKILL_DETIAL);
        user = User.read(this);
        noSkillRmark = View.inflate(this, R.layout.view_header_exception, null);
        service = Network.getService(SkillService.class);
        binding = bindView(R.layout.activity_skill_details);
        headBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.view_header_placeholder, binding.recyclerView, false);
        ViewCompat.setTransitionName(headBinding.btnBuySkill, "back");
        headBinding.userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMe) {
                    UserHomeActivity.show(SkillDetailsActivity.this, skill.getUid());
                }

            }
        });
        headBinding.btnContact.setOnClickListener(this);
        mPlaceHolderView = headBinding.getRoot();
        binding.recyclerView.addHeaderView(mPlaceHolderView);
        adapter = new RmarkListAdapter(this, this);
        binding.refreshLayout.setPtrHandler(this);
        StoreHouseHeader header = PullHeadView.create(this);
        binding.refreshLayout.addPtrUIHandler(header);
        binding.refreshLayout.setHeaderView(header);

        binding.recyclerView.setAdapter(adapter);
        if (getIntent().hasExtra("skill")) {
            Skill skill = Parcels.unwrap(getIntent().getParcelableExtra("skill"));
            sid = skill.getId();
            isMe = skill.getUid() == user.getUid();
            if (isMe) {
                setUIWhenIsMe();
            } else {
                if(skill.getAllow_sell()==1){
                    headBinding.btnContact.setVisibility(View.VISIBLE);
                    headBinding.divinder.setVisibility(View.VISIBLE);
                    headBinding.btnBuySkill.setVisibility(View.VISIBLE);

                }else{
                    headBinding.btnContact.setVisibility(View.VISIBLE);
                    headBinding.divinder.setVisibility(View.GONE);
                    headBinding.btnBuySkill.setVisibility(View.GONE);
                }
            }
            binding.refreshLayout.autoRefresh();
        } else {
            sid = getIntent().getLongExtra("sid", 0);
            binding.refreshLayout.autoRefresh();
        }

        headBinding.btnBuySkill.setOnClickListener(this);
    }

    /***
     *
     */
    private void setUIWhenIsMe() {
        headBinding.btnContact.setVisibility(View.GONE);
        headBinding.divinder.setVisibility(View.GONE);
        headBinding.btnBuySkill.setVisibility(View.VISIBLE);
        headBinding.btnBuySkill.setImageResource(R.mipmap.ic_remark_add);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQEUST_PAY) {
            canBuy = true;
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == REQEUST_RMARK_ADD) {
                binding.refreshLayout.autoRefresh();
            } else if (requestCode == REQEUST_PAY) {//如果是支付成功返回的就进入订单界面
                if (data != null) {
                    Lottery lottery = Parcels.unwrap(data.getParcelableExtra("lottery"));
                    LogUtil.d(SkillDetailsActivity.class.getName(), "lottery" + lottery.getIsLottery());
                    LogUtil.d(SkillDetailsActivity.class.getName(), "lottery" + lottery.getLotUrl());
                    OrderDetailActivity.show(this, lottery);
                }
            }
        }
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {

    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View content, View header) {
        // 默认实现，根据实际情况做改动
        return PtrDefaultHandler.checkContentCanBePulledDown(ptrFrameLayout, content, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
        page = 1;
        sync();
    }

    private void sync() {
        service.rmark_list(sid, page, this);
    }

    @Override
    public void start() {
        dialog = WaitDialog.show(this);
    }

    @Override
    public void success(RmarkListResponse res, Response response) {
        if (res.isValidateAuth(this) && res.isSuccess()) {
            List<Rmark> list = res.getData().getList();

            if (list.size() > 0) {
                Rmark rmark = list.get(0);
            }

            skill = res.getData().getDetail();
            isMe = skill.getUid() == user.getUid();
            if (isMe) {
                setUIWhenIsMe();
            } else {
                if(skill.getAllow_sell()==1){
                    headBinding.btnContact.setVisibility(View.VISIBLE);
                    headBinding.divinder.setVisibility(View.VISIBLE);
                    headBinding.btnBuySkill.setVisibility(View.VISIBLE);

                }else{
                    headBinding.btnContact.setVisibility(View.VISIBLE);
                    headBinding.divinder.setVisibility(View.GONE);
                    headBinding.btnBuySkill.setVisibility(View.GONE);
                }
            }
            if (skill.getAllow_sell() != 1) {
                headBinding.skillAllowSell.setVisibility(View.GONE);
            } else {
                headBinding.skillAllowSell.setVisibility(View.VISIBLE);
            }

            if (skill.isSelector()) {
                headBinding.skillSlector.setVisibility(View.VISIBLE);
            }
            headBinding.setSkill(skill);
            if (skill.isXiajia()) {
                headBinding.skillStatus.setVisibility(View.VISIBLE);
            } else {
                headBinding.skillStatus.setVisibility(View.INVISIBLE);
            }
            boolean actived = skill.isActive();
            if (actived) {
                headBinding.iconActivie.setVisibility(View.VISIBLE);
                headBinding.iconActivie.setText(skill.getActive_price());
                headBinding.activeBanner.setVisibility(View.VISIBLE);
                headBinding.activeBanner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MobclickAgent.onEvent(v.getContext(), UEvent.SKILL_IN_LOTTORY_TO_HTML5);
                        WebViewActivity.show(v.getContext(), skill.getActiveUrl());
                    }
                });
                Calendar systemC = Calendar.getInstance();
                Calendar startC = Calendar.getInstance();
                systemC.setTimeInMillis(skill.getSystime() * 1000);
                startC.setTimeInMillis(skill.getStarttime() * 1000);
                long count = startC.getTimeInMillis() - systemC.getTimeInMillis();
                if (count <= 24 * 60 * 60 * 1000) {
                    CountDownTimer timer = new CountDownTimer(count, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                            fmt.setTimeZone(new SimpleTimeZone(0, "UTC"));
                            headBinding.activeTime.setText(fmt.format(millisUntilFinished));
                        }

                        @Override
                        public void onFinish() {
                            headBinding.activeTime.setText("开始抢购");
                        }
                    };
                    timer.start();
                }

            } else {
                headBinding.iconActivie.setVisibility(View.GONE);
                headBinding.activeBanner.setVisibility(View.GONE);
            }
            binding.setSkill(skill);
            Spanned text = Html.fromHtml(getResources().getString(R.string.lable_price, skill.getPrice(), skill.getUnit()));
            headBinding.price.setText(text);

            binding.recyclerView.removeHeaderView(noSkillRmark);
            if (list.size() > 0 || page > 1) {
            } else {
                ////pic_no_skill_rmark
                binding.recyclerView.addHeaderView(noSkillRmark);

            }

            adapter.reload(list, page != 1);
            adapter.notifyDataSetChanged();
        } else {
            res.isValidateAuth(this, REQEUST_RMARK_AUTH);
            res.showMessage(this);
        }
    }

    @Override
    public void failure(HNetError hNetError) {

    }

    @Override
    public void end() {
        if (dialog != null) {
            dialog.dismiss();
        }
        binding.refreshLayout.refreshComplete();

    }

    @Override
    public void onItemClick(RmarkListAdapter.ViewHolder holder, int postion) {

    }

    @Override
    public void onClick(RmarkListAdapter.ViewHolder holder, View clickItem, int postion) {

    }

    @Override
    public void onClick(View v) {
        if (v == headBinding.btnContact) {
            if (!isMe) {
                Map<String, RobotUser> robotMap = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getRobotList();
                String chatLoginName = skill.getChatLoginName();
                if (!robotMap.containsKey(chatLoginName)) {
                    RobotUser robot = new RobotUser();
                    robot.setId(skill.getUid());
                    robot.setUsername(chatLoginName);
                    robot.setNick(skill.getNickname());
                    robot.setAvatar(skill.getAvatar());
                    robot.setWechat(skill.getWeichat());
                    robot.setQq(skill.getQq());
                    robot.setMobile(skill.getContact());

                    maimeng.yodian.app.client.android.chat.domain.User user = new maimeng.yodian.app.client.android.chat.domain.User();
                    user.setId(skill.getUid());
                    user.setUsername(chatLoginName);
                    user.setNick(skill.getNickname());
                    user.setAvatar(skill.getAvatar());
                    user.setWechat(skill.getWeichat());
                    user.setQq(skill.getQq());
                    user.setMobile(skill.getContact());
                    ((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveOrUpdate(robot);
                    ((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveOrUpdate(user);
                }
                MobclickAgent.onEvent(this, UEvent.CONTACT_TA);
                ChatActivity.show(SkillDetailsActivity.this, skill, new ChatUser(chatLoginName, skill.getUid(), skill.getNickname()));
            }
        } else if (v == headBinding.btnBuySkill) {
            if (isMe) {
                MobclickAgent.onEvent(this, UEvent.SKILL_DETAIL_PUBLISH_RMARK);
                RmarkPublishActivity.show(this, skill, headBinding.btnBuySkill, REQEUST_RMARK_ADD);
            } else {
                MobclickAgent.onEvent(v.getContext(), UEvent.SKILL_DETAIL_PAY_CLICK);
                if (canBuy) {
                    PayWrapperActivity.show(SkillDetailsActivity.this, skill, REQEUST_PAY);
                    canBuy = false;
                }

            }
        }
    }

    @Override
    public void onDelete(final RmarkListAdapter.ViewHolder holder) {
        MobclickAgent.onEvent(this, UEvent.RMARK_DELETE);
        AlertDialog.newInstance("提示", "确定要删除吗?").setPositiveListener(new AlertDialog.PositiveListener() {
            @Override
            public void onPositiveClick(final DialogInterface dialog) {
                dialog.dismiss();
                service.delete_rmark(holder.getBinding().getRmark().getScid(), new ToastCallback(SkillDetailsActivity.this) {
                    @Override
                    public void success(ToastResponse res, Response response) {
                        super.success(res, response);
                        if (res.isSuccess()) {
                            adapter.remove(holder.getPosition());
                        }
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
        }).show(getFragmentManager(), "delete_dialog");


    }

    @Override
    public void onReport(final RmarkListAdapter.ViewHolder holder) {

        AlertDialog.newInstance("提示", "确定要举报吗?").setPositiveListener(new AlertDialog.PositiveListener() {
            @Override
            public void onPositiveClick(final DialogInterface dialog) {
                dialog.dismiss();
                Network.getService(CommonService.class).report(2, 0, holder.getBinding().getRmark().getScid(), 0, new ToastCallback(SkillDetailsActivity.this));
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
        }).show(getFragmentManager(), "delete_dialog");


    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    protected void initToolBar(Toolbar toolbar) {
        super.initToolBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setDisplayUseLogoEnabled(true);
//            actionBar.setLogo(R.drawable.ic_go_back);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_go_back);
            mTitle.setTextColor(Color.WHITE);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add(0, MENU_ID_SHARE, 10, null);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItem.setIcon(R.mipmap.btn_share);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                ActivityCompat.finishAfterTransition(SkillDetailsActivity.this);
                return true;
            }
            case MENU_ID_SHARE: {
                ShareDialog.show(SkillDetailsActivity.this, new ShareDialog.ShareParams(skill, skill.getQrcodeUrl(), skill.getUid(), skill.getNickname(), ""), 1);
            }
        }
        return super.onOptionsItemSelected(item);
    }


}

