package maimeng.yodian.app.client.android.view.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Window;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.view.FeedBackActivity;

/**
 * Created by xujianhua on 9/7/15.
 */
public class TransActivity extends AppCompatActivity{
    private final int REQUEST_CODE=0x23;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window=getWindow();
        window.setWindowAnimations(R.style.FeedBackAnim);
        window.setGravity(Gravity.BOTTOM);
        setContentView(R.layout.activity_trans);
        startActivityForResult(new Intent(this, FeedBackActivity.class), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE){
//            finish();
        }
    }
}
