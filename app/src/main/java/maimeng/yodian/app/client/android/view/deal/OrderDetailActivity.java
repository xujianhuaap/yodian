package maimeng.yodian.app.client.android.view.deal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.easemob.applib.controller.HXSDKHelper;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.chat.DemoHXSDKHelper;
import maimeng.yodian.app.client.android.chat.activity.ChatActivity;
import maimeng.yodian.app.client.android.chat.db.UserDao;
import maimeng.yodian.app.client.android.chat.domain.RobotUser;
import maimeng.yodian.app.client.android.databinding.ActivityOrderDetailBinding;
import maimeng.yodian.app.client.android.model.OrderInfo;
import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.service.OrderService;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.view.AbstractActivity;


/**
 * Created by xujianhua on 10/10/15.
 */
public class OrderDetailActivity extends AbstractActivity {

    private ActivityOrderDetailBinding mBinding;
    private OrderService mService;


    public static void show(Context context, OrderInfo orderInfo, boolean isSaled) {
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra("orderInfo", orderInfo);
        intent.putExtra("isSaled", isSaled);
        context.startActivity(intent);
    }

    @Override
    protected void initToolBar(Toolbar toolbar) {
        super.initToolBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_go_back_black);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = bindView(R.layout.activity_order_detail);
        final OrderInfo info = getIntent().getParcelableExtra("orderInfo");
        final boolean isSaled = getIntent().getBooleanExtra("isSaled", false);
        mBinding.setOrderInfo(info);

        if (isSaled) {
            mBinding.orderBuyer.setText(Html.fromHtml(getResources().getString(R.string.order_buyer_name, info.getBuyer().getNickname())));
            mBinding.orderTotalFee.setText(Html.fromHtml(getResources().getString(R.string.order_saled_total_fee, info.getTotal_fee())));
        } else {
            mBinding.orderBuyer.setText(Html.fromHtml(getResources().getString(R.string.order_seller_name, info.getSkill().getNickname())));
            mBinding.orderTotalFee.setText(Html.fromHtml(getResources().getString(R.string.order_buyed_total_fee, info.getTotal_fee())));
        }

