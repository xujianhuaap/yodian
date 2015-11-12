package maimeng.yodian.app.client.android2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public abstract class AbstractActivity extends AppCompatActivity  {
    private FrameLayout mContent;
    protected TextView mTitle;
    protected Toolbar mToolBar;


    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
     * toolbar初始化完成时调用
     *
     * @param toolbar
     */
    protected void initToolBar(Toolbar toolbar) {

    }

}
