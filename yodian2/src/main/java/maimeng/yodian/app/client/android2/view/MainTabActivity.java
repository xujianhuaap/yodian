package maimeng.yodian.app.client.android2.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import org.parceler.Parcels;

import maimeng.yodian.app.client.android2.R;
import maimeng.yodian.app.client.android2.model.Auth;
import maimeng.yodian.app.client.android2.view.user.LoginByPhoneActivity;

public class MainTabActivity extends AbstractActivity {
    private static final int REQUEST_AUTH = 0x1001;
    private Auth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = where(Auth.class).findFirst();
        if (auth == null) {
            startActivityForResult(new Intent(this, LoginByPhoneActivity.class), REQUEST_AUTH);
        } else if (TextUtils.isEmpty(auth.getToken())) {
            remove(auth);
            startActivityForResult(new Intent(this, LoginByPhoneActivity.class), REQUEST_AUTH);
        } else {
        }

        setContentView(R.layout.activity_main_tab, false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_AUTH) {
                auth = get("auth");
            }
        }
    }
}
