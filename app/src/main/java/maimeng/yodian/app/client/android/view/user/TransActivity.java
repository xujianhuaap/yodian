package maimeng.yodian.app.client.android.view.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Window;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.view.common.FeedBackActivity;
import maimeng.yodian.app.client.android.view.dialog.ChangeAccountActivity;

/**
 * Created by xujianhua on 9/7/15.
 */
public class TransActivity extends AppCompatActivity{
    private final int REQUEST_CODE=0x23;

    /***
     *
     * @param context
     * @param flag  0x1 开启FeedBackActivity  0x2开启AccountActivity
     */
    public static void show(Context context,int flag){
        Intent intent=new Intent(context,TransActivity.class);
        intent.putExtra("flag",flag);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent=getIntent();
        int flag=intent.getIntExtra("flag", 0);
        Window window=getWindow();
        if(flag==0x1){
            window.setWindowAnimations(R.style.FeedBackAnim);
        }else {
            window.setWindowAnimations(R.style.account_change_dialog);
        }

        window.setGravity(Gravity.BOTTOM);
        setContentView(R.layout.activity_trans);
        if(flag==0x1){
            startActivityForResult(new Intent(this, FeedBackActivity.class), REQUEST_CODE);
        }else {
            startActivityForResult(new Intent(this, ChangeAccountActivity.class), REQUEST_CODE);
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
