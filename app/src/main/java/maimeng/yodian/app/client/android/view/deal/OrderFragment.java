package maimeng.yodian.app.client.android.view.deal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.analytics.MobclickAgent;

import org.henjue.library.hnet.Callback;
import org.henjue.library.hnet.Response;
import org.henjue.library.hnet.exception.HNetError;
import org.parceler.Parcels;

import java.util.List;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.adapter.OrderListAdapter;
import maimeng.yodian.app.client.android.common.PullHeadView;
import maimeng.yodian.app.client.android.common.UEvent;
import maimeng.yodian.app.client.android.model.Lottery;
import maimeng.yodian.app.client.android.model.OrderInfo;
import maimeng.yodian.app.client.android.network.ErrorUtils;
import maimeng.yodian.app.client.android.network.Network;
import maimeng.yodian.app.client.android.network.response.OrderListRepsonse;
import maimeng.yodian.app.client.android.network.response.ToastResponse;
import maimeng.yodian.app.client.android.network.service.OrderService;
import maimeng.yodian.app.client.android.utils.LogUtil;
import maimeng.yodian.app.client.android.view.dialog.WaitDialog;
import maimeng.yodian.app.client.android.widget.EndlessRecyclerOnScrollListener;
import maimeng.yodian.app.client.android.widget.ListLayoutManager;

/**
 * Created by xujianhua on 9/29/15.
 */
public class OrderFragment extends Fragment implements PtrHandler {
    private boolean isSaled;
    private int mPage = 1;
    private PtrFrameLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private View mNoOrder;
    private EndlessRecyclerOnScrollListener mEndlessRecyclerOnScrollListener;
    private OrderListAdapter mAdapter;
    private OrderService mService;
    private boolean mIsAppend;
    private WaitDialog mWaitDialog;
    private static final int REQUEST_ORDER_BUY = 0x24;
    private static final int REQUEST_SKILL_DETAIL = 0x25;


    /***
     * @param activity
     * @param isSaled  是否是已出售订单
     * @param viewId   占位view的id
     */
    public static void show(AppCompatActivity activity, boolean isSaled, int viewId) {
        OrderFragment orderFragment = new OrderFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isSaled", isSaled);
        orderFragment.setArguments(bundle);
        activity.getSupportFragmentManager().beginTransaction().replace(viewId, orderFragment).commit();
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
        mRefreshLayout.setPtrHandler(this);
        StoreHouseHeader header = PullHeadView.create(getActivity()).setTextColor(0x0);
        mRefreshLayout.addPtrUIHandler(header);
        mRefreshLayout.setHeaderView(header);
        ListLayoutManager layout = new ListLayoutManager(getActivity());
        mEndlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(layout) {
            @Override
            public void onLoadMore() {
                mPage++;
                mIsAppend = true;
                freshData();
            }


        };
        mAdapter = new OrderListAdapter(getActivity(), new ViewHolderClickListenerProxy());
        mAdapter.setIsSaled(isSaled);
        mRecyclerView.setLayoutManager(layout);
        mRecyclerView.addOnScrollListener(mEndlessRecyclerOnScrollListener);
        mRecyclerView.setAdapter(mAdapter);

        mService = Network.getService(OrderService.class);
        mNoOrder = view.findViewById(R.id.no_order);
        mRefreshLayout.autoRefresh();

    }


