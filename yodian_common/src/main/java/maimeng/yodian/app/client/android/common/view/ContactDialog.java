package maimeng.yodian.app.client.android.common.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import maimeng.yodian.app.client.android.common.R;
import pl.droidsonroids.gif.GifImageView;


/**
 * Created by android on 2015/8/7.
 */
public class ContactDialog extends AppCompatActivity implements View.OnClickListener{
    private String mWeChatNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.contact_dialog);
        super.onCreate(savedInstanceState);

        mWeChatNum = getIntent().getStringExtra("wechat");

        View view = getLayoutInflater().inflate(R.layout.dialog_contact, null);
        GifImageView gifImageView = (GifImageView) view.findViewById(R.id.gif);
        TextView weChat = (TextView) view.findViewById(R.id.tv_wechat);
        Button exit=(Button)view.findViewById(R.id.btn_wechat_close);
        Button enter=(Button)view.findViewById(R.id.btn_enter_wechat);
        exit.setOnClickListener(this);
        enter.setOnClickListener(this);
        setFinishOnTouchOutside(true);
        setContentView(view);

        gifImageView.setImageResource(R.drawable.weixin);
        weChat.setText("已将微信号：" + mWeChatNum);

        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText("wechat" , mWeChatNum);
        clipboardManager.setPrimaryClip(data);

    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.btn_enter_wechat){


        }else if(v.getId()==R.id.btn_wechat_close){
            finish();
        }
    }
}
