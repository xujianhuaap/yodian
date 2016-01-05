package maimeng.yodian.app.client.android.view.deal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.adapter.AbstractAdapter;
import maimeng.yodian.app.client.android.adapter.RemainderDetailAdapter;
import maimeng.yodian.app.client.android.common.PullHeadView;
import maimeng.yodian.app.client.android.model.OrderInfo;
import maimeng.yodian.app.client.android.widget.EndlessRecyclerOnScrollListener;
import maimeng.yodian.app.client.android.widget.ListLayoutManager;

/**
 * Created by xujianhua on 05/01/16.
 */
public class RemainderDetailFragment extends Fragment implements PtrHandler,AbstractAdapter.ViewHolderClickListener{
    private static final int REMAINDER_TYPE_FEE=1;//支出列表
    private static final int REMAINDER_TYPE_TOTAL=2;//全部列表
    private static final int REMAINDER_TYPE_INCOME=0;//收入列表


    private RecyclerView recyclerView;
    private PtrFrameLayout frameLayout;
    private RemainderDetailAdapter adapter;


    public static RemainderDetailFragment newInstance(int type){
        RemainderDetailFragment remainderDetailFragment=new RemainderDetailFragment();
        Bundle bundle=new Bundle();
        bundle.putInt("remaindertype", type);
        remainderDetailFragment.setArguments(bundle);
        return remainderDetailFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_remainder,null);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle=getArguments();
        int remainderType=bundle.getInt("remaindertype");

        recyclerView=(RecyclerView)view.findViewById(R.id.recyclerView);
        frameLayout=(PtrFrameLayout)view.findViewById(R.id.refresh_layout);
        frameLayout.setPtrHandler(this);
        StoreHouseHeader header = PullHeadView.create(getActivity()).setTextColor(0x0);
        frameLayout.addPtrUIHandler(header);
        frameLayout.setHeaderView(header);
        ListLayoutManager layoutManager=new ListLayoutManager(getActivity());
        EndlessRecyclerOnScrollListener recyclerOnScrollListener=new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore() {
                //????????????????????????????????

            }
        };
        adapter = new RemainderDetailAdapter(getActivity(),this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(recyclerOnScrollListener);


    }


    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return false;
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {

    }


    @Override
    public void onClick(Object holder, View clickItem, int postion) {

    }

    @Override
    public void onItemClick(Object holder, int postion) {
        OrderInfo orderInfo=adapter.getItem(postion);
        OrderDetailActivity.show(getActivity(),orderInfo.getOid());
    }
}
