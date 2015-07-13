package maimeng.yodian.app.client.android;

import android.support.design.widget.FloatingActionButton;
import android.view.View;

/**
 * Created by android on 15-7-13.
 */
public class MainHomeProxy implements ActivityProxy{
    private final View mView;
    private final MainActivity mActivity;

    public MainHomeProxy(MainActivity activity, View view){
        this.mView=view;
        this.mActivity=activity;
    }

    @Override
    public void onTitleChanged(CharSequence title, int color) {

    }

    @Override
    public void show(FloatingActionButton button) {

    }

    @Override
    public void hide(FloatingActionButton button) {

    }

    @Override
    public boolean isShow() {
        return false;
    }
}
