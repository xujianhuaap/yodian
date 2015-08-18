package maimeng.yodian.app.client.android.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.view.skill.SkillDetailsActivity;
import maimeng.yodian.app.client.android.view.user.UserHomeActivity;


/**
 * Created by android on 15-6-29.
 */
public class WebViewActivity extends AbstractActivity {

    public static final String URL = "_url";
    private View mBtnBack;

    public static Intent newIntent(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(URL, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static void show(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(URL, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        final String imgth = getIntent().getStringExtra(URL);
        final WebView web = (WebView) findViewById(R.id.web);
        mBtnBack = findViewById(R.id.btn_back);
        ViewCompat.setTransitionName(mBtnBack, "back");
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (web.canGoBack()) {
                    web.goBack();
                } else {
                    ActivityCompat.finishAfterTransition(WebViewActivity.this);
                }
            }
        });
        WebSettings webSettings = web.getSettings();
        webSettings.setBlockNetworkLoads(false);
        webSettings.setBlockNetworkImage(false);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);

        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        };
        web.addJavascriptInterface(new YoDianJavaScript(this),"yodian");
        web.setWebViewClient(webViewClient);
        web.loadUrl(imgth);
    }

    public static class YoDianJavaScript{
        private final Activity mContext;
        public YoDianJavaScript(Activity context){
            this.mContext=context;
        }
        @android.webkit.JavascriptInterface
        public boolean startActivity(final String url){
            if(url==null)return false;
            Uri uri = Uri.parse(url);
            if(uri.getScheme().equalsIgnoreCase("intent")){
                String _type=uri.getQueryParameter("ydtype");
                String _id=uri.getQueryParameter("ydid");
                if(!TextUtils.isEmpty(_type) && !TextUtils.isEmpty(_id)){
                    int type=Integer.parseInt(_type);
                    long id=Long.parseLong(_id);
                    switch (type){
                        case 0:
                            mContext.startActivity(new Intent(mContext,UserHomeActivity.class).putExtra("uid",id));
                            return true;
                        case 1:
                            mContext.startActivity(new Intent(mContext,SkillDetailsActivity.class).putExtra("sid", id));
                            return true;
                    }
                }

            }
            return false;
        }
    }
}
