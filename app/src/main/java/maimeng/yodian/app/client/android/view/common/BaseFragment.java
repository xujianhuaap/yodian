package maimeng.yodian.app.client.android.view.common;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.parceler.Parcels;

import maimeng.yodian.app.client.android.R;

/**
 * Created by android on 15-10-10.
 */
public abstract class BaseFragment extends Fragment {
    public static final int REQUEST_CREATE_SKILL = 0x2403;//新建技能
    public static final int REQUEST_EDIT_SKILL = 0x2404;//编辑技能
    private FrameLayout mContent;
    private TextView mTitle;
    private View mProgress;
    private View mError;
    private Toolbar mToolBar;

    protected void showError(boolean show) {
        mProgress.setVisibility(View.GONE);
        if (show) {
            mError.setVisibility(View.VISIBLE);
            if (mContent != null) mContent.setVisibility(View.GONE);
        } else {
            mError.setVisibility(View.GONE);
            if (mContent != null) mContent.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    protected void showProgress(final boolean show) {
        showProgress(show, mContent);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show, final View view) {
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

    protected <T extends View> T findViewById(@IdRes int id) {
        return (T) getView().findViewById(id);
    }

    public boolean isShowTitle() {
        return showTitle;
    }

    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }

    private boolean showTitle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager();
    }

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        if (AbstractActivity.class.isInstance(activity)) {
            if (((AbstractActivity) activity).isShowTitle()) {
                showTitle = false;
            } else {
                showTitle = true;
            }
        }
        View view = onCreateView(inflater, showTitle);
        if (view == null) {
            return null;
        }
        View root = inflater.inflate(R.layout.activity_base, container, false);
        mContent = (FrameLayout) root.findViewById(R.id.base_content);
        mTitle = (TextView) root.findViewById(R.id.base_title);
        mToolBar = (Toolbar) root.findViewById(R.id.toolbar);
        if (!showTitle) {
            mToolBar.setVisibility(View.GONE);
        }
        mProgress = root.findViewById(R.id.progress);
        mError = root.findViewById(R.id.error);
        mError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showError(false);
                onRetry();
            }
        });
        if (mToolBar != null && AppCompatActivity.class.isInstance(getActivity())) {
            mToolBar.setTitle("");
            ((AppCompatActivity) getActivity()).setSupportActionBar(mToolBar);
            initToolBar(mToolBar);

        }
        mContent.removeAllViews();
        mContent.addView(view);
        return root;
    }

    /**
     * toolbar初始化完成时调用
     *
     * @param toolbar
     */
    protected void initToolBar(Toolbar toolbar) {

    }

    protected void onRetry() {

    }

    public abstract View onCreateView(LayoutInflater inflater, boolean showTitle);

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    protected <T> T get(String key) {
        return Parcels.unwrap(getArguments().getParcelable(key));
    }
}
