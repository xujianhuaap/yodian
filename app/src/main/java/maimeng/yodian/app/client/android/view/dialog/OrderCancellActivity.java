package maimeng.yodian.app.client.android.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.*;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.easemob.applib.controller.HXSDKHelper;
import com.umeng.analytics.MobclickAgent;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import io.realm.internal.TableView;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.chat.DemoHXSDKHelper;
import maimeng.yodian.app.client.android.chat.activity.ChatActivity;
import maimeng.yodian.app.client.android.chat.domain.RobotUser;
import maimeng.yodian.app.client.android.common.UEvent;
import maimeng.yodian.app.client.android.model.chat.ChatUser;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.service.OrderService;
import maimeng.yodian.app.client.android.view.deal.BasicalInfoConfirmActivity;
import maimeng.yodian.app.client.android.view.deal.BindStatus;

/**
 * Created by xujianhua on 10/16/15.
 */
public class OrderCancellActivity extends AppCompatActivity {
    private static final int REQUEST_CERTIFY = 0x234;
    private BindStatus status;

    /***
     * @param context
     */

    public static void show(Activity context, long oid, boolean isCancel,int requestCode) {
        Intent intent = new Intent(context, OrderCancellActivity.class);
        intent.putExtra("oid", oid);
        intent.putExtra("isCancle", isCancel);
        context.startActivityForResult(intent,requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        status = User.read(this).getInfo().getVouch_status();
        getWindow().setGravity(Gravity.BOTTOM);
        setContentView(R.layout.activity_order_cancel);
        findViewById(R.id.contact_us).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //请联系客服

                RobotUser robot = new RobotUser();
                robot.setUsername("hx_admin");

                maimeng.yodian.app.client.android.chat.domain.User user = new maimeng.yodian.app.client.android.chat.domain.User();
                user.setUsername("hx_admin");

                // 存入内存
                ((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveOrUpdate(robot);
                ((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveOrUpdate(user);
                MobclickAgent.onEvent(OrderCancellActivity.this, UEvent.CONTACT_TA);
                ChatActivity.show(OrderCancellActivity.this, new ChatUser(user.getUsername(), 0, null));
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
                                long oid = getIntent().getLongExtra("oid", 0);
                                Network.getService(OrderService.class).cancleOrder(oid, new Callback<ToastResponse>() {
                                    @Override
                                    public void start() {

                                    }

                                    @Override
                                    public void success(ToastResponse toastResponse, Response response) {
                                        Toast.makeText(OrderCancellActivity.this, toastResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                        setResult(RESULT_OK,getIntent().putExtra("isOperator",true));
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
