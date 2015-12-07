package maimeng.yodian.app.client.android.view.deal;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;
import org.henjue.library.share.Type;
import org.henjue.library.share.manager.AuthFactory;
import org.henjue.library.share.manager.WechatAuthManager;
import org.parceler.Parcels;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.common.UEvent;
import maimeng.yodian.app.client.android.model.Lottery;
import maimeng.yodian.app.client.android.model.OrderInfo;
import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.LotteryResponse;
import maimeng.yodian.app.client.android.network.response.RemainderPayParamsResponse;
import maimeng.yodian.app.client.android.network.response.RemainderResponse;
import maimeng.yodian.app.client.android.network.response.WXPayParamResponse;
import maimeng.yodian.app.client.android.network.response.ZhiFuBaoPayParamsResponse;
import maimeng.yodian.app.client.android.network.service.BuyService;
import maimeng.yodian.app.client.android.network.service.MoneyService;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.view.common.AbstractActivity;
import maimeng.yodian.app.client.android.view.deal.pay.IPay;
import maimeng.yodian.app.client.android.view.deal.pay.IPayFactory;
import maimeng.yodian.app.client.android.view.deal.pay.IPayStatus;
import maimeng.yodian.app.client.android.view.dialog.AlertDialog;
import maimeng.yodian.app.client.android.view.dialog.RemainderCustomDialog;
import maimeng.yodian.app.client.android.view.dialog.ViewDialog;
import maimeng.yodian.app.client.android.view.dialog.WaitDialog;

/**
 * Created by xujianhua on 10/12/15.
 */
public class PayListActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mTitle;
    private Skill mSkill;
    private BuyService mService;
    private OrderInfo mOrderInfo;
    private boolean isOrderPay;
    private WaitDialog mDialog;

    private final static int PAY_TYPE_ZHIFUBAO = 1;
    private final static int PAY_TYPE_REMAINDER = 3;
    private final static int PAY_TYPE_WECHAT = 2;
    private TextView mBtnMoney;
    private float price;
    private float money;
    private Lottery lottery;

    /***
     * 订单支付
     *
     * @param context
     */
    public static void show(Activity context, OrderInfo orderInfo, int requestCode) {
        Intent intent = new Intent(context, PayListActivity.class);
        intent.putExtra("orderInfo", Parcels.wrap(orderInfo));
        context.startActivityForResult(intent, requestCode, new Bundle());
    }

    /***
     * 技能购买
     *
     * @param context
     * @param orderInfo
     * @param requestCode
     */
    public static void show(Activity context, Skill orderInfo, int requestCode) {
        Intent intent = new Intent(context, PayListActivity.class);
        intent.putExtra("skill", Parcels.wrap(orderInfo));
        context.startActivityForResult(intent, requestCode, new Bundle());
    }

    private boolean canUseMoney = false;//是否使用余额

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setGravity(Gravity.BOTTOM);
        setContentView(R.layout.activity_pay_list);

        mTitle = (TextView) findViewById(R.id.pay_title);
        View remainder=findViewById(R.id.pay_remainer);
        findViewById(R.id.pay_wechat).setOnClickListener(this);
        findViewById(R.id.pay_zhifubao).setOnClickListener(this);
        remainder.setOnClickListener(this);

        Intent intent = getIntent();
        mBtnMoney = ((TextView) findViewById(R.id.btn_money));
        if (intent.hasExtra("orderInfo")) {
            mOrderInfo = get("orderInfo");
            price = mOrderInfo.getTotal_fee()- mOrderInfo.getBalance();
            isOrderPay = true;
            remainder.setVisibility(View.GONE);
        } else {
            mSkill = get("skill");
            price = mSkill.getPrice();
            remainder.setVisibility(View.VISIBLE);
        }
        String priceStr=getResources().getString(R.string.pay_list_title, price);
        if(mOrderInfo!=null&&mOrderInfo.getBalance()>0){
            priceStr=getResources().getString(R.string.pay_list_title_using_balance, price);
        }
        mTitle.setText(Html.fromHtml(priceStr));

