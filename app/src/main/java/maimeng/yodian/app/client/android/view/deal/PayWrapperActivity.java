package maimeng.yodian.app.client.android.view.deal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import org.parceler.Parcels;

import maimeng.yodian.app.client.android.model.OrderInfo;
import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.utils.LogUtil;

/**
 * Created by xujianhua on 10/12/15.
 */
public class PayWrapperActivity extends AppCompatActivity {
    private final int REQUEST_CODE = 0x23;

    /***
     * 订单支付
     *
     * @param context
     * @param info
     */
//    public static void show(Context context, OrderInfo info) {
//        Intent intent = new Intent(context, PayWrapperActivity.class);
//        intent.putExtra("info", Parcels.wrap(info));
//        context.startActivity(intent);
//    }

    /***
     * 订单支付
     *
     * @param context
     * @param info
     * @param requestCode
     */
    public static void show(Activity context, OrderInfo info, int requestCode) {
        Intent intent = new Intent(context, PayWrapperActivity.class);
        intent.putExtra("info", Parcels.wrap(info));
        context.startActivityForResult(intent, requestCode, new Bundle());
    }

    /***
     * 订单支付
     *
     * @param context
     * @param info
     * @param requestCode
     */
    public static void show(Fragment context, OrderInfo info, int requestCode) {
        Intent intent = new Intent(context.getContext(), PayWrapperActivity.class);
        intent.putExtra("info", Parcels.wrap(info));
        context.startActivityForResult(intent, requestCode);
    }

    /***
     * 技能直接购买
     *
     * @param context
     * @param info
     */
    public static void show(Context context, Skill info) {
        Intent intent = new Intent(context, PayWrapperActivity.class);
        intent.putExtra("skill", Parcels.wrap(info));
        context.startActivity(intent, new Bundle());
    }

    /***
     * 技能直接购买
     *
     * @param context
     * @param info
     */
    public static void show(Activity context, Skill info, int requestCode) {
        Intent intent = new Intent(context, PayWrapperActivity.class);
        intent.putExtra("skill", Parcels.wrap(info));
        intent.putExtra("result", true);
        context.startActivityForResult(intent, requestCode, new Bundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent.hasExtra("info")) {
            OrderInfo info = Parcels.unwrap(intent.getParcelableExtra("info"));
            PayListActivity.show(this, info, REQUEST_CODE);
        } else {
            Skill skill = Parcels.unwrap(intent.getParcelableExtra("skill"));
            PayListActivity.show(this, skill, REQUEST_CODE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            setResult(RESULT_OK, data);
            finish();
        }
    }
}
