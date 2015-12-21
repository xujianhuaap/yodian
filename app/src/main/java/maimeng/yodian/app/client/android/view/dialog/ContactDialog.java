package maimeng.yodian.app.client.android.view.dialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import org.henjue.library.share.Type;
import org.henjue.library.share.manager.AuthFactory;
import org.henjue.library.share.manager.WechatAuthManager;

import maimeng.yodian.app.client.android.R;

/**
 * Created by android on 2015/8/7.
 */
public class ContactDialog extends AppCompatActivity implements View.OnClickListener {
    private String mWeChatNum;
    private boolean isWechat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String contactMethod = getResources().getString(R.string.contact_method_wechat);
        mWeChatNum = intent.getStringExtra("wechat");
        isWechat = true;
        if (intent.hasExtra("qq")) {
            mWeChatNum = intent.getStringExtra("qq");
            isWechat = false;
            contactMethod = getResources().getString(R.string.contact_method_qq);
        }
        View view = getLayoutInflater().inflate(R.layout.dialog_contact, null);
        TextView weChat = (TextView) view.findViewById(R.id.tv_contact_title);
        TextView subTitle = (TextView) view.findViewById(R.id.tv_contact_subtitle);
        TextView exit = (TextView) view.findViewById(R.id.btn_cancel);
        TextView enter = (TextView) view.findViewById(R.id.btn_enter_contact);
        exit.setOnClickListener(this);
        enter.setOnClickListener(this);
        setFinishOnTouchOutside(true);
        setContentView(view);

        Spanned title = Html.fromHtml(getResources().getString(R.string.contact_title, contactMethod, mWeChatNum));
        Spanned subTitleSpan = Html.fromHtml(getResources().getString(R.string.contact_subtitle, contactMethod));
        weChat.setText(title);
        subTitle.setText(subTitleSpan);

        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText("wechat", mWeChatNum);
        clipboardManager.setPrimaryClip(data);

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_enter_contact) {
            if (isWechat) {
                AuthFactory.create(this, Type.Platform.WEIXIN);
                if (WechatAuthManager.getIWXAPI().isWXAppInstalled()) {
                    WechatAuthManager.getIWXAPI().openWXApp();
                    finish();
                }
            } else {

                String format = "mqqwpa://im/chat?chat_type=wpa&uin=%s";
                String url = String.format(format, mWeChatNum);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                finish();

            }

        } else if (v.getId() == R.id.btn_cancel) {
            finish();
        }
    }
}
