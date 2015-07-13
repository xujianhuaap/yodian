package maimeng.yodian.app.client.android.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Set;

import maimeng.yodian.app.client.android.R;


public class WebLauncher extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_launcher);
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String scheme = intent.getScheme();
        Uri uri = intent.getData();
        StringBuffer sb=new StringBuffer("scheme:" + scheme).append("\n");
        if (uri != null) {
            String host = uri.getHost();
            String path = uri.getPath();
            sb.append("host"+host).append("\n");
            sb.append("path"+path).append("\n");
            Set<String> params = uri.getQueryParameterNames();
            for(String key:params){
                sb.append(key+":"+uri.getQueryParameter(key)).append("\n");
            }
            ((TextView)findViewById(R.id.text)).setText(sb.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web_launcher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
