package maimeng.yodian.app.client.android.view;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.common.DataCleanManager;
import maimeng.yodian.app.client.android.common.LauncherCheck;
import maimeng.yodian.app.client.android.model.User;

/**
 * Created by android on 2015/7/21.
 */
public class SettingsActivity extends AbstractActivity {
    private View mBtnBack;
    private View mBtnYijian;
    private View mBtnChangeAccount;
    private View mBtnCleanCache;
    private TextView mCurrentVersion;
    PopupWindow window;
    User user;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ActivityCompat.finishAfterTransition(SettingsActivity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.mm_title_back);
        }
        user = User.read(SettingsActivity.this);
        mBtnBack = findViewById(R.id.btn_back);
        mBtnYijian = findViewById(R.id.btn_yijian);
        mBtnChangeAccount = findViewById(R.id.btn_change_account);
        mBtnCleanCache = findViewById(R.id.btn_cleancache);
        mCurrentVersion = (TextView) findViewById(R.id.current_version);
        ViewCompat.setTransitionName(mBtnBack, "back");
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            mCurrentVersion.setText(getString(R.string.currentVersion, packageInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(SettingsActivity.this);
            }
        });
        mBtnYijian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pair<View, String> back = Pair.create((View) mBtnBack, "back");
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(SettingsActivity.this, back);
                ActivityCompat.startActivity(SettingsActivity.this, new Intent(SettingsActivity.this, FeedBackActivity.class), options.toBundle());
            }

        });
        mBtnChangeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = View.inflate(v.getContext(), R.layout.activity_accountchange, null);
                window = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
                window.setAnimationStyle(R.style.Base_Animation_AppCompat_DropDownUp);
                window.setBackgroundDrawable(new BitmapDrawable(getResources()));
                TextView tvAccountName = (TextView) view.findViewById(R.id.tvAccountName);
                TextView changeAccount = (TextView) view.findViewById(R.id.tvChangeAccount);
                changeAccount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        User.clear(SettingsActivity.this);
                        Intent intent = new Intent(SettingsActivity.this, MainTabActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
                TextView cancleChange = (TextView) view.findViewById(R.id.tvCancel);
                cancleChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (window != null) {
                            window.dismiss();
                        }
                    }
                });
                tvAccountName.setText(getString(R.string.currentAccount, user.getNickname()));
                window.showAtLocation(findViewById(R.id.root), Gravity.BOTTOM, 0, 0);
            }
        });
        mBtnCleanCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCache();
                Toast.makeText(SettingsActivity.this, R.string.clearSuccess, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void clearCache() {
        DataCleanManager.cleanDatabases(SettingsActivity.this);
        DataCleanManager.cleanExternalCache(SettingsActivity.this);
        DataCleanManager.cleanFiles(SettingsActivity.this);
        DataCleanManager.cleanInternalCache(SettingsActivity.this);
        DataCleanManager.cleanSharedPreference(SettingsActivity.this);
        user.write(SettingsActivity.this);
        LauncherCheck.updateFirstRun(this, false);
    }
}
