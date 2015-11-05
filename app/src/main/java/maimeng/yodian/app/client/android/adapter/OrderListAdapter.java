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
            String status = mOrder.getStatus();

            if (!TextUtils.isEmpty(status)) {
                String statusStr = null;
                String operatorStr = null;
                switch (Integer.parseInt(status)) {
                    case 0:
                        statusStr = mContext.getString(R.string.order_status_unpay);
                        if (!isSaled) {
                            operatorStr = mContext.getString(R.string.buyer_operator_pay);
                            mBinding.acceptOrder.setBackground(mContext.getResources().getDrawable(R.mipmap.btn_oval_bg_blue));
                        } else {
                            mBinding.acceptOrder.setVisibility(View.INVISIBLE);
                        }
                        break;
                    case 1:
                        //后台预留
                        statusStr = mContext.getString(R.string.order_status_delete);

                        break;
                    case 2:
                        statusStr = mContext.getString(R.string.order_status_payed);
                        if (!isSaled) {
                            operatorStr = mContext.getString(R.string.buyer_operator_wait_accept);
                            mBinding.acceptOrder.setBackground(mContext.getResources().getDrawable(R.mipmap.ic_oval_gray));
                        } else {
                            operatorStr = mContext.getString(R.string.seller_operator_accept);
                            mBinding.acceptOrder.setBackground(mContext.getResources().getDrawable(R.mipmap.btn_oval_bg_blue));
                        }
                        break;
                    case 3:
                        statusStr = mContext.getString(R.string.order_status_accept);
                        if (!isSaled) {
                            operatorStr = mContext.getString(R.string.buyer_operator_wait_send);
                            mBinding.acceptOrder.setBackground(mContext.getResources().getDrawable(R.mipmap.ic_oval_gray));
                        } else {
                            operatorStr = mContext.getString(R.string.seller_operator_send);
                            mBinding.acceptOrder.setBackground(mContext.getResources().getDrawable(R.mipmap.btn_oval_bg_blue));
                        }
                        break;
                    case 4:
                        statusStr = mContext.getString(R.string.order_status_send_goods);
                        if (!isSaled) {
                            operatorStr = mContext.getString(R.string.buyer_operator_confirm);
                            mBinding.acceptOrder.setBackground(mContext.getResources().getDrawable(R.mipmap.btn_oval_bg_blue));
                        } else {

                            mBinding.acceptOrder.setVisibility(View.INVISIBLE);
                        }
                        break;
                    case 5:
                        statusStr = mContext.getString(R.string.order_status_confirm_deal);
                        operatorStr = mContext.getString(R.string.order_status_confirm_deal);
                        mBinding.acceptOrder.setBackground(mContext.getResources().getDrawable(R.mipmap.ic_oval_gray));
                        break;
                    default:
                        statusStr = null;
                        break;
                }
                Skill skill = mOrder.getSkill();

                mBinding.orderStatus.setText(statusStr);
                Spanned html = Html.fromHtml(mContext.getString(R.string.order_total_fee));
                mBinding.orderPrice.setText(Html.fromHtml(mContext.getString(R.string.order_total_fee,
                        mOrder.getTotal_fee())));
                mBinding.skillPrice.setText(Html.fromHtml(mContext.getString(R.string.lable_price,
                        skill.getPrice(), skill.getUnit())));
                SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月 HH:mm");
                String dateStr = format.format(new java.util.Date(Long.parseLong(mOrder.getCreatetime()) * 1000));
                ;
                mBinding.orderTime.setText(dateStr);
                mBinding.acceptOrder.setText(operatorStr);
            }

        }
    }
}