//        HNet net = new HNet.Builder()
//                .setEndpoint(BuildConfig.API_HOST)
//                .setIntercept(new RequestIntercept(this))
//                .build();
        mBtnMoney.setText(getString(R.string.pay_remainer, 0f));
        Network.getService(MoneyService.class).remanider(new Callback<RemainderResponse>() {
            @Override
            public void start() {
            }

            @Override
            public void success(RemainderResponse res, Response response) {
                money = res.getData().getMoney();
                mBtnMoney.setText(getString(R.string.pay_remainer, money));
                if (money < price&&money>0) {
                    canUseMoney = true;
                    mBtnMoney.setEnabled(false);
                    mBtnMoney.setTextColor(Color.parseColor("#cccccc"));
                    findViewById(R.id.pay_remainer).setEnabled(false);
                }

            }

            @Override
            public void failure(HNetError hNetError) {

            }

            @Override
            public void end() {

            }
        });
        mService = Network.getService(BuyService.class);


    }

    /***
     * @param v
     */
    @Override
    public void onClick(View v) {
        //生成订单信息
        if (isOrderPay) {
            //订单列表购买
            if (v.getId() == R.id.pay_remainer) {
                MobclickAgent.onEvent(this, UEvent.PAY_REMAINDER);
                mService.buyOrder(mOrderInfo.getOid(), PAY_TYPE_REMAINDER, 1, new CallBackProxy(PAY_TYPE_REMAINDER));
            } else if (v.getId() == R.id.pay_wechat) {
                payByWechatOrder(mOrderInfo.getBalance() == 0);
            } else if (v.getId() == R.id.pay_zhifubao) {
                payByAliPayOrder(mOrderInfo.getBalance() == 0);
            }
        } else {
            MobclickAgent.onEvent(this, UEvent.PAY_CLICK);
            //技能列表购买
            if (v.getId() == R.id.pay_remainer) {
                MobclickAgent.onEvent(this, UEvent.PAY_REMAINDER);
                mService.buySkill(mSkill.getId(), PAY_TYPE_REMAINDER, 1, new CallBackProxy(PAY_TYPE_REMAINDER));
            } else if (v.getId() == R.id.pay_wechat) {
                if (canUseMoney) {
                    RemainderCustomDialog.Builder builder = new RemainderCustomDialog.Builder(PayListActivity.this);
                    builder.setMesage(Html.fromHtml(getResources().getString(R.string.pay_remainder_enable, String.format("%.2f",money), String.format("%.2f",price - money ))));
                    builder.setPositiveListener(new RemainderCustomDialog.IPositiveListener() {
                        @Override
                        public void positiveClick() {
                            MobclickAgent.onEvent(PayListActivity.this, UEvent.PAY_MUTIFY_REMAINDER_YES);
                            payByWechatSkill(true);
                        }
                    }, "使用");
                    builder.setNegtiveListener(new RemainderCustomDialog.INegativeListener() {
                        @Override
                        public void negtiveClick() {
                            MobclickAgent.onEvent(PayListActivity.this, UEvent.PAY_MUTIFY_REMAINDER_NO);
                            payByWechatSkill(false);
                        }
                    }, "全额付款");
                    builder.setCloseListener(new RemainderCustomDialog.ICloseListener() {
                        @Override
                        public void closeClick() {
                            finish();
                        }
                    });

                    builder.create().show();
                } else {
                    payByWechatSkill(false);
                }


            } else if (v.getId() == R.id.pay_zhifubao) {
                if (canUseMoney) {

                    RemainderCustomDialog.Builder builder = new RemainderCustomDialog.Builder(PayListActivity.this);
                    builder.setMesage(Html.fromHtml(getResources().getString(R.string.pay_remainder_enable, String.format("%.2f",money), String.format("%.2f",price - money))));
                    builder.setPositiveListener(new RemainderCustomDialog.IPositiveListener() {
                        @Override
                        public void positiveClick() {
                            MobclickAgent.onEvent(PayListActivity.this, UEvent.PAY_MUTIFY_REMAINDER_YES);
                            payByAliPaySkill(true);
                        }
                    }, "使用");
                    builder.setNegtiveListener(new RemainderCustomDialog.INegativeListener() {
                        @Override
                        public void negtiveClick() {
                            MobclickAgent.onEvent(PayListActivity.this, UEvent.PAY_MUTIFY_REMAINDER_NO);
                            payByAliPaySkill(false);
                        }
                    }, "全额付款");
                    builder.setCloseListener(new RemainderCustomDialog.ICloseListener() {
                        @Override
                        public void closeClick() {
                            finish();
                        }
                    });

                    builder.create().show();
                } else {
                    payByAliPaySkill(false);
                }
            }
        }

    }

    private void payByAliPayOrder(boolean useMoney) {
        MobclickAgent.onEvent(this, UEvent.PAY_ZHIFUBAO);
        mService.buyOrder(mOrderInfo.getOid(), PAY_TYPE_ZHIFUBAO, useMoney ? 1 : 0, new CallBackProxy(PAY_TYPE_ZHIFUBAO));
    }

    private void payByWechatOrder(boolean useMoney) {
        MobclickAgent.onEvent(this, UEvent.PAY_WECHAT);
        WechatAuthManager manager = (WechatAuthManager) AuthFactory.create(this, Type.Platform.WEIXIN);//代码一定不能删除，保证IWXAPI已经初始化
        if (manager.getIWXAPI().isWXAppInstalled()) {
            mService.buyOrder(mOrderInfo.getOid(), PAY_TYPE_WECHAT, useMoney ? 1 : 0, new CallBackProxy(PAY_TYPE_WECHAT));
        } else {
            Toast.makeText(this, "没有安装微信", Toast.LENGTH_SHORT).show();
        }

    }

    private void payByAliPaySkill(boolean useMoney) {
        MobclickAgent.onEvent(this, UEvent.PAY_ZHIFUBAO);
        mService.buySkill(mSkill.getId(), PAY_TYPE_ZHIFUBAO, useMoney ? 1 : 0, new CallBackProxy(PAY_TYPE_ZHIFUBAO));
    }

    private void payByWechatSkill(boolean useMoney) {
        MobclickAgent.onEvent(this, UEvent.PAY_WECHAT);
        WechatAuthManager manager = (WechatAuthManager) AuthFactory.create(this, Type.Platform.WEIXIN);//代码一定不能删除，保证IWXAPI已经初始化
        if (manager.getIWXAPI().isWXAppInstalled()) {
            mService.buySkill(mSkill.getId(), PAY_TYPE_WECHAT, useMoney ? 1 : 0, new CallBackProxy(PAY_TYPE_WECHAT));
        } else {
            Toast.makeText(this, "没有安装微信", Toast.LENGTH_SHORT).show();
        }
    }

    /***
     *
     */
    public final class CallBackProxy implements Callback<String> {
        private final int payType;

        public CallBackProxy(int payType) {
            this.payType = payType;
        }

        @Override
        public void start() {
            mDialog = WaitDialog.show(PayListActivity.this);
        }

        @Override
        public void success(final String s, Response response) {
            final Gson gson = Network.getOne().getGson();
            final IPay pay;
            lottery = gson.fromJson(s, LotteryResponse.class).getData();
            String lotUrl = lottery.getLotUrl();
            lottery.setLotUrl(String.format(lotUrl + "?uid=%1$d&oid=%2$d", User.read(PayListActivity.this).getUid(), lottery.getOid()));
            if (payType == PAY_TYPE_ZHIFUBAO) {
                ZhiFuBaoPayParamsResponse zhiFuBaoPayParamsResponse = gson.fromJson(s, ZhiFuBaoPayParamsResponse.class);
                IPayStatus status = new PayStatus(zhiFuBaoPayParamsResponse.getData().getOid());
                pay = IPayFactory.createPay(PayListActivity.this, IPayFactory.Type.Alipay, zhiFuBaoPayParamsResponse.getData(), status);
                if (pay != null) {
                    pay.sendReq();
                }
            } else if (payType == PAY_TYPE_WECHAT) {
                WXPayParamResponse paramResponse = gson.fromJson(s, WXPayParamResponse.class);
                IPayStatus status = new PayStatus(paramResponse.getData().getOid());
                pay = IPayFactory.createPay(PayListActivity.this, IPayFactory.Type.Wechat, paramResponse.getData().getParams(), status);
                if (pay != null) {
                    pay.sendReq();
                }
            } else if (payType == PAY_TYPE_REMAINDER) {
                RemainderPayParamsResponse remainderPayParamsResponse = gson.fromJson(s, RemainderPayParamsResponse.class);
                IPayStatus status = new PayStatus(remainderPayParamsResponse.getData().getOid());
                pay = IPayFactory.createPay(PayListActivity.this, IPayFactory.Type.Remainder, remainderPayParamsResponse.getData(), status);
                final android.support.v7.app.AlertDialog alertDialog = new ViewDialog.Builder(PayListActivity.this).setMesage(getResources().getString(R.string.pay_deal_tip))
                        .setPositiveListener(new ViewDialog.IPositiveListener() {
                            @Override
                            public void positiveClick() {
                                if (pay != null) {
                                    pay.sendReq();
                                }
                            }
                        }, "").setNegtiveListener(new ViewDialog.INegativeListener() {
                            @Override
                            public void negtiveClick() {
                                finish();
                            }
                        }, "").create();
                alertDialog.show();

            }


        }

        @Override
        public void failure(HNetError hNetError) {
            ErrorUtils.checkError(PayListActivity.this, hNetError);
        }

        @Override
        public void end() {
            mDialog.dismiss();
        }
    }

    /***
     * 支付结果
     */
    public class PayStatus implements IPayStatus {
        private final long oid;

        public PayStatus(long oid) {
            this.oid = oid;
        }

        @Override
        public void failurepay(int errCode) {
            Spanned fail = Html.fromHtml(getResources().getString(R.string.pay_result_fail));
            String failStr = fail.toString();
            if (errCode == IPayStatus.PAY_ERROR_REMAINDER_SHORT) {
                failStr = getResources().getString(R.string.pay_deal_remainder_shortage);
            }
            String btnTip = getResources().getString(R.string.btn_name);
            String title = getResources().getString(R.string.pay_deal_title);

            new ViewDialog.Builder(PayListActivity.this).setMesage(failStr)
                    .setTitle(title).setPositiveListener(new ViewDialog.IPositiveListener() {
                @Override
                public void positiveClick() {
                    finish();
                }
            }, btnTip).create().show();


        }

        @Override
        public void sucessPay(int errCode) {
            Spanned sucess = Html.fromHtml(getResources().getString(R.string.pay_result_sucess));
            String btnTip = getResources().getString(R.string.btn_name);
            String title = getResources().getString(R.string.pay_deal_title);
            new ViewDialog.Builder(PayListActivity.this).setMesage(sucess.toString())
                    .setTitle(title).setPositiveListener(new ViewDialog.IPositiveListener() {
                @Override
                public void positiveClick() {
                    Intent intent = new Intent();
                    intent.putExtra("lottery", Parcels.wrap(lottery));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }, btnTip).create().show();
        }
    }

    protected <T> T get(String key) {
        return Parcels.unwrap(getIntent().getParcelableExtra(key));
    }
}
