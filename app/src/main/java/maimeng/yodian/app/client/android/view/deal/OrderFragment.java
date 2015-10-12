package maimeng.yodian.app.client.android.view.deal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.adapter.OrderListAdapter;
import maimeng.yodian.app.client.android.model.OrderInfo;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.OrderRepsonse;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.service.OrderService;
import maimeng.yodian.app.client.android.widget.EndlessRecyclerOnScrollListener;
import maimeng.yodian.app.client.android.widget.ListLayoutManager;

/**
 * Created by xujianhua on 9/29/15.
 */
public class OrderFragment extends Fragment implements PtrHandler{
    private boolean isSaled;
    private int mPage=1;
    private PtrFrameLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private EndlessRecyclerOnScrollListener mEndlessRecyclerOnScrollListener;
    private OrderListAdapter mAdapter;
    private OrderService mService;
    private boolean mIsAppend;


    /***
     *
     * @param activity
     * @param isSaled 是否是已出售订单
     * @param viewId 占位view的id
     */
    public static void show(AppCompatActivity activity,boolean isSaled,int viewId){
        OrderFragment orderFragment=new OrderFragment();
        Bundle bundle=new Bundle();
        bundle.putBoolean("isSaled",isSaled);
        orderFragment.setArguments(bundle);
        activity.getSupportFragmentManager().beginTransaction().replace(viewId,orderFragment).commit();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_order, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRefreshLayout = (PtrFrameLayout) view.findViewById(R.id.refresh_layout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
//        StoreHouseHeader header = PullHeadView.create(getActivity());
//        header.setTextColor(0x000000);
//        mRefreshLayout.addPtrUIHandler(header);
//        mRefreshLayout.setHeaderView(header);
        mRefreshLayout.setPtrHandler(this);
        ListLayoutManager layout = new ListLayoutManager(getActivity());
        mEndlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(layout) {
            @Override
            public void onLoadMore() {
                mPage++;
                mIsAppend= true;
                freshData();
            }


        };
        mAdapter=new OrderListAdapter(getActivity(),new ViewHolderClickListenerProxy());
        mAdapter.setIsSaled(isSaled);
        mRecyclerView.setLayoutManager(layout);
        mRecyclerView.addOnScrollListener(mEndlessRecyclerOnScrollListener);
        mRecyclerView.setAdapter(mAdapter);

        mService= Network.getService(OrderService.class);
        freshData();

    }


    /***
     * 滑到顶部 数据刷新
     * @param ptrFrameLayout
     */
    @Override
    public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
        mPage=1;
        mIsAppend= false;
        freshData();
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View view, View view1) {
        return false;
    }

    /***
     *
     */
    private void freshData() {
        if(isSaled){
            mService.seller(mPage,new CallBackProxy());
        }else{
            mService.buyers(mPage,new CallBackProxy());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isSaled=getArguments().getBoolean("isSaled");
    }

    /***
     *
     */
    public final class ViewHolderClickListenerProxy implements OrderListAdapter.ViewHolderClickListener<OrderListAdapter.ViewHolder> {
        @Override
        public void onItemClick(OrderListAdapter.ViewHolder holder, int postion) {
            OrderInfo orderInfo=mAdapter.getItem(postion);
            OrderDetailActivity.show(getActivity(),orderInfo,mAdapter.isSaled());
        }

        @Override
        public void onClick(OrderListAdapter.ViewHolder holder, View clickItem, int postion) {
                OrderInfo info=mAdapter.getItem(postion);
                int status=Integer.parseInt(info.getStatus());
                String oid=info.getOid();
                OrderOperatorCallBackProxy proxy=new OrderOperatorCallBackProxy();
                if(holder.mBinding.acceptOrder==clickItem){
                    if(mAdapter.isSaled()){
                        //出售订单
                        if(status==2){
                            //接单
                            mService.acceptOrder(oid,proxy);
                        }else if(status==3){
                            //发货
                            mService.sendGoods(oid,proxy);
                        }
                    }else {
                        //购买订单
                        if(status==0){
                            //支付
                            PayWrapperActivity.show(getActivity(),info);
                        }else if(status==4){
                            //确认发货
                            mService.confirmOrder(oid,proxy);

                        }
                    }
                }
        }
    }

    /***
     *
     */
    public final class OrderOperatorCallBackProxy implements Callback<ToastResponse>{
        @Override
        public void start() {

        }

        @Override
        public void success(ToastResponse toastResponse, Response response) {
            if(toastResponse.getCode()==20000){
                Toast.makeText(getActivity(),toastResponse.getMsg(),Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void failure(HNetError hNetError) {

        }

        @Override
        public void end() {
            mPage=1;
            mIsAppend= false;
            freshData();
        }
    }

    /***
     *订单列表信息
     */
    public final class CallBackProxy implements Callback<OrderRepsonse>{
        @Override
        public void start() {

        }

        @Override
        public void success(OrderRepsonse orderRepsonse, Response response) {
            if(orderRepsonse.getCode()==20000){
                mAdapter.reload(orderRepsonse.getData().getList(),mIsAppend);
                mAdapter.notifyDataSetChanged();
            }

        }

        @Override
        public void failure(HNetError hNetError) {
            ErrorUtils.checkError(getActivity(),hNetError);
        }

        @Override
        public void end() {

        }
    }
}
