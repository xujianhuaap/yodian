package maimeng.yodian.app.client.android.view.deal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import maimeng.yodian.app.client.android.model.OrderInfo;
import maimeng.yodian.app.client.android.model.skill.Skill;

/**
 * Created by xujianhua on 10/12/15.
 */
public class PayWrapperActivity extends AppCompatActivity {
    private final int REQUEST_CODE=0x23;

    /***
     * 订单支付
     * @param context
     * @param info
     */
    public static void show(Context context,OrderInfo info){
        Intent intent=new Intent(context,PayWrapperActivity.class);
        intent.putExtra("info",info);
        context.startActivity(intent);
    }

    /***
     * 技能直接购买
     * @param context
     * @param info
     */
    public static void show(Context context,Skill info){
        Intent intent=new Intent(context,PayWrapperActivity.class);
        intent.putExtra("skill",info);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        if(intent.hasExtra("info")){
            OrderInfo info=intent.getParcelableExtra("info");
            PayListActivity.show(this,info,REQUEST_CODE);
        }else{
            Skill skill=intent.getParcelableExtra("skill");
            PayListActivity.show(this,skill,REQUEST_CODE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE){
            finish();
        }
    }
}