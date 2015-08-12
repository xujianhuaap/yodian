package maimeng.yodian.app.client.android.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import java.util.ArrayList;

import maimeng.yodian.app.client.android.adapter.ThemeAdapter;
import maimeng.yodian.app.client.android.model.Theme;


/**
 * Created by android on 15-8-11.
 */
public class CategoryView extends GridView implements ThemeAdapter.AdapterClickListener{

    private static final String LOG_TAG =CategoryView.class.getName() ;

    private ThemeAdapter adapter;
    private CategoryClickListener mCategoryClickListener;

    public CategoryView(Context context) {
        super(context);

    }


    public CategoryView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public CategoryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


    public void setCategoryClickListener(CategoryClickListener categoryClickListener) {
        this.mCategoryClickListener = categoryClickListener;
    }


    /***
     * 为该控件绑定数据
     * @param context
     * @param themes
     */
    public  void  bindData(Context context, ArrayList<Theme> themes) {
        adapter = new ThemeAdapter(context);
        adapter.reload(themes, true);
        this.setAdapter(adapter);
        adapter.setAdapterClickListener(this);
    }



    /***
     * 点击刷新category
     * @param v
     * @param theme
     */
    @Override
    public void onClickListner(View v, Theme theme) {
        mCategoryClickListener.onClickListener(v,theme);
    }

    /***
     *
     */
    public interface CategoryClickListener {
        public void onClickListener(View v,Theme theme);
    }
}
