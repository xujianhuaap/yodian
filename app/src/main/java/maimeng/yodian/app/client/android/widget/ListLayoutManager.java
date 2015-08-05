package maimeng.yodian.app.client.android.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by android on 2015/8/5.
 */
public class ListLayoutManager extends LinearLayoutManager {
    public ListLayoutManager(Context context) {
        super(context);
    }

    public ListLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public ListLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected int getExtraLayoutSpace(RecyclerView.State state) {
        //return super.getExtraLayoutSpace(state);
        return 5;
    }
}
