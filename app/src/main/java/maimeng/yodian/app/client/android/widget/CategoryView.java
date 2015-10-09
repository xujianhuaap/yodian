package maimeng.yodian.app.client.android.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import java.util.List;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.adapter.ThemeAdapter;
import maimeng.yodian.app.client.android.model.skill.Theme;


/**
 * Created by android on 15-8-11.
 */
public class CategoryView extends GridView implements ThemeAdapter.AdapterClickListener {

    private static final String LOG_TAG = CategoryView.class.getName();

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
     * 为该控件绑定数据
     *
     * @param context
     * @param themes
     */
    public void bindData(Context context, List<Theme> themes) {
        if(adapter==null){
            adapter = new ThemeAdapter(context);
        }
        adapter.reload(themes, false);
        this.setAdapter(adapter);
        adapter.setAdapterClickListener(this);
    }


    /***
     * 点击刷新category
     *
     * @param v
     * @param theme
     */
    @Override
    public void onClickListner(View v, Theme theme,int position) {
        for(int i=0;i<getChildCount();i++){
           View view = getChildAt(i).findViewById(R.id.white_dot);
            if(position==i){
                view.setVisibility(VISIBLE);
            }else{
                view.setVisibility(INVISIBLE);
            }
        }
        mCategoryClickListener.onClickListener(v, theme);
    }

    /***
     *
     */
    public interface CategoryClickListener {
        public void onClickListener(View v, Theme theme);
    }
}
