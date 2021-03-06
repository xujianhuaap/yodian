package maimeng.yodian.app.client.android.view.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.easemob.applib.controller.HXSDKHelper;
import com.umeng.analytics.MobclickAgent;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;
import org.parceler.Parcels;

import java.util.Map;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.chat.DemoHXSDKHelper;
import maimeng.yodian.app.client.android.chat.activity.ChatActivity;
import maimeng.yodian.app.client.android.common.UEvent;
import maimeng.yodian.app.client.android.model.OrderInfo;
import maimeng.yodian.app.client.android.model.chat.ChatUser;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.service.OrderService;
import maimeng.yodian.app.client.android.view.common.AbstractActivity;
import maimeng.yodian.app.client.android.view.deal.BindStatus;

/**
 * Created by xujianhua on 10/16/15.
 */
public class OrderCancellActivity extends AbstractActivity {
    private static final int REQUEST_CERTIFY = 0x234;
    private BindStatus status;

    /***
     * @param context
     */

    public static void show(Activity context, OrderInfo info, boolean isCancel, int requestCode) {
        Intent intent = new Intent(context, OrderCancellActivity.class);
        intent.putExtra("info", Parcels.wrap(info));
        intent.putExtra("isCancle", isCancel);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        status = User.read(this).getInfo().getVouch_status();
        getWindow().setGravity(Gravity.BOTTOM);
        setContentView(R.layout.activity_order_cancel,false);
        findViewById(R.id.contact_us).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, maimeng.yodian.app.client.android.chat.domain.User> users = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList();
                maimeng.yodian.app.client.android.chat.domain.User user = users.get("hx_admin");
//                Skill skill = Skill.parse(msg);
                ChatUser chatUser = new ChatUser(user.getUsername(), user.getId(), user.getNick());
                chatUser.setMobile(user.getMobile());
                chatUser.setQq(user.getQq());
                chatUser.setWechat(user.getWechat());
                ChatActivity.show(v.getContext(),(OrderInfo) get("info"),chatUser);
                finish();
            }
        });


        View view = findViewById(R.id.order_cancle);
        if (getIntent().getBooleanExtra("isCancle", false)) {
            view.setVisibility(View.VISIBLE);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消订单
                ViewDialog.Builder builder = new ViewDialog.Builder(OrderCancellActivity.this);
                builder.setTitle("").setMesage("你确定要取消订单吗？")
                        .setPositiveListener(new ViewDialog.IPositiveListener() {
                            @Override
                            public void positiveClick() {
                                MobclickAgent.onEvent(OrderCancellActivity.this, UEvent.ORDER_CANCEL_YES);
                                long oid = ((OrderInfo)get("info")).getOid();
                                Network.getService(OrderService.class).cancleOrder(oid, new Callback<ToastResponse>() {
                                    @Override
                                    public void start() {

                                    }

                                    @Override
                                    public void success(ToastResponse toastResponse, Response response) {
                                        Toast.makeText(OrderCancellActivity.this, toastResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                        setResult(RESULT_OK, getIntent().putExtra("isOperator", true));
                                        finish();
                                    }

                                    @Override
                                    public void failure(HNetError hNetError) {

                                    }

                                    @Override
                                    public void end() {

                                    }
                                });
                            }
                        }, "取消订单").setNegtiveListener(new ViewDialog.INegativeListener() {
                    @Override
                    public void negtiveClick() {
                        MobclickAgent.onEvent(OrderCancellActivity.this, UEvent.ORDER_CANCEL_NO);
                    }
                }, "暂不取消");
                android.support.v7.app.AlertDialog dialog = builder.create();
                dialog.show();


            }
        });

        findViewById(R.id.go_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
