package maimeng.yodian.app.client.android2.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.parceler.Parcels;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import maimeng.yodian.app.client.android2.R;

public abstract class AbstractActivity extends AppCompatActivity  {
    private FrameLayout mContent;
    protected TextView mTitle;
    protected Toolbar mToolBar;
    private View mProgress;
    protected Realm realm;


    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm=Realm.getDefaultInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(realm!=null){
            realm.close();
        }
    }

    protected <T> T get(String key){
        return Parcels.unwrap(getIntent().getParcelableExtra(key));
    }
    protected <E extends RealmObject> void remove(E item){
        realm.beginTransaction();
        item.removeFromRealm();
        realm.commitTransaction();
    }
    protected <E extends RealmObject> RealmQuery<E> where(Class<E> eClass){
        return realm.where(eClass);
    }
    protected <E extends RealmObject> E saveOrUpdate(E e){
        realm.beginTransaction();
        E item = realm.copyToRealmOrUpdate(e);
        realm.commitTransaction();
        return item;
    }
    protected <E extends RealmObject> E save(E e){
        realm.beginTransaction();
        E item = realm.copyToRealm(e);
        realm.commitTransaction();
        return item;
    }
    protected  <E extends RealmObject> List<E> saveOrUpdate(Iterable<E> e){
        realm.beginTransaction();
        List<E> items = realm.copyToRealm(e);
        realm.commitTransaction();
        return items;
    }
    protected  <E extends RealmObject> List<E> save(Iterable<E> e){
        realm.beginTransaction();
        List<E> items = realm.copyToRealm(e);
        realm.commitTransaction();
        return items;
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
        mTitle = (TextView) findViewById(R.id.base_title);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolBar != null) {
            mToolBar.setTitle("");
            setSupportActionBar(mToolBar);
            initToolBar(mToolBar);

        }
        getLayoutInflater().inflate(layoutResID, mContent, true);
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    protected void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mContent.setVisibility(show ? View.GONE : View.VISIBLE);
            mContent.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mContent.setVisibility(show ? View.GONE : View.VISIBLE);
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

}
