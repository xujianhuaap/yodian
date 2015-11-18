package maimeng.yodian.app.client.android.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.melnykov.fab.FloatingActionButton;

import org.henjue.library.hnet.exception.HNetError;
import org.parceler.Parcels;

import java.lang.reflect.Type;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.model.user.User;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.view.auth.AuthSeletorActivity;
import maimeng.yodian.app.client.android.view.dialog.AlertDialog;

/**
 * Created by android on 15-7-13.
 */
public abstract class AbstractActivity extends AppCompatActivity implements EMConnectionListener {
    private FrameLayout mContent;
    protected TextView mTitle;
    protected Toolbar mToolBar;
    private AlertDialog dialog;
    private View mProgress;
    private View mError;

    public FloatingActionButton getFloatButton() {
        return null;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        EMChatManager.getInstance().addConnectionListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EMChatManager.getInstance().removeConnectionListener(this);
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected(int error) {
        if (error == EMError.USER_REMOVED) {
            onCurrentAccountRemoved();
        } else if (error == EMError.CONNECTION_CONFLICT) {
            onConnectionConflict();
        } else {
            onConnectionDisconnected(error);
        }
    }

    private void onConnectionDisconnected(int error) {

    }

    private void onConnectionConflict() {
        if (dialog == null) {
            dialog = AlertDialog.newInstance("提示", getString(maimeng.yodian.app.client.android.R.string.connect_conflict));
            dialog.setPositiveListener(new AlertDialog.PositiveListener() {
                @Override
                public void onPositiveClick(DialogInterface d) {
                    User.clear(AbstractActivity.this);
                    Intent intent = new Intent(AbstractActivity.this, AuthSeletorActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    dialog.dismiss();
                    finish();
                }

                @Override
                public String positiveText() {
                    return getString(android.R.string.ok);
                }
            });
        }
        FragmentManager fm = getFragmentManager();
        if (fm.findFragmentByTag("_onconnectionconflict") == null) {
            dialog.show(fm, "_onconnectionconflict");
        }
    }

    private void onCurrentAccountRemoved() {

    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        if (mTitle != null) mTitle.setText(title);
    }

    public void setContentView(View view, boolean showTitle) {
        if (showTitle) {
            setContentView(view);
        } else {
            super.setContentView(view);
        }
    }

    public void setContentView(int layoutResID, boolean showTitle) {
        if (showTitle) {
            setContentView(layoutResID);
        } else {
            super.setContentView(layoutResID);
        }
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(R.layout.activity_base);
        mContent = (FrameLayout) findViewById(R.id.base_content);
        mTitle = (TextView) findViewById(R.id.base_title);
        mProgress=findViewById(R.id.progress);
        mError=findViewById(R.id.error);
        mError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRetry();
            }
        });
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolBar != null) {
            mToolBar.setTitle("");
            setSupportActionBar(mToolBar);
            initToolBar(mToolBar);

        }
        mContent.removeAllViews();
        mContent.addView(view);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_base);
        mContent = (FrameLayout) findViewById(R.id.base_content);
        mProgress=findViewById(R.id.progress);
        mError=findViewById(R.id.error);
        mError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRetry();
            }
        });
        mTitle = (TextView) findViewById(R.id.base_title);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolBar != null) {
            mToolBar.setTitle("");
            setSupportActionBar(mToolBar);
            initToolBar(mToolBar);

        }
        getLayoutInflater().inflate(layoutResID, mContent, true);
    }

    protected void onRetry(){

    }

    protected <T extends ViewDataBinding> T bindView(int layoutId) {
        final T view = DataBindingUtil.inflate(getLayoutInflater(), layoutId, null, false);
        setContentView(view.getRoot());
        return view;
    }

    protected void checkError(HNetError error) {
        ErrorUtils.checkError(this, error);
    }
    protected void showError(boolean show ){
        showProgress(show, mError);
    }
    @Deprecated
    protected void hideError(){
        showError(false);
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    protected void showProgress(final boolean show) {
        showProgress(show,mContent);
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show,final View view) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            view.setVisibility(show ? View.GONE : View.VISIBLE);
            view.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgress.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    /**
     * toolbar初始化完成时调用
     *
     * @param toolbar
     */
    protected void initToolBar(Toolbar toolbar) {

    }

    protected <T> T get(String key) {
        return Parcels.unwrap(getIntent().getParcelableExtra(key));
    }
}
