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
    private ObjectAnimator objectAnimator;
    private ObjectAnimator dismissAnimator;
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
     * 初始化动画
     * @param offset
     */
    private void initAnimator(int offset) {

        if(objectAnimator==null){
            objectAnimator = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, 0);
        }

        dismissAnimator=null;
        if(dismissAnimator==null){
            offset=-offset;
            dismissAnimator = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y,offset);
        }

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
     * 伴随动画展现控件
     * @param animDuration
     */
    public void show(int animDuration){
        this.setVisibility(VISIBLE);
        initAnimator(this.getHeight());
       if(objectAnimator!=null&&!objectAnimator.isRunning()){
           objectAnimator.setDuration(animDuration);
           objectAnimator.start();
       }

    }

    /***
     * 伴随动画控件消失
     * @param animDuration
     */
    public void dismiss(int animDuration){
        this.setVisibility(GONE);
        initAnimator(this.getHeight());
        if(dismissAnimator!=null&&!dismissAnimator.isRunning()){
            dismissAnimator.setDuration(animDuration);
            dismissAnimator.start();
        }

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
