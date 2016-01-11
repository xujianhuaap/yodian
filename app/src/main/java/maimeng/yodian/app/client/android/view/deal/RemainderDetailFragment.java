package maimeng.yodian.app.client.android.view.deal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import java.util.List;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.adapter.AbstractAdapter;
import maimeng.yodian.app.client.android.adapter.RemainderDetailAdapter;
import maimeng.yodian.app.client.android.common.PullHeadView;
import maimeng.yodian.app.client.android.model.BalanceInfo;
import maimeng.yodian.app.client.android.model.OrderInfo;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.BalanceResponse;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.service.MoneyService;
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
    private int page=1;
    private boolean isAppend;
    private TextView noBalance;
    private MoneyService service;
    private int remainderType;


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
        remainderType = bundle.getInt("remaindertype");
        service = Network.getService(MoneyService.class);
        refreshData();
        recyclerView=(RecyclerView)view.findViewById(R.id.recyclerView);
        frameLayout=(PtrFrameLayout)view.findViewById(R.id.refresh_layout);
        noBalance=(TextView)view.findViewById(R.id.no_remainder);
        if(remainderType ==0){
            noBalance.setText(getResources().getString(R.string.no_balance_total));
        }else if(remainderType ==1){
            noBalance.setText(getResources().getString(R.string.no_balance_income));
        }else {
            noBalance.setText(getResources().getString(R.string.no_balance_fee));
        }
        frameLayout.setPtrHandler(this);
        StoreHouseHeader header = PullHeadView.create(getActivity()).setTextColor(0x0);
        frameLayout.addPtrUIHandler(header);
        frameLayout.setHeaderView(header);
        ListLayoutManager layoutManager=new ListLayoutManager(getActivity());
        EndlessRecyclerOnScrollListener recyclerOnScrollListener=new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore() {
                page++;
                isAppend=true;
                refreshData();
            }
        };
        adapter = new RemainderDetailAdapter(getActivity(),this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(recyclerOnScrollListener);
        recyclerView.setAdapter(adapter);


    }

    private void refreshData() {
        service.getBalaceInfo(remainderType, page, new CallBackProxy());
    }


    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(frameLayout, content, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        page=1;
        isAppend=false;
        refreshData();
    }


    @Override
    public void onClick(Object holder, View clickItem, int postion) {

    }

    @Override
    public void onItemClick(Object holder, int postion) {
        BalanceInfo orderInfo=adapter.getItem(postion);
        boolean isOrder=false;
        if(orderInfo.getType()==1||orderInfo.getType()==4||orderInfo.getType()==7||orderInfo.getType()==8){
            isOrder=true;
        }
        if(isOrder){
            OrderDetailActivity.show(getActivity(),orderInfo.getSrcid());
        }

    }

    public final class CallBackProxy implements Callback<BalanceResponse>{
        @Override
        public void end() {
            frameLayout.refreshComplete();
        }

        @Override
        public void start() {

        }

        @Override
        public void success(BalanceResponse balanceResponse, Response response) {
            if(balanceResponse.isSuccess()){
                List<BalanceInfo>lists=balanceResponse.getData().getList();
                if(lists!=null){
                    if(lists.size()>0){
                        adapter.reload(balanceResponse.getData().getList(),isAppend);
                        adapter.notifyDataSetChanged();

                    }else{
                        if(page==1){
                            noBalance.setVisibility(View.VISIBLE);
                            frameLayout.setVisibility(View.GONE);
                        }
                    }
                }
            }else{
                Toast.makeText(getActivity(),balanceResponse.getMsg(),Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void failure(HNetError hNetError) {
            ErrorUtils.checkError(getActivity(),hNetError);
        }
    }

}
