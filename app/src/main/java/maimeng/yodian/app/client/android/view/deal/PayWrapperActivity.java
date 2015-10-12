package maimeng.yodian.app.client.android.view.deal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import maimeng.yodian.app.client.android.model.OrderInfo;

/**
 * Created by xujianhua on 10/12/15.
 */
public class PayWrapperActivity extends AppCompatActivity {
    private final int REQUEST_CODE=0x23;

    public static void show(Context context,OrderInfo info){
        Intent intent=new Intent(context,PayWrapperActivity.class);
        intent.putExtra("info",info);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OrderInfo info=getIntent().getParcelableExtra("info");
        PayListActivity.show(this,info,REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE){
            finish();
        }
    }
}
