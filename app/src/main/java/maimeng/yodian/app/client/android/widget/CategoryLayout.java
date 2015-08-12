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
    private CategoryView mCategory;
    private ObjectAnimator objectAnimator;
    private ObjectAnimator dismissAnimator;

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
}