    /***
     * 滑到顶部 数据刷新
     *
     * @param ptrFrameLayout
     */
    @Override
    public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
        mPage = 1;
        mIsAppend = false;
        freshData();
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View view, View view1) {
        return PtrDefaultHandler.checkContentCanBePulledDown(ptrFrameLayout, view, view1);
    }

    /***
     *
     */
    private void freshData() {
        if (isSaled) {
            mService.seller(mPage, new CallBackProxy());
        } else {
            mService.buyers(mPage, new CallBackProxy());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isSaled = getArguments().getBoolean("isSaled");
    }

    /***
     *
     */
    public final class ViewHolderClickListenerProxy implements OrderListAdapter.ViewHolderClickListener<OrderListAdapter.ViewHolder> {
        @Override
        public void onItemClick(OrderListAdapter.ViewHolder holder, int postion) {
            OrderInfo orderInfo = mAdapter.getItem(postion);
            OrderDetailActivity.show(OrderFragment.this, orderInfo, REQUEST_SKILL_DETAIL);
        }

        @Override
        public void onClick(OrderListAdapter.ViewHolder holder, View clickItem, int postion) {
            OrderInfo info = mAdapter.getItem(postion);
            int status = info.getStatus();
            long oid = info.getOid();
            OrderOperatorCallBackProxy proxy = new OrderOperatorCallBackProxy();
            if (holder.mBinding.acceptOrder == clickItem) {
                if (mAdapter.isSaled()) {
                    MobclickAgent.onEvent(getActivity(), UEvent.ORDER_SALED_CLICK);
                    //出售订单
                    if (status == 2) {
                        //接单
                        mService.acceptOrder(oid, proxy);
                    } else if (status == 3) {
                        //发货
                        mService.sendGoods(oid, proxy);
                    }
                } else {
                    //购买订单
                    MobclickAgent.onEvent(getActivity(), UEvent.ORDER_BUYED_CLICK);
                    if (status == 0) {
                        //支付
                        PayWrapperActivity.show(OrderFragment.this, info, REQUEST_ORDER_BUY);
                    } else if (status == 4) {
                        //确认发货
                        mService.confirmOrder(oid, proxy);

                    } else if (status == 2) {

                    }

                }
            }
        }
    }

    /***
     *
     */
    public final class OrderOperatorCallBackProxy implements Callback<ToastResponse> {
        @Override
        public void start() {
            mWaitDialog = WaitDialog.show(getActivity());
        }

        @Override
        public void success(ToastResponse toastResponse, Response response) {
            toastResponse.showMessage(getActivity());

        }

        @Override
        public void failure(HNetError hNetError) {
            ErrorUtils.checkError(getActivity(), hNetError);
        }

        @Override
        public void end() {
            mPage = 1;
            mIsAppend = false;
            freshData();
            if (mWaitDialog != null) {
                mWaitDialog.dismiss();
            }
        }
    }

    /***
     * 订单列表信息
     */
    public final class CallBackProxy implements Callback<OrderListRepsonse> {
        @Override
        public void start() {
            if(mWaitDialog==null){
                mWaitDialog=WaitDialog.show(getActivity());
            }
        }

        @Override
        public void success(OrderListRepsonse orderListRepsonse, Response response) {
            if (orderListRepsonse.getCode() == 20000) {
                List<OrderInfo> orders = orderListRepsonse.getData().getList();
                if (orders.size() == 0 && mPage == 1) {
                    mNoOrder.setVisibility(View.VISIBLE);
                    mRefreshLayout.setVisibility(View.GONE);
                } else {
                    mNoOrder.setVisibility(View.GONE);
                    mRefreshLayout.setVisibility(View.VISIBLE);
                    mAdapter.reload(orders, mIsAppend);
                    mAdapter.notifyDataSetChanged();
                }

            }

        }

        @Override
        public void failure(HNetError hNetError) {
            ErrorUtils.checkError(getActivity(), hNetError);
        }

        @Override
        public void end() {
            if(mWaitDialog!=null){
                mWaitDialog.dismiss();
            }
            mRefreshLayout.refreshComplete();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d(OrderFragment.class.getName(),"requestCode: %d,REQUEST_CODE: %d",requestCode,REQUEST_ORDER_BUY);
        if (resultCode == getActivity().RESULT_OK) {
            if (data != null) {
                if (requestCode == REQUEST_ORDER_BUY) {
                    mPage=1;
                    mIsAppend=false;
                    freshData();
                    Lottery lottery = Parcels.unwrap(data.getParcelableExtra("lottery"));
                    if (lottery.getIsLottery() == 1) {
                        OrderDetailActivity.show(getActivity(), lottery);
                    }
                } else if (requestCode == REQUEST_SKILL_DETAIL) {
                    if (data.getBooleanExtra("isOperator", false)) {
                        mPage = 1;
                        mIsAppend = false;
                        freshData();
                    }
                }
            }

        }
    }
}