        final int status = Integer.parseInt(info.getStatus());
        LogUtil.d("ceshi", "status" + status);
        String operatorStr = null;
        switch (status) {

            case 0:
                //未支付
                if (!isSaled) {
                    operatorStr = getString(R.string.buyer_operator_pay);
                    mBinding.orderOperator.setBackground(getResources().getDrawable(R.mipmap.ic_oval_orange));
                } else {
                    mBinding.orderOperator.setVisibility(View.INVISIBLE);
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
                if (!isSaled) {
                    operatorStr = getString(R.string.buyer_operator_wait_accept);
                    mBinding.orderOperator.setBackground(getResources().getDrawable(R.mipmap.ic_oval_gray));
                } else {
                    operatorStr = getString(R.string.seller_operator_accept);
                    mBinding.orderOperator.setBackground(getResources().getDrawable(R.mipmap.ic_oval_orange));
                }
                mBinding.orderStatusPay.setChecked(true);
                mBinding.orderPayTimeContent.setText(Html.fromHtml(getString(R.string.order_pay_time, formatDate(info.getPay_time()))));
                mBinding.orderAcceptTimeContent.setText(Html.fromHtml(getString(R.string.order_unaccept_time)));
                mBinding.orderConfirmTimeContent.setText(Html.fromHtml(getString(R.string.order_unconfirm_time)));
                break;
            case 3:
                //已经接单
                if (!isSaled) {
                    operatorStr = getString(R.string.buyer_operator_wait_send);
                    mBinding.orderOperator.setBackground(getResources().getDrawable(R.mipmap.ic_oval_gray));
                } else {
                    operatorStr = getString(R.string.seller_operator_send);
                    mBinding.orderOperator.setBackground(getResources().getDrawable(R.mipmap.ic_oval_orange));
                }
                mBinding.processPay.setChecked(true);
                mBinding.orderStatusPay.setChecked(true);
                mBinding.orderStatusAccept.setChecked(true);
                mBinding.orderPayTimeContent.setText(Html.fromHtml(getString(R.string.order_pay_time, formatDate(info.getPay_time()))));
                mBinding.orderAcceptTimeContent.setText(Html.fromHtml(getString(R.string.order_accept_time, formatDate(info.getAccept_time()))));
                mBinding.orderConfirmTimeContent.setText(Html.fromHtml(getString(R.string.order_unconfirm_time)));
                break;
            case 4:
                //已经o发货
                if (!isSaled) {
                    operatorStr = getString(R.string.buyer_operator_confirm);
                    mBinding.orderOperator.setBackground(getResources().getDrawable(R.mipmap.ic_oval_orange));
                } else {

                    mBinding.orderOperator.setVisibility(View.INVISIBLE);
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
                mBinding.orderOperator.setBackground(getResources().getDrawable(R.mipmap.ic_oval_gray));
                mBinding.processPay.setChecked(true);
                mBinding.processAccept.setChecked(true);
                mBinding.orderStatusConfirm.setChecked(true);
                mBinding.orderStatusAccept.setChecked(true);
                mBinding.orderStatusPay.setChecked(true);
                mBinding.orderPayTimeContent.setText(Html.fromHtml(getString(R.string.order_pay_time, formatDate(info.getPay_time()))));
                mBinding.orderAcceptTimeContent.setText(Html.fromHtml(getString(R.string.order_accept_time, formatDate(info.getAccept_time()))));
                mBinding.orderConfirmTimeContent.setText(Html.fromHtml(getString(R.string.order_confirm_time, formatDate(info.getConfirm_time()))));
                break;
            default:
                break;


        }
        mBinding.orderOperator.setText(operatorStr);
        mBinding.orderNumber.setText(Html.fromHtml(getResources().getString(R.string.order_number, info.getNumber())));
        mBinding.orderSkillPrice.setText(Html.fromHtml(getResources().getString(R.string.order_skill_price, info.getSkill().getPrice(), info.getSkill().getUnit())));
        mBinding.orderOperator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oid = info.getOid();
                OrderOperatorCallBackProxy proxy = new OrderOperatorCallBackProxy();
                if (isSaled) {
                    //出售订单
                    if (status == 2) {
                        //接单
                        mService.acceptOrder(oid, proxy);
                    } else if (status == 3) {
                        //发货
                        mService.sendGoods(oid, proxy);
                    }
                } else {
                    //购买订单
                    if (status == 0) {
                        //支付
                        PayWrapperActivity.show(OrderDetailActivity.this, info);
                    } else if (status == 4) {
                        //确认发货
                        mService.confirmOrder(oid, proxy);

                    }
                }

            }
        });

        mBinding.contactBuyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //联系买家或者卖家
                Skill skill = info.getSkill();
                Intent intent = new Intent(OrderDetailActivity.this, ChatActivity.class);
                intent.putExtra("skill", skill);
                intent.putExtra("uid", skill.getUid());
                Map<String, RobotUser> robotMap = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getRobotList();
                String chatLoginName = skill.getChatLoginName();
                if (robotMap.containsKey(chatLoginName)) {
                    intent.putExtra("userId", chatLoginName);
                    OrderDetailActivity.this.startActivity(intent);
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
                    UserDao dao = new UserDao(OrderDetailActivity.this);
                    dao.saveOrUpdate(user);
                    dao.saveOrUpdate(robot);
                    intent.putExtra("userId", chatLoginName);
                    intent.putExtra("userNickname", skill.getNickname());
                    OrderDetailActivity.this.startActivity(intent);
                }
            }
        });

        mService = Network.getService(OrderService.class);
    }

    /***
     *
     */
    public final class OrderOperatorCallBackProxy implements Callback<ToastResponse> {
        @Override
        public void start() {

        }

        @Override
        public void success(ToastResponse toastResponse, Response response) {
            if (toastResponse.getCode() == 20000) {
                Toast.makeText(OrderDetailActivity.this, toastResponse.getMsg(), Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void failure(HNetError hNetError) {

        }

        @Override
        public void end() {

        }
    }

    /***
     * @param time
     * @return
     */
    private String formatDate(String time) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        return format.format(new Date(Long.parseLong(time) * 1000));
    }
}
