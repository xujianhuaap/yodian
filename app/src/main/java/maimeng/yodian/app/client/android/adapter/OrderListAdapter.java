package maimeng.yodian.app.client.android.adapter;

import android.app.Fragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.databinding.ItemOrderListBinding;
import maimeng.yodian.app.client.android.model.OrderInfo;
import maimeng.yodian.app.client.android.model.skill.Skill;

/**
 * Created by xujianhua on 9/29/15.
 */
public class OrderListAdapter extends AbstractAdapter<OrderInfo, OrderListAdapter.ViewHolder> {
    public OrderListAdapter(Context context, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(context, viewHolderClickListener);
    }

    public OrderListAdapter(Fragment fragment, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(fragment, viewHolderClickListener);
    }

    public OrderListAdapter(android.support.v4.app.Fragment fragment, ViewHolderClickListener<ViewHolder> viewHolderClickListener) {
        super(fragment, viewHolderClickListener);
    }

    private boolean isSaled;


    public boolean isSaled() {
        return isSaled;
    }

    public void setIsSaled(boolean isSaled) {
        this.isSaled = isSaled;
    }

    @Override
    public void delete(int position) {
        super.delete(position);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OrderInfo orderInfo = getItem(position);
        Skill skill = orderInfo.getSkill();
        holder.mBinding.setOrder(orderInfo);
        holder.mBinding.setSkill(skill);
        holder.bind(orderInfo);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.item_order_list, null);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        view.setLayoutParams(layoutParams);
        ItemOrderListBinding listBinding = DataBindingUtil.bind(view);
        return new ViewHolder(listBinding);
    }


    /***
     *
     */

    public final class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private OrderInfo mOrder;
        public final ItemOrderListBinding mBinding;

        public ViewHolder(ItemOrderListBinding listBinding) {
            super(listBinding.getRoot());
            mBinding = listBinding;
            mBinding.getRoot().setOnClickListener(this);
            mBinding.acceptOrder.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v == mBinding.getRoot()) {
                mViewHolderClickListener.onItemClick(this, getLayoutPosition());
            } else {
                mViewHolderClickListener.onClick(this, v, getLayoutPosition());
            }
        }

        /****
         * @param orderInfo
         */
        public void bind(OrderInfo orderInfo) {
            mOrder = orderInfo;
            mBinding.setBuyer(orderInfo.getBuyer());
            mBinding.setIsSaled(isSaled);
            int status = mOrder.getStatus();

                String statusStr = null;
                String operatorStr = null;
                mBinding.acceptOrder.setVisibility(View.VISIBLE);
                switch (status) {
                    case 0:
                        statusStr = mContext.getString(R.string.order_status_unpay);
                        if (!isSaled) {
                            operatorStr = mContext.getString(R.string.buyer_operator_pay);
                        } else {
                            mBinding.acceptOrder.setVisibility(View.GONE);
                        }
                        break;
                    case 1:
                        //后台预留
                        statusStr = mContext.getString(R.string.order_status_delete);

                        break;
                    case 2:
                        statusStr = mContext.getString(R.string.buyer_operator_wait_accept);
                        if (!isSaled) {
                            mBinding.acceptOrder.setVisibility(View.GONE);
                        } else {
                            operatorStr = mContext.getString(R.string.seller_operator_accept);
                        }
                        break;
                    case 3:
                        statusStr = mContext.getString(R.string.buyer_operator_wait_send);
                        if (!isSaled) {
                            mBinding.acceptOrder.setVisibility(View.GONE);
                        } else {
                            operatorStr = mContext.getString(R.string.seller_operator_send);
                        }
                        break;
                    case 4:
                        statusStr = mContext.getString(R.string.order_status_send_goods);
                        if (!isSaled) {
                            operatorStr = mContext.getString(R.string.buyer_operator_confirm);
                        } else {

                            mBinding.acceptOrder.setVisibility(View.GONE);
                        }
                        break;
                    case 5:
                        statusStr = mContext.getString(R.string.order_status_confirm_deal);
                        operatorStr = mContext.getString(R.string.order_status_confirm_deal);
                        mBinding.acceptOrder.setVisibility(View.GONE);
                        break;
                    case 6:
                        //订单关闭
                        if (isSaled) {
                            statusStr = mContext.getString(R.string.order_status_buyer_close);
                        } else {
                            statusStr = mContext.getString(R.string.order_status_close);
                        }

                        mBinding.acceptOrder.setVisibility(View.GONE);
                        break;
                    case 7:
                        //订单取消
                        if (isSaled) {
                            statusStr = mContext.getString(R.string.order_status_buyer_cancle);
                        } else {
                            statusStr = mContext.getString(R.string.order_status_cancle);
                        }
                        mBinding.acceptOrder.setVisibility(View.GONE);
                        break;
                    default:
                        statusStr = null;
                        break;
                }
                Skill skill = mOrder.getSkill();

                mBinding.orderStatus.setText(statusStr);
                Spanned html = Html.fromHtml(mContext.getString(R.string.order_total_fee));
                if (mOrder.getBalance() > 0) {
                    mBinding.orderBalance.setVisibility(View.VISIBLE);
                    mBinding.orderBalance.setText(Html.fromHtml(mContext.getString(R.string.order_total_fee_for_balance,
                            mOrder.getBalance() + "")));
                } else {
                    mBinding.orderBalance.setVisibility(View.GONE);
                }
                mBinding.orderPrice.setText(Html.fromHtml(mContext.getString(R.string.order_total_fee,
                        mOrder.getTotal_fee())));
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String dateStr = format.format(new java.util.Date(mOrder.getCreatetime() * 1000));
                ;
                mBinding.orderTime.setText(dateStr);
                mBinding.acceptOrder.setText(operatorStr);


        }
    }
}
