package maimeng.yodian.app.client.android.widget;

import android.support.v7.widget.RecyclerView;

/**
 * Created by android on 15-7-12.
 */
public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;


    private ListLayoutManager mLinearLayoutManager;

    public EndlessRecyclerOnScrollListener(ListLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);


    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            visibleItemCount = recyclerView.getChildCount();
            totalItemCount = mLinearLayoutManager.getItemCount();
            firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
            if (loading) {
                if (totalItemCount >= previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            if (mLinearLayoutManager.findFirstCompletelyVisibleItemPosition() != 0 && !loading && (totalItemCount - visibleItemCount)
                    <= (firstVisibleItem + visibleThreshold)) {
                onLoadMore();
                loading = true;
            }
        }
    }

    public abstract void onLoadMore();
}
