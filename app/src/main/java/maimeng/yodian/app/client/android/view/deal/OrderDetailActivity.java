package maimeng.yodian.app.client.android.view.deal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.easemob.applib.controller.HXSDKHelper;
import com.umeng.analytics.MobclickAgent;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;
import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.SimpleTimeZone;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.chat.DemoHXSDKHelper;
import maimeng.yodian.app.client.android.chat.activity.ChatActivity;
import maimeng.yodian.app.client.android.chat.domain.RobotUser;
import maimeng.yodian.app.client.android.common.PullHeadView;
import maimeng.yodian.app.client.android.common.UEvent;
import maimeng.yodian.app.client.android.databinding.ActivityOrderDetailBinding;
import maimeng.yodian.app.client.android.model.Lottery;
import maimeng.yodian.app.client.android.model.OrderInfo;
import maimeng.yodian.app.client.android.model.chat.ChatUser;
import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.model.user.Buyer;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.OrderInfoResponse;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.service.OrderService;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.view.common.AbstractActivity;
import maimeng.yodian.app.client.android.view.common.WebViewActivity;
import maimeng.yodian.app.client.android.view.dialog.AlertDialog;
import maimeng.yodian.app.client.android.view.dialog.OrderCancellActivity;
import maimeng.yodian.app.client.android.view.dialog.ViewDialog;
import maimeng.yodian.app.client.android.view.skill.SkillDetailsActivity;


/**
 * Created by xujianhua on 10/10/15.
 */
public class OrderDetailActivity extends AbstractActivity implements PtrHandler, Callback<OrderInfoResponse> {

    private ActivityOrderDetailBinding mBinding;
    private OrderService mService;
    private boolean isSaled;
    private OrderInfo info;
    private static final int REQUEST_ORDER_BUY = 0x23;
    private static final int REQUEST_ORDER_CANCEL = 0x24;
    private Lottery mLottery;
    private long oid;
    private boolean isOperator = false;
    private boolean isCancel = false;
    private Timer timer;
    private String tipStr;

