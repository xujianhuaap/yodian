package maimeng.yodian.app.client.android.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.javascripts.YoDianJavaScript;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.utils.WebLauncherUtils;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setHomeButtonEnabled(true);
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
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

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Toast.makeText(WebViewActivity.this, error.getDescription(), Toast.LENGTH_SHORT).show();
            }


        };
        web.addJavascriptInterface(new YoDianJavaScript(this, web), "yodian");
        web.setWebViewClient(webViewClient);
        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                WebViewActivity.this.setProgress(newProgress * 1000);
            }
        });
        web.getSettings().setAllowFileAccess(true);
        web.loadUrl(imgth);
    }

}
