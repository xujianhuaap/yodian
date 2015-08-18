package maimeng.yodian.app.client.android.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Set;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.utils.WebLauncherUtils;


public class WebLauncher extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeTransparent);
        super.onCreate(savedInstanceState);
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        WebLauncherUtils.handler(this, uri);
        finish();
    }
}
