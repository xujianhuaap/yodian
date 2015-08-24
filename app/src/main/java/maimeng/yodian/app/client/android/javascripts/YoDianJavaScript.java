package maimeng.yodian.app.client.android.javascripts;

import android.app.Activity;
import android.webkit.WebView;

import maimeng.yodian.app.client.android.utils.WebLauncherUtils;

/**
 * Created by android on 2015/8/24.
 */
public class YoDianJavaScript {
    private final Activity mContext;
    private final WebView web;

    public YoDianJavaScript(Activity context, WebView web) {
        this.mContext = context;
        this.web = web;
    }

    @android.webkit.JavascriptInterface
    public boolean startActivity(final String url) {
        return WebLauncherUtils.handler(mContext, url);
    }

    @android.webkit.JavascriptInterface
    public boolean canBack() {
        return web.canGoBack();
    }

    @android.webkit.JavascriptInterface
    public void back() {
        web.goBack();
    }

    @android.webkit.JavascriptInterface
    public void close() {
        mContext.finish();
    }
}
