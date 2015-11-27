package maimeng.yodian.app.client.android.view.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;

import org.henjue.library.hnet.Response;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.common.DataCleanManager;
import maimeng.yodian.app.client.android.common.LauncherCheck;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.common.ToastCallback;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.service.CommonService;
import maimeng.yodian.app.client.android.view.user.TransActivity;

/**
 * Created by android on 2015/7/21.
 */
public class SettingsActivity extends AbstractActivity {
    private View mBtnChangeAccount;
    private View mBtnCleanCache;
    private CheckBox mPush;
    private TextView mCurrentVersion;
    PopupWindow window;
    User user;
    private CommonService mPushService;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ActivityCompat.finishAfterTransition(SettingsActivity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initToolBar(Toolbar toolbar) {
        super.initToolBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_go_back);
        }
    }

    public static boolean isPush(Context context) {
        return context.getSharedPreferences("push", Context.MODE_PRIVATE).getBoolean("_push", false);
    }

    private void setPush(final boolean statu) {
        getSharedPreferences("push", Context.MODE_PRIVATE).edit().putBoolean("_push", statu).apply();
        mPushService.push(statu ? 0 : 1, new ToastCallback(this) {
            @Override
            public void success(ToastResponse toastResponse, Response response) {
                if (toastResponse.isSuccess()) {
                    user.setPushOn(statu);
                    user.write(SettingsActivity.this);
                }
                //                super.success(toastResponse, response);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        user = User.read(SettingsActivity.this);
        mPushService = Network.getService(CommonService.class);
        mPush = (CheckBox) findViewById(R.id.push);
        mPush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setPush(isChecked);
            }
        });
        mPush.setChecked(user.isPushOn());
        mBtnChangeAccount = findViewById(R.id.btn_change_account);
        mBtnCleanCache = findViewById(R.id.btn_cleancache);
        mCurrentVersion = (TextView) findViewById(R.id.current_version);
        mCurrentVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!CheckUpdateDelegate.checking) {
                    Toast.makeText(v.getContext(), R.string.toast_checkversion, Toast.LENGTH_SHORT).show();
                    new CheckUpdateDelegate(SettingsActivity.this, true).checkUpdate();
                } else {
                    Toast.makeText(v.getContext(), R.string.checkversion_ing, Toast.LENGTH_SHORT).show();

                }
            }
        });
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            mCurrentVersion.setText(getString(R.string.currentVersion, packageInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        mBtnChangeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransActivity.show(SettingsActivity.this);
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
