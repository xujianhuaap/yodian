package maimeng.yodian.app.client.android.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.provider.ContactsContract;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;

/**
 * Created by android on 15-8-12.
 */
public class CategoryLayout extends LinearLayout {


    public ObjectAnimator enterAnimator;
    public ObjectAnimator dismissAnimator;

    public CategoryLayout(Context context) {
        super(context);
    }

    public CategoryLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CategoryLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    /***
     * 初始化动画
     *
     */
    public void initAnimator(int duration,int enterOffSet) {

            enterAnimator = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y ,enterOffSet);
            enterAnimator.setDuration(duration);

            dismissAnimator = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y,-this.getHeight()-enterOffSet);
            dismissAnimator.setDuration(duration);




    }

}
