package maimeng.yodian.app.client.android.view.deal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Gravity;
import android.widget.TextView;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.model.OrderInfo;

/**
 * Created by xujianhua on 10/12/15.
 */
public class PayListActivity extends AppCompatActivity {

    private TextView mTitle;
    private OrderInfo mInfo;

    /***
     *
     * @param context
     */
    public  static void show(Context context,OrderInfo orderInfo){
        Intent intent=new Intent(context,PayListActivity.class);
        intent.putExtra("orderInfo",orderInfo);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setGravity(Gravity.BOTTOM);
        setContentView(R.layout.activity_pay_list);

        OrderInfo orderInfo=getIntent().getParcelableExtra("orderInfo");
        mTitle=(TextView)findViewById(R.id.pay_title);
        mTitle.setText(Html.fromHtml(getResources().getString(R.string.pay_list_title,orderInfo.getTotal_fee())));
    }
}
