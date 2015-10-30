package maimeng.yodian.app.client.android.view.chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.view.dialog.ContactDialog;
import maimeng.yodian.app.client.android.view.dialog.ViewDialog;

/**
 * Created by xujianhua on 10/16/15.
 */
public class ContactPathActivity extends AppCompatActivity{


    private String mPhone;
    private String wechat;
    private String mQQ;

    /***
     *
     * @param context
     */

    public static void show(Context context,User.Info info){
        Intent intent=new Intent(context,ContactPathActivity.class);
        intent.putExtra("info",info);
        context.startActivity(intent);
    }

    /***
     *
     * @param context
     */

    public static void show(Context context,Skill skill){
        Intent intent=new Intent(context,ContactPathActivity.class);
        intent.putExtra("skill",skill);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setGravity(Gravity.BOTTOM);
        setContentView(R.layout.activity_contact_path);
        Intent intent=getIntent();
        if(intent.hasExtra("skill")){
            Skill  skill =intent.getParcelableExtra("skill");
            wechat = skill.getWeichat();
            mQQ = skill.getQq();
            mPhone = skill.getContact();
        }else {
            User.Info user=intent.getParcelableExtra("info");
            wechat = user.getWechat();
            mQQ = user.getQq();
            mPhone = user.getContact();
        }

        if(TextUtils.isEmpty(wechat)){
            wechat =getString(R.string.contact_no);
        }
        if(TextUtils.isEmpty(mQQ)){
            mQQ =getString(R.string.contact_no);;
        }
        if(TextUtils.isEmpty(mPhone)){
            mPhone =getString(R.string.contact_no);;
        }
        TextView tvWeChat=(TextView)findViewById(R.id.tv_wechat);
        TextView tvQQ=(TextView)findViewById(R.id.tv_QQ);
        TextView tvPhone=(TextView)findViewById(R.id.tv_phone);

        tvWeChat.setText(wechat);
        tvQQ.setText(mQQ);
        tvPhone.setText(mPhone);

        findViewById(R.id.wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ContactPathActivity.this, ContactDialog.class).putExtra("wechat", wechat));
                finish();
            }
        });


        findViewById(R.id.qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ContactPathActivity.this, ContactDialog.class).putExtra("qq", wechat));
                finish();
            }
        });

        findViewById(R.id.phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pattern pattern = Pattern.compile("\\d+?");
                Matcher matcher = pattern.matcher(mPhone);
                if (matcher.matches()) {
                    final String num = mPhone;
                    ViewDialog.Builder builder = new ViewDialog.Builder(ContactPathActivity.this);
                    builder.setMesage(num);
                    builder.setTitle("");
                    builder.setPositiveListener(new ViewDialog.IPositiveListener() {
                        @Override
                        public void positiveClick() {
                            Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + num));
                            startActivity(intent);
                            finish();

                        }
                    }, getString(R.string.btn_name_confirm));
                    builder.setNegtiveListener(new ViewDialog.INegativeListener() {
                        @Override
                        public void negtiveClick() {
                            finish();
                        }
                    }, getString(R.string.btn_name_cancel));

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }


            }
        });
    }
}
