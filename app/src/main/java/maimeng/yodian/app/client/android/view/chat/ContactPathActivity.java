package maimeng.yodian.app.client.android.view.chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.model.skill.Skill;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.view.deal.VouchApplyActivity;
import maimeng.yodian.app.client.android.view.dialog.ContactDialog;
import maimeng.yodian.app.client.android.view.dialog.ViewDialog;

/**
 * Created by xujianhua on 10/16/15.
 */
public class ContactPathActivity extends AppCompatActivity{


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
        Skill skill=getIntent().getParcelableExtra("skill");
        final String wechat=skill.getWeichat();
        String qq=skill.getQq();
        final String phone=skill.getContact();
        TextView tvWeChat=(TextView)findViewById(R.id.tv_wechat);
        TextView tvQQ=(TextView)findViewById(R.id.tv_QQ);
        TextView tvPhone=(TextView)findViewById(R.id.tv_phone);

        tvWeChat.setText(skill.getWeichat());
        tvQQ.setText(skill.getQq());
        tvPhone.setText(skill.getContact());

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
                Matcher matcher = pattern.matcher(phone);
                if (matcher.matches()) {
                    final String num = phone;
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