    @Override
    public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
        refreshInfo();
    }

    private void refreshInfo() {
        mService.info(oid, this);
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View view, View view1) {
        return PtrDefaultHandler.checkContentCanBePulledDown(ptrFrameLayout, view, view1);
    }

    public static void show(Context context, long oid) {
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra("oid", oid);
        context.startActivity(intent);
    }

    /****
     * 在订单列表购买完成后直接对应订单详情
     *
     * @param context
     * @param lottery
     */
    public static void show(Context context, Lottery lottery) {
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra("lottery", Parcels.wrap(lottery));
        context.startActivity(intent);
    }

    public static void show(Fragment context, OrderInfo orderInfo, int requestCode) {
        Intent intent = new Intent(context.getContext(), OrderDetailActivity.class);
        intent.putExtra("orderInfo", Parcels.wrap(orderInfo));
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void initToolBar(Toolbar toolbar) {
        super.initToolBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            mTitle.setTextColor(Color.WHITE);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_go_back);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.menu_more) {
            int status = info.getStatus();
            if (isSaled) {
                OrderCancellActivity.show(this, info.getOid(), false, REQUEST_ORDER_CANCEL);
            } else {
                boolean isCancel = (status == 0 || status == 2);
                MobclickAgent.onEvent(this, UEvent.ODER_DETAIL_CANCEL_CLICK);
                OrderCancellActivity.show(this, info.getOid(), isCancel, REQUEST_ORDER_CANCEL);
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_order_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private void refreshUI(final OrderInfo info) {
        isSaled = info.getSeller_id() == User.read(OrderDetailActivity.this).getUid();

        if (timer != null) {
            timer.cancel();
        }
        long diffTime = info.getDifftime();
        if (diffTime > 0) {
            timer = new Timer(diffTime * 1000, 1000);
            timer.start();
        }


        mBinding.setOrderInfo(info);
        String btnContactStr = null;
        if (isSaled) {
            btnContactStr = getString(R.string.button_contact_buyer);
            mBinding.orderBuyer.setText(Html.fromHtml(getResources().getString(R.string.order_buyer_name, info.getBuyer().getNickname())));
            mBinding.orderTotalFee.setText(Html.fromHtml(getResources().getString(R.string.order_saled_total_fee, info.getTotal_fee())));
        } else {
            btnContactStr = getString(R.string.button_contact_seller);
            mBinding.orderBuyer.setText(Html.fromHtml(getResources().getString(R.string.order_seller_name, info.getSkill().getNickname())));
            if (info.getBalance() > 0) {
                mBinding.orderTotalFee.setText(Html.fromHtml(getResources().getString(R.string.order_buyed_total_fee_for_balance, info.getTotal_fee(), info.getBalance() + "")));
            } else {
                mBinding.orderTotalFee.setText(Html.fromHtml(getResources().getString(R.string.order_buyed_total_fee, info.getTotal_fee())));
            }
        }

        mBinding.contactBuyer.setText(btnContactStr);
        final int status = info.getStatus();


        String operatorStr = null;
        switch (status) {
            case 0:
                //未支付
                tipStr = getResources().getString(R.string.tip_for_buyer_pay);
                if (!isSaled) {
                    operatorStr = getString(R.string.buyer_operator_pay);
                    mBinding.orderOperator.setTextColor(Color.WHITE);
                    mBinding.orderOperator.setBackground(getResources().getDrawable(R.drawable.btn_oval_blue));
                } else {
                    operatorStr = getString(R.string.seller_operator_wait_pay);
                    mBinding.orderOperator.setBackgroundColor(Color.TRANSPARENT);
                    mBinding.orderOperator.setTextColor(getResources().getColor(R.color.colorPrimaryDark3));
                }
                mBinding.orderPayTimeContent.setText(Html.fromHtml(getString(R.string.order_unpay_time)));
                mBinding.orderAcceptTimeContent.setText(Html.fromHtml(getString(R.string.order_unaccept_time)));
                mBinding.orderConfirmTimeContent.setText(Html.fromHtml(getString(R.string.order_unconfirm_time)));
                break;
            case 1:
                //预留
                break;
            case 2:
                //支付
                tipStr = getResources().getString(R.string.tip_for_seller_accept);
                if (!isSaled) {
                    operatorStr = getString(R.string.buyer_operator_wait_accept);
                    mBinding.orderOperator.setBackgroundColor(Color.TRANSPARENT);
                    mBinding.orderOperator.setTextColor(getResources().getColor(R.color.colorPrimaryDark3));
                } else {

                    operatorStr = getString(R.string.seller_operator_accept);
                    mBinding.orderOperator.setTextColor(Color.WHITE);
                    mBinding.orderOperator.setBackground(getResources().getDrawable(R.drawable.btn_oval_blue));
                }
                mBinding.orderStatusPay.setChecked(true);
                mBinding.orderPayTimeContent.setText(Html.fromHtml(getString(R.string.order_pay_time, formatDate(info.getPay_time()))));
                mBinding.orderAcceptTimeContent.setText(Html.fromHtml(getString(R.string.order_unaccept_time)));
                mBinding.orderConfirmTimeContent.setText(Html.fromHtml(getString(R.string.order_unconfirm_time)));
                break;
            case 3:
                //已经接单
                tipStr = getResources().getString(R.string.tip_for_seller_send);
                if (!isSaled) {
                    operatorStr = getString(R.string.buyer_operator_wait_send);
                    mBinding.orderOperator.setBackgroundColor(Color.TRANSPARENT);
                    mBinding.orderOperator.setTextColor(getResources().getColor(R.color.colorPrimaryDark3));
                } else {
                    operatorStr = getString(R.string.seller_operator_send);
                    mBinding.orderOperator.setTextColor(Color.WHITE);
                    mBinding.orderOperator.setBackground(getResources().getDrawable(R.drawable.btn_oval_blue));
                }
                mBinding.processPay.setChecked(true);
                mBinding.orderStatusPay.setChecked(true);
                mBinding.orderStatusAccept.setChecked(true);
                mBinding.orderPayTimeContent.setText(Html.fromHtml(getString(R.string.order_pay_time, formatDate(info.getPay_time()))));
                mBinding.orderAcceptTimeContent.setText(Html.fromHtml(getString(R.string.order_accept_time, formatDate(info.getAccept_time()))));
                mBinding.orderConfirmTimeContent.setText(Html.fromHtml(getString(R.string.order_unconfirm_time)));
                break;
            case 4:
                //已经发货
                tipStr = getResources().getString(R.string.tip_for_buyer_confirm);
                if (!isSaled) {
                    operatorStr = getString(R.string.buyer_operator_confirm);
                    mBinding.orderOperator.setTextColor(Color.WHITE);
                    mBinding.orderOperator.setBackground(getResources().getDrawable(R.drawable.btn_oval_blue));
                } else {

                    operatorStr = getString(R.string.order_status_send_goods);
                    mBinding.orderOperator.setBackgroundColor(Color.TRANSPARENT);
                    mBinding.orderOperator.setTextColor(getResources().getColor(R.color.colorPrimaryDark3));
                }
                mBinding.processPay.setChecked(true);
                mBinding.orderStatusPay.setChecked(true);
                mBinding.orderStatusAccept.setChecked(true);
                mBinding.orderStatusConfirm.setChecked(false);
                mBinding.orderPayTimeContent.setText(Html.fromHtml(getString(R.string.order_pay_time, formatDate(info.getPay_time()))));
                mBinding.orderAcceptTimeContent.setText(Html.fromHtml(getString(R.string.order_accept_time, formatDate(info.getAccept_time()))));
                mBinding.orderConfirmTimeContent.setText(Html.fromHtml(getString(R.string.order_unconfirm_time)));
                break;
            case 5:
                //交易完成
                operatorStr = getString(R.string.order_status_confirm_deal);
                mBinding.orderOperator.setBackgroundColor(Color.TRANSPARENT);
                mBinding.tip.setVisibility(View.GONE);
                mBinding.orderOperator.setTextColor(getResources().getColor(R.color.colorPrimaryDark3));
                mBinding.processPay.setChecked(true);
                mBinding.processAccept.setChecked(true);
                mBinding.orderStatusConfirm.setChecked(true);
                mBinding.orderStatusAccept.setChecked(true);
                mBinding.orderStatusPay.setChecked(true);
                mBinding.orderPayTimeContent.setText(Html.fromHtml(getString(R.string.order_pay_time, formatDate(info.getPay_time()))));
                mBinding.orderAcceptTimeContent.setText(Html.fromHtml(getString(R.string.order_accept_time, formatDate(info.getAccept_time()))));
                mBinding.orderConfirmTimeContent.setText(Html.fromHtml(getString(R.string.order_confirm_time, formatDate(info.getConfirm_time()))));
                break;
            case 6:
                //订单关闭

                if (!isSaled) {
                    operatorStr = getString(R.string.order_status_close);
                } else {
                    operatorStr = getString(R.string.order_status_buyer_close);
                }
                cancelOrder(info);
                break;
            case 7:
                //订单取消
                if (!isSaled) {
                    operatorStr = getString(R.string.order_status_cancle);
                } else {
                    operatorStr = getString(R.string.order_status_buyer_cancle);
                }
                cancelOrder(info);
                break;
            default:
                break;


        }
        if (status < 5) {
            refreshTip(info, tipStr);
        }
        mBinding.orderOperator.setText(operatorStr);
        mBinding.orderNumber.setText(Html.fromHtml(getResources().getString(R.string.order_number, info.getNumber())));
        mBinding.orderSkillPrice.setText(Html.fromHtml(getResources().getString(R.string.order_skill_price, info.getSkill().getPrice(), info.getSkill().getUnit())));
        mBinding.orderOperator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long oid = info.getOid();
                OrderOperatorCallBackProxy proxy = new OrderOperatorCallBackProxy();
                if (isSaled) {
                    MobclickAgent.onEvent(OrderDetailActivity.this, UEvent.ORDER_SALED_DETAIL_CLICK);
                    //出售订单
                    if (status == 2) {
                        //接单
                        mService.acceptOrder(oid, proxy);
                        isOperator = true;
                    } else if (status == 3) {
                        //发货
                        mService.sendGoods(oid, proxy);
                        isOperator = true;
                    }
                } else {
                    //购买订单
                    MobclickAgent.onEvent(OrderDetailActivity.this, UEvent.ORDER_BUYED_DETAILED_CLICK);
                    if (status == 0) {
                        //支付
                        isOperator = true;
                        PayWrapperActivity.show(OrderDetailActivity.this, info, REQUEST_ORDER_BUY);
                    } else if (status == 4) {
                        //确认收货
                        isOperator = true;
                        confirmOrder(oid);

                    }
                }

                if (isOperator) {
                    setResult(RESULT_OK, getIntent().putExtra("isOperator", isOperator));
                }

            }

        });

        mBinding.contactBuyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, RobotUser> robotMap = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getRobotList();
                if (isSaled) {
                    Buyer buyer = info.getBuyer();
                    if (!robotMap.containsKey(buyer.getHxname())) {
                        RobotUser robot = new RobotUser();
                        robot.setId(buyer.getId());
                        robot.setUsername(buyer.getHxname());
                        robot.setNick(buyer.getNickname());
                        robot.setAvatar(buyer.getAvatar());
                        robot.setWechat(buyer.getWeichat());
                        robot.setMobile(buyer.getMobile());
                        robot.setQq(buyer.getQq());

                        maimeng.yodian.app.client.android.chat.domain.User user = new maimeng.yodian.app.client.android.chat.domain.User();
                        user.setId(buyer.getId());
                        user.setUsername(buyer.getHxname());
                        user.setNick(buyer.getNickname());
                        user.setAvatar(buyer.getAvatar());
                        user.setWechat(buyer.getWeichat());
                        user.setMobile(buyer.getMobile());
                        user.setQq(buyer.getQq());


                        // 存入内存
                        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveOrUpdate(robot);
                        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveOrUpdate(user);
                    }
                    ChatUser chatUser = new ChatUser(buyer.getHxname(), buyer.getId(), buyer.getNickname());
                    chatUser.setMobile(buyer.getMobile());
                    chatUser.setQq(buyer.getQq());
                    chatUser.setWechat(buyer.getWeichat());
                    MobclickAgent.onEvent(OrderDetailActivity.this, UEvent.CONTACT_TA);
                    ChatActivity.show(OrderDetailActivity.this, info.getSkill(), chatUser);
                } else {
                    //联系买家或者卖家
                    Skill skill = info.getSkill();
                    String chatLoginName = skill.getChatLoginName();
                    if (!robotMap.containsKey(chatLoginName)) {
                        RobotUser robot = new RobotUser();
                        robot.setId(skill.getUid());
                        robot.setUsername(chatLoginName);
                        robot.setNick(skill.getNickname());
                        robot.setAvatar(skill.getAvatar());
                        robot.setMobile(skill.getContact());
                        robot.setQq(skill.getQq());
                        robot.setWechat(skill.getWeichat());

                        maimeng.yodian.app.client.android.chat.domain.User user = new maimeng.yodian.app.client.android.chat.domain.User();
                        user.setId(skill.getUid());
                        user.setUsername(chatLoginName);
                        user.setNick(skill.getNickname());
                        user.setAvatar(skill.getAvatar());
                        user.setMobile(skill.getContact());
                        user.setQq(skill.getQq());
                        user.setWechat(skill.getWeichat());

                        // 存入内存
                        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveOrUpdate(robot);
                        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveOrUpdate(user);
                    }
                    ChatUser chatUser = new ChatUser(chatLoginName, skill.getUid(), skill.getNickname());
                    chatUser.setMobile(skill.getContact());
                    chatUser.setQq(skill.getQq());
                    chatUser.setWechat(skill.getWeichat());
                    MobclickAgent.onEvent(OrderDetailActivity.this, UEvent.CONTACT_TA);
                    ChatActivity.show(OrderDetailActivity.this, info.getSkill(), chatUser);
                }
            }
        });
    }

    private void cancelOrder(OrderInfo info) {
        mBinding.tip.setVisibility(View.GONE);
        Spanned payStr = null;
        Spanned acceptStr = null;
        Spanned confirmStr = null;
        if (info.getPay_time() > 0) {
            payStr = Html.fromHtml(getString(R.string.order_pay_time_on_cancel, formatDate(info.getPay_time())));
        } else {
            payStr = Html.fromHtml(getString(R.string.order_unpay_time));
        }
        if (info.getAccept_time() > 0) {
            acceptStr = Html.fromHtml(getString(R.string.order_accept_time_on_cancel, formatDate(info.getAccept_time())));
        } else {
            acceptStr = Html.fromHtml(getString(R.string.order_unaccept_time));
        }
        if (info.getConfirm_time() > 0) {
            confirmStr = Html.fromHtml(getString(R.string.order_confirm_time_on_cancel, formatDate(info.getConfirm_time())));
        } else {
            confirmStr = Html.fromHtml(getString(R.string.order_unconfirm_time));
        }
        mBinding.orderOperator.setBackgroundColor(Color.TRANSPARENT);
        mBinding.orderOperator.setTextColor(getResources().getColor(R.color.colorPrimaryDark3));
        mBinding.orderStatusPay.setChecked(false);
        mBinding.orderStatusAccept.setChecked(false);
        mBinding.orderStatusConfirm.setChecked(false);
        mBinding.processPay.setChecked(false);
        mBinding.processAccept.setChecked(false);
        mBinding.orderPayTimeContent.setText(payStr);
        mBinding.orderAcceptTimeContent.setText(acceptStr);
        mBinding.orderConfirmTimeContent.setText(confirmStr);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = bindView(R.layout.activity_order_detail);
        mService = Network.getService(OrderService.class);
        info = get("orderInfo");
        oid = getIntent().getLongExtra("oid", 0);
        mLottery = get("lottery");
        //购买完成后跳到订单详情
        if (mLottery != null) {
            oid = mLottery.getOid();
            if (mLottery.getIsLottery() == 1) {
                LotteryActivity.show(this, mLottery);
            }
        }

        //从技能详情或订单列表跳到订单详情
        if (info != null) {
            oid = info.getOid();
        }
        mBinding.refreshLayout.setPtrHandler(this);
        StoreHouseHeader header = PullHeadView.create(this).setTextColor(0x0);
        mBinding.refreshLayout.addPtrUIHandler(header);
        mBinding.refreshLayout.setHeaderView(header);

        mBinding.skillDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailActivity.this, SkillDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("skill", Parcels.wrap(info.getSkill()));
                startActivity(intent);
            }
        });

        refreshInfo();

    }

    @Override
    public void start() {

    }

    @Override
    public void success(OrderInfoResponse res, Response response) {
        if (res.isSuccess()) {
            this.info = res.getData();
            refreshUI(res.getData());

            if (isCancel) {
                isCancel = false;
                //系统自动处理
                systemDealWithOrder();

            }
        } else {
            res.showMessage(this);
        }
    }

    /***
     * 系统订单自动处理
     */
    private void systemDealWithOrder() {
        if (!isSaled) {
            if (info.getStatus() < 4) {
                mService.cancleOrder(info.getOid(), new Callback<ToastResponse>() {
                    @Override
                    public void start() {

                    }

                    @Override
                    public void success(ToastResponse toastResponse, Response response) {
                        if (toastResponse.isSuccess()) {
                            isOperator = true;
                            refreshInfo();
                        }
                        toastResponse.showMessage(OrderDetailActivity.this);

                    }

                    @Override
                    public void failure(HNetError hNetError) {
                        ErrorUtils.checkError(OrderDetailActivity.this, hNetError);
                    }

                    @Override
                    public void end() {

                    }
                });
            }
            if (info.getStatus() == 4 && !isSaled) {
                confirmOrder(info.getOid());

            }
        }
    }

    @Override
    public void failure(HNetError hNetError) {

    }

    @Override
    public void end() {
        mBinding.refreshLayout.refreshComplete();
    }

    /***
     *
     */
    public class OrderOperatorCallBackProxy implements Callback<ToastResponse> {
        @Override
        public void start() {

        }

        @Override
        public void success(ToastResponse toastResponse, Response response) {
            if (toastResponse.getCode() == 20000) {
                refreshInfo();
            }
            toastResponse.showMessage(OrderDetailActivity.this);
        }

        @Override
        public void failure(HNetError hNetError) {
            ErrorUtils.checkError(OrderDetailActivity.this, hNetError);
        }

        @Override
        public void end() {

        }
    }

    /***
     * @param time
     * @return
     */
    private String formatDate(long time) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        return format.format(new Date(time * 1000));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (data != null) {
                if (requestCode == REQUEST_ORDER_BUY) {
                    Lottery lottery = Parcels.unwrap(data.getParcelableExtra("lottery"));
                    if (lottery.getIsLottery() == 1) {
                        LotteryActivity.show(this, lottery);
                    }
                } else if (requestCode == REQUEST_ORDER_CANCEL) {
                    setResult(RESULT_OK, data);
                }
            }

        }
    }


    public class Timer extends CountDownTimer {
        private long millisUtilFinished;

        public Timer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);

        }

        @Override
        public void onTick(long millisUntilFinished) {
            this.millisUtilFinished = millisUntilFinished;
            refreshTime(millisUntilFinished, tipStr);
        }

        @Override
        public void onFinish() {
            refreshInfo();
            isCancel = true;

        }
    }

    /***
     * @param info
     * @param tipStr
     */
    public void refreshTip(OrderInfo info, String tipStr) {
        long diffTime = info.getDifftime();

        long time = diffTime * 1000;
        if (info.getStatus() < 5) {
            refreshTime(time, tipStr);
        }


    }

    /***
     * time 单位为毫秒
     *
     * @param time
     * @param tipStr
     */
    public void refreshTime(long time, String tipStr) {
        String diffStr = null;
        if (time / (24 * 60 * 60 * 1000) > 0) {
            diffStr = time / (24 * 60 * 60 * 1000) + "天";
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH小时mm分ss秒", Locale.getDefault());
            dateFormat.setTimeZone(new SimpleTimeZone(0, "UTC"));
            diffStr = dateFormat.format(new Date(time));
        }
        if (tipStr != null) {
            mBinding.tip.setText(String.format(tipStr, diffStr));
        }


    }

    /***
     * 收货
     *
     * @param oid
     */
    private void confirmOrder(long oid) {
        mService.confirmOrder(oid, new OrderOperatorCallBackProxy() {
            @Override
            public void success(ToastResponse toastResponse, Response response) {
                super.success(toastResponse, response);
                toastResponse.showMessage(OrderDetailActivity.this);
                if (toastResponse.isSuccess()) {
                    mBinding.refreshLayout.autoRefresh();
                    mBinding.tip.setVisibility(View.GONE);
                }
            }
        });
    }
}
