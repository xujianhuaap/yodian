package maimeng.yodian.app.client.android.view.skill;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.GridView;
import android.widget.PopupWindow;

import java.util.ArrayList;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.adapter.ThemeAdapter;
import maimeng.yodian.app.client.android.model.Theme;
import maimeng.yodian.app.client.android.network.service.SkillService;

/**
 * Created by android on 15-8-10.
 */
public class CategoryWindow extends PopupWindow implements ThemeAdapter.AdapterClickListener{

    private CategoryClickListener mCategoryClickListener;



    public CategoryWindow(Context context) {
        super(context);
    }

    public CategoryWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CategoryWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CategoryWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    public static CategoryWindow show(Context context, ArrayList<Theme> themes,CategoryClickListener listener) {
        View view = View.inflate(context, R.layout.activity_theme_select, null);
        ThemeAdapter adapter = new ThemeAdapter(context);
        adapter.reload(themes, true);
        GridView gridView=((GridView) view.findViewById(R.id.theme));
        gridView.setAdapter(adapter);
        CategoryWindow instance=new CategoryWindow(context);
        adapter.setAdapterClickListener(instance);
        instance.mCategoryClickListener=listener;
        instance.setContentView(view);
        instance.setOutsideTouchable(true);
        instance.setClippingEnabled(false);
        instance.setWidth(context.getResources().getDisplayMetrics().widthPixels + 10);
        instance.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        instance.setAnimationStyle(R.style.CatogoryWindowAnimation);



        return instance;
    }

    @Override
    public void onClickListner(View v, Theme theme) {
        mCategoryClickListener.onClickListener(v,theme);
    }

    public interface CategoryClickListener {
        public void onClickListener(View v,Theme theme);
    }
}
