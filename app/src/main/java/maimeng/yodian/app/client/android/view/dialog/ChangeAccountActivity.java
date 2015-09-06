package maimeng.yodian.app.client.android.view.dialog;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.chat.widget.photoview.Compat;
import maimeng.yodian.app.client.android.model.User;
import maimeng.yodian.app.client.android.view.MainTabActivity;

/**
 * Created by xujianhua on 9/1/15.
 */
public class ChangeAccountActivity extends AppCompatActivity implements View.OnClickListener{
    private User user;
    public static void show(Activity context){
        Intent intent=new Intent();
        intent.setClass(context, ChangeAccountActivity.class);
        ActivityOptionsCompat options=ActivityOptionsCompat.makeCustomAnimation(context,R.anim.fade_in,R.anim.fade_out);
        ActivityCompat.startActivity(context,intent,options.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountchange);
        user=User.read(this);
        final Window window=getWindow();

        setFinishOnTouchOutside(true);

        ActionBar actionBar=getActionBar();
        if(actionBar!=null){
            actionBar.hide();
            actionBar.setTitle("");
        }


        window.setGravity(Gravity.BOTTOM);
        TextView tvAccountName = (TextView) findViewById(R.id.tvAccountName);
        TextView changeAccount = (TextView) findViewById(R.id.tvChangeAccount);
        tvAccountName.setTextColor(Color.BLACK);
        tvAccountName.setText("你当前账号为"+user.getNickname());
        changeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.clear(ChangeAccountActivity.this);
                Intent intent = new Intent(ChangeAccountActivity.this, MainTabActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        TextView cancleChange = (TextView) findViewById(R.id.tvCancel);
        cancleChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (window != null) {
                    finish();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {

    }
}
